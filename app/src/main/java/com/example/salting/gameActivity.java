package com.example.salting;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firbasedao.Listeners.RetrievalEventListener;
import com.example.firbasedao.Listeners.TaskListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class gameActivity extends AppCompatActivity implements SensorEventListener{

    TextView emailTextView;
    ImageView photoImageView,salterImageView;
    Button signOutButton;
    Button saveButton;

    GoogleSignInAccount signInAccount;

    private static final float SHAKE_THRESHOLD = 3.25f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 500;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;
    TextView shakeCounterTextView;
    private int shakeCount = 0;
    private String friends = "";
    private String regDate = "";

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //progress betöltése:
        userDAOfb userDAOfb = new userDAOfb();
        userDAOfb.getAll(new RetrievalEventListener<List<userClass>>() {
            @Override
            public void OnDataRetrieved(List<userClass> userClasses) {
                if (userClasses.isEmpty()){
                    userClass usernew = new userClass();
                    userDAOfb userDAOfb = new userDAOfb();
                    usernew.email = signInAccount.getEmail();
                    usernew.name = signInAccount.getEmail().split("@")[0];
                    usernew.friends = "";
                    usernew.shakeCount = 0;
                    usernew.profPic = signInAccount.getPhotoUrl().toString();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    regDate = dateFormat.format(date);
                    usernew.regDate = regDate;
                    userDAOfb.save(usernew, userDAOfb.GetNewKey(), new TaskListener() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(getApplicationContext(), "Welcome to the game!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void OnFail() {
                            Toast.makeText(getApplicationContext(), "Failed to save!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                boolean useralreadyexists = false;
                for (userClass user : userClasses){
                    if (user.email.equals(signInAccount.getEmail())){
                        useralreadyexists = true;
                        friends = user.friends;
                        shakeCount = user.shakeCount;
                        regDate = user.regDate;
                        shakeCounterTextView.setText(""+shakeCount);
                    }
                }
                if(!useralreadyexists){
                    userClass usernew = new userClass();
                    userDAOfb userDAOfb = new userDAOfb();
                    usernew.email = signInAccount.getEmail();
                    usernew.name = signInAccount.getEmail().split("@")[0];
                    usernew.friends = "";
                    usernew.shakeCount = 0;
                    usernew.profPic = signInAccount.getPhotoUrl().toString();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    usernew.regDate = dateFormat.format(date);
                    System.out.println("ÚJ ACC: "+usernew.email + " "+ usernew.name + " "+usernew.friends+" "+usernew.shakeCount+" "+usernew.profPic);
                    userDAOfb.save(usernew, userDAOfb.GetNewKey(), new TaskListener() {
                        @Override
                        public void OnSuccess() {
                            Toast.makeText(getApplicationContext(), "Welcome to the game!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void OnFail() {
                            Toast.makeText(getApplicationContext(), "Failed to save!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        emailTextView = findViewById(R.id.emailTextView);
        photoImageView = findViewById(R.id.photoImageView);
        signOutButton = findViewById(R.id.signOutButton);
        saveButton = findViewById(R.id.saveButton);
        salterImageView = findViewById(R.id.salterImageView);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount!=null){
            emailTextView.setText(signInAccount.getEmail().split("@")[0]);
            Glide.with(this).load(String.valueOf(signInAccount.getPhotoUrl())).override(50,45).centerCrop().into(photoImageView);
        }

        shakeCounterTextView = findViewById(R.id.shakeCounterTextView);
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorMgr.registerListener((SensorEventListener) this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveShakeToDb();
            }
        });

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileClicked();
            }
        });

    }

    public void signOutButtonClicked(View view) {
        saveShakeToDb();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        saveShakeToDb();
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(exit);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    shakeCount++;
                    Animation shake,scale;
                    shake = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    scale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale);
                    salterImageView.startAnimation(shake);
                    shakeCounterTextView.startAnimation(scale);
                    shakeCounterTextView.setText(""+shakeCount);
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    public void saveShakeToDb() {

        DAOuser dao = new DAOuser();

        int id = 1;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("friends", friends);
        hashMap.put("shakeCount", shakeCount);

        //TODO
        //ezt megoldani a daouser.java-ban
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://salting-1635517386719-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = db.getReference(userClass.class.getSimpleName());
        databaseReference
                .orderByChild("email")
                .equalTo(signInAccount.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            dao.update(key, hashMap).addOnSuccessListener(suc ->
                            {
                                Toast.makeText(gameActivity.this, "Your progress is now saved.", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(fail ->
                            {
                                Toast.makeText(gameActivity.this, "" + fail.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void profileClicked() {
        saveShakeToDb();
        Intent intent = new Intent(getApplicationContext(),profileActivity.class);
        intent.putExtra("email",signInAccount.getEmail());
        intent.putExtra("friends",friends);
        intent.putExtra("shakeCount",shakeCount);
        intent.putExtra("regDate",regDate);
        intent.putExtra("pic",signInAccount.getPhotoUrl().toString());
        startActivity(intent);
    }
}



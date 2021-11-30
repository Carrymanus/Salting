package com.example.salting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firbasedao.Listeners.RetrievalEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class profileActivity extends AppCompatActivity {

    TextView profileRegDateTextView,profileShakeCountTextView,profileEmailTextView;
    ImageView profileProfPicImageView;

    String friends,email;

    Button returnButton,addFriendButton;

    RecyclerView recyclerView;
    recyclerViewAdapter adapter;
    ArrayList<userClass> list;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newFriendName;
    private Button newFriendAdd,newFriendAddCancel;

    public void removeFriend(String name){
        friends = friends.replace(" "+name,"");
        DAOuser daOuser = new DAOuser();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("friends",friends);
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://salting-1635517386719-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = db.getReference(userClass.class.getSimpleName());
        databaseReference
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String key = childSnapshot.getKey();
                            daOuser.update(key, hashMap).addOnSuccessListener(suc ->
                            {
                                Toast.makeText(profileActivity.this, "Friend succesfully removed!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(fail ->
                            {
                                Toast.makeText(profileActivity.this, "" + fail.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        recyclerView = findViewById(R.id.friendListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String[] friendArray = friends.split(" ");
        list = new ArrayList<>();
        adapter = new recyclerViewAdapter(this,list);
        recyclerView.setAdapter(adapter);
        userDAOfb userDAOfb = new userDAOfb();
        userDAOfb.getAll(new RetrievalEventListener<List<userClass>>() {
            @Override
            public void OnDataRetrieved(List<userClass> userClasses) {
                for (userClass user : userClasses){
                    for (int i = 0;i<friendArray.length;i++){
                        if (user.name.equals(friendArray[i])){
                            list.add(user);
                        }
                    }
                }
                Collections.sort(list, new Comparator<userClass>() {
                    @Override
                    public int compare(userClass userClass, userClass t1) {
                        if (userClass.shakeCount > t1.shakeCount)
                            return -1;
                        else
                            return 1;
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profileRegDateTextView = findViewById(R.id.profileRegDateTextView);
        profileShakeCountTextView = findViewById(R.id.profileShakeCountTextView);
        profileProfPicImageView = findViewById(R.id.profileProfPicImageView);
        returnButton = findViewById(R.id.returnButton);
        addFriendButton = findViewById(R.id.addFriendButton);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        friends = intent.getStringExtra("friends");
        String[] friendArray = friends.split(" ");
        int shakeCount = intent.getIntExtra("shakeCount",0);
        String regDate = intent.getStringExtra("regDate");
        String photoUrl = intent.getStringExtra("pic");

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent2 = new Intent(getApplicationContext(),gameActivity.class);
//                startActivity(intent2);
                  finish();
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder = new AlertDialog.Builder(profileActivity.this);
                final View popupView = getLayoutInflater().inflate(R.layout.popup,null);
                newFriendName = popupView.findViewById(R.id.newFriendName);
                newFriendAdd = popupView.findViewById(R.id.newFriendAdd);
                newFriendAddCancel = popupView.findViewById(R.id.newFriendAddCancel);

                dialogBuilder.setView(popupView);
                dialog = dialogBuilder.create();
                dialog.show();

                newFriendAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DAOuser daOuser = new DAOuser();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        friends = friends.concat(" "+newFriendName.getText());
                        hashMap.put("friends",friends);
                        FirebaseDatabase db = FirebaseDatabase.getInstance("https://salting-1635517386719-default-rtdb.europe-west1.firebasedatabase.app");
                        DatabaseReference databaseReference = db.getReference(userClass.class.getSimpleName());
                        databaseReference
                                .orderByChild("email")
                                .equalTo(email)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            daOuser.update(key, hashMap).addOnSuccessListener(suc ->
                                            {
                                                Toast.makeText(profileActivity.this, "Friend succesfully added!", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(fail ->
                                            {
                                                Toast.makeText(profileActivity.this, "" + fail.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                        userDAOfb userDAOfb = new userDAOfb();
                        userDAOfb.getAll(new RetrievalEventListener<List<userClass>>() {
                            @Override
                            public void OnDataRetrieved(List<userClass> userClasses) {
                                for (userClass user : userClasses){
                                        if (user.name.equals(newFriendName.getText().toString())){
                                            list.add(user);
                                        }
                                }
                                Collections.sort(list, new Comparator<userClass>() {
                                    @Override
                                    public int compare(userClass userClass, userClass t1) {
                                        if (userClass.shakeCount > t1.shakeCount)
                                            return -1;
                                        else
                                            return 1;
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }
                        });
                        dialog.dismiss();
                    }
                });

                newFriendAddCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        Glide.with(this).load(photoUrl).override(90,80).centerCrop().into(profileProfPicImageView);
        profileShakeCountTextView.setText(""+shakeCount);
        profileEmailTextView.setText(email.split("@")[0]);
        profileRegDateTextView.setText(regDate);


        recyclerView = findViewById(R.id.friendListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new recyclerViewAdapter(this,list);
        recyclerView.setAdapter(adapter);

        userDAOfb userDAOfb = new userDAOfb();
        userDAOfb.getAll(new RetrievalEventListener<List<userClass>>() {
            @Override
            public void OnDataRetrieved(List<userClass> userClasses) {
                for (userClass user : userClasses){
                    for (int i = 0;i<friendArray.length;i++){
                        if (user.name.equals(friendArray[i])){
                            list.add(user);
                        }
                    }
                }
                Collections.sort(list, new Comparator<userClass>() {
                    @Override
                    public int compare(userClass userClass, userClass t1) {
                        if (userClass.shakeCount > t1.shakeCount)
                            return -1;
                        else
                            return 1;
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }
}
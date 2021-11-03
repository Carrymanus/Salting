package com.example.salting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class profileActivity extends AppCompatActivity {

    TextView profileRegDateTextView,profileShakeCountTextView,profileEmailTextView;
    ImageView profileProfPicImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profileRegDateTextView = findViewById(R.id.profileRegDateTextView);
        profileShakeCountTextView = findViewById(R.id.profileShakeCountTextView);
        profileProfPicImageView = findViewById(R.id.profileProfPicImageView);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String friends = intent.getStringExtra("friends");
        int shakeCount = intent.getIntExtra("shakeCount",0);
        String regDate = intent.getStringExtra("regDate");
        String photoUrl = intent.getStringExtra("pic");

        Glide.with(this).load(photoUrl).override(90,80).into(profileProfPicImageView);
        profileShakeCountTextView.setText("Shake count: "+shakeCount);
        profileEmailTextView.setText(email.split("@")[0]);
        profileRegDateTextView.setText("Reg. date: "+regDate);
    }
}
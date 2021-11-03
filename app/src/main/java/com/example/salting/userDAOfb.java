package com.example.salting;

import com.example.firbasedao.Listeners.RetrievalEventListener;
import com.google.firebase.database.DataSnapshot;

public class userDAOfb extends com.example.firbasedao.FirebaseDao<userClass>{

    public userDAOfb() {
        super("userClass");
    }

    protected void parseDataSnapshot(DataSnapshot dataSnapshot, RetrievalEventListener<userClass> retrievalEventListener) {
        final userClass user = new userClass();
        user.name = dataSnapshot.child("name").getValue().toString();
        user.email = dataSnapshot.child("email").getValue().toString();
        user.friends = dataSnapshot.child("friends").getValue().toString();
        user.shakeCount = Integer.parseInt(dataSnapshot.child("shakeCount").getValue().toString());
        user.profPic = dataSnapshot.child("profPic").getValue().toString();
        user.regDate = dataSnapshot.child("regDate").getValue().toString();
        retrievalEventListener.OnDataRetrieved(user);
    }
}

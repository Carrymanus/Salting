package com.example.salting;

import android.content.Intent;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAOuser{

    String foundKey;

    public int getShakesUber() {
        return shakesUber;
    }

    int shakesUber = -1;

    private DatabaseReference databaseReference;
    public DAOuser(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://salting-1635517386719-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = db.getReference(userClass.class.getSimpleName());
    }
    public Task<Void> add(userClass user){
        return databaseReference.push().setValue(user);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

    public Task<Void> getUserFriends(userClass user){
        String friends = user.getFriends();
        System.out.println(friends);
        return null;
    }

    public Task<Void> getAllData(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectEmails((Map<String,Object>) snapshot.getValue());
            }

            private void collectEmails(Map<String, Object> value) {
                ArrayList<String> emails = new ArrayList<>();
                for (Map.Entry<String, Object> entry : value.entrySet()){
                    Map singleUser = (Map) entry.getValue();
                    emails.add((String) singleUser.get("email"));
                }
                System.out.println(emails.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return null;
    }



    public interface SimpleCallback<S> {
        void callback(Object data);
    }

    public void setFoundKey(String foundKey) {
        this.foundKey = foundKey;
    }

    public String getUserKeyByEmail(String email, SimpleCallback<String> finishedCallback){

        String foundKey = "";
        ArrayList<String> list = new ArrayList<String>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                collectKey((Map<String,Object>) snapshot.getValue());
            }

            private void collectKey(Map<String, Object> value) {
                for (Map.Entry<String,Object> entry : value.entrySet()){
                    Map singleUser = (Map) entry.getValue();
                    if (singleUser.get("email").equals(email)){
                        finishedCallback.callback(value.entrySet().toString().split("\\[")[1].split("=")[0]);
                        setFoundKey(value.entrySet().toString().split("\\[")[1].split("=")[0]);
                        list.add(value.entrySet().toString().split("\\[")[1].split("=")[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return list.toString();

    }

    public void updateByEmail(String email, HashMap<String, Object> hashMap, SimpleCallback<String> finishedCallback){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectKey((Map<String,Object>) snapshot.getValue());
            }

            private void collectKey(Map<String, Object> value) {
                for (Map.Entry<String,Object> entry : value.entrySet()){
                    Map singleUser = (Map) entry.getValue();
                    if (singleUser.get("email").equals(email)){
                        databaseReference.child(value.entrySet().toString().split("\\[")[1].split("=")[0]).updateChildren(hashMap);
                        finishedCallback.callback(value.entrySet().toString().split("\\[")[1].split("=")[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public int getUserShakes(String email){
        final int[] shakeSuper = new int[1];
        databaseReference
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectShake((Map<String,Object>) dataSnapshot.getValue());
                    }

                    private void collectShake(Map<String, Object> value) {
                        int shakes = -1;
                        for (Map.Entry<String, Object> entry : value.entrySet()){
                            Map singleUser = (Map) entry.getValue();
                            shakes = Integer.parseInt(singleUser.get("shakeCount").toString());
                        }
                        shakeSuper[0] = shakes;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        shakesUber = shakeSuper[0];
        return shakeSuper[0];
    }

}

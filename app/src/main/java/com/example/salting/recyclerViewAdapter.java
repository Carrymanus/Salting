package com.example.salting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.ViewHolder> {

    Context context;

    ArrayList<userClass> list;

    public recyclerViewAdapter(Context context,ArrayList<userClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend,parent,false);
        return new ViewHolder(v);
    }

    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        userClass user = list.get(position);
        holder.friendSpotTextView.setText(position+1+".");
        holder.friendNameTextView.setText(capitalize(user.name));
        holder.friendRegDateTextView.setText(user.regDate);
        holder.friendShakeCountTextView.setText(""+user.shakeCount);
        Glide.with(context).load(user.getProfPic()).override(90,80).into(holder.friendPicImageView);
        holder.removeFriendImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(context instanceof profileActivity){
                            ((profileActivity)context).removeFriend(user.name);
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView friendNameTextView,friendShakeCountTextView,friendRegDateTextView,friendSpotTextView;
        ImageView friendPicImageView,removeFriendImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            friendSpotTextView = itemView.findViewById(R.id.friendSpotTextView);
            friendNameTextView = itemView.findViewById(R.id.friendNameTextView);
            friendShakeCountTextView = itemView.findViewById(R.id.friendShakeCountTextView);
            friendRegDateTextView = itemView.findViewById(R.id.friendRegDateTextView);
            friendPicImageView = itemView.findViewById(R.id.friendPicImageView);
            removeFriendImageView = itemView.findViewById(R.id.removeFriendImageView);


        }
    }{

    }
}

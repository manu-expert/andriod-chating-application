package com.example.wechat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class useradapter extends RecyclerView.Adapter<useradapter.viewholder> {


    MainActivity mainActivity;
    ArrayList<User> arrayList;


    public useradapter(MainActivity mainActivity, ArrayList<User> arrayList) {
        this.mainActivity = mainActivity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public useradapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull useradapter.viewholder holder, int position) {

        User user = arrayList.get(position);


        holder.username.setText(user.getUserName());
        holder.userstatus.setText(user.getStatus());


        String profilePicUrl = user.getProfilePic();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {

            Picasso.get().load(profilePicUrl).into(holder.userimg);
        } else {

            holder.userimg.setImageResource(R.drawable.ic_launcher_foreground);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mainActivity, ChatWin.class);

                // Pass user details to the ChatWin
                intent.putExtra("nameee", user.getUserName());
                intent.putExtra("receiverimg", user.getProfilePic());
                intent.putExtra("uid", user.getUserId());


                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    // ViewHolder class to hold references to the views in each item of the RecyclerView
    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username, userstatus;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.userName);
            userstatus = itemView.findViewById(R.id.userstatus);
        }
    }
}

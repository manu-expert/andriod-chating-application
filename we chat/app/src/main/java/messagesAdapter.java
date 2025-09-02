package com.example.wechat;

import static com.example.wechat.ChatWin.Receiver_img;
import static com.example.wechat.ChatWin.Sender_img;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class messagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList <msgModelclass>msgsadapterarraylist;


    int item_send = 1;
    int item_receive = 2;

    // Constructor
    public messagesAdapter(ArrayList<msgModelclass> msgsadapterarraylist, Context context) {
        this.msgsadapterarraylist = msgsadapterarraylist;
        this.context = context;
        Log.d("MessagesAdapter", "Adapter initialized with " + msgsadapterarraylist.size() + " messages.");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == item_send) {
            Log.d("MessagesAdapter", "Inflating sender_layout.");
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewholder(view);
        } else {
            Log.d("MessagesAdapter", "Inflating receiver_layout.");
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new receiverViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages = msgsadapterarraylist.get(position);
        Log.d("MessagesAdapter", "Binding message at position " + position + ": " + messages.getMessage());

        if (holder.getClass() == senderViewholder.class) {
            senderViewholder viewholder = (senderViewholder) holder;
            viewholder.msgText.setText(messages.getMessage());

            // FIX: Add null/empty check for Sender_img before loading with Picasso
            if (Sender_img != null && !Sender_img.isEmpty()) {
                Picasso.get().load(Sender_img)
                        .placeholder(R.drawable.ic_launcher_background) // Fallback while loading
                        .error(R.drawable.ic_launcher_foreground)      // Fallback on error
                        .into(viewholder.circleimg);
                Log.d("MessagesAdapter", "Loading Sender_img: " + Sender_img);
            } else {
                Log.d("MessagesAdapter", "Sender_img is null or empty. Using placeholder.");
                viewholder.circleimg.setImageResource(R.drawable.ic_launcher_background); // Fallback placeholder
            }
        } else {
            receiverViewholder viewholder = (receiverViewholder) holder;
            viewholder.msgText.setText(messages.getMessage());

            // FIX: Add null/empty check for Receiver_img before loading with Picasso
            if (Receiver_img != null && !Receiver_img.isEmpty()) {
                Picasso.get().load(Receiver_img)
                        .placeholder(R.drawable.ic_launcher_background) // Fallback while loading
                        .error(R.drawable.ic_launcher_foreground)      // Fallback on error
                        .into(viewholder.circleimg);
                Log.d("MessagesAdapter", "Loading Receiver_img: " + Receiver_img);
            } else {
                Log.d("MessagesAdapter", "Receiver_img is null or empty. Using placeholder.");
                viewholder.circleimg.setImageResource(R.drawable.ic_launcher_background); // Fallback placeholder
            }
        }
    }

    @Override
    public int getItemCount() {

        int count = msgsadapterarraylist.size();
        Log.d("MessagesAdapter", "getItemCount called, returning: " + count);
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass messages = msgsadapterarraylist.get(position);
        // FIX: Use .equals() for String comparison, not ==
        // Also, add a null check for FirebaseAuth.getInstance().getUid()
        String currentUid = FirebaseAuth.getInstance().getUid();
        if (currentUid != null && currentUid.equals(messages.getSenderID())) {
            Log.d("MessagesAdapter", "Item " + position + " is sender type.");
            return item_send;
        } else {
            Log.d("MessagesAdapter", "Item " + position + " is receiver type.");
            return item_receive;
        }
    }

    // ViewHolder for sender's messages
    class senderViewholder extends RecyclerView.ViewHolder {
        CircleImageView circleimg;
        TextView msgText;

        public senderViewholder(@NonNull View itemView) {
            super(itemView);
            circleimg = itemView.findViewById(R.id.profilerggg); // Your image ID
            msgText = itemView.findViewById(R.id.msgsendertyp); // Your textview ID
            Log.d("MessagesAdapter", "Sender ViewHolder created.");
        }
    }

    // ViewHolder for receiver's messages
    class receiverViewholder extends RecyclerView.ViewHolder {
        CircleImageView circleimg;
        TextView msgText;

        public receiverViewholder(@NonNull View itemView) {
            super(itemView);
            circleimg = itemView.findViewById(R.id.pro); // Your image ID
            msgText = itemView.findViewById(R.id.recivertextset); // Your textview ID
            Log.d("MessagesAdapter", "Receiver ViewHolder created.");
        }
    }
}

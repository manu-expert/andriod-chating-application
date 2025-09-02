package com.example.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatWin extends AppCompatActivity {
    // Declare Firebase instances
    FirebaseAuth firebaseauth;
    FirebaseDatabase database;

    // Variables for current user and receiver details
    String receiverimg, receiverUid, receiverName, SenderUID;

    // UI elements
    CircleImageView profile;
    TextView Receivernname;
    CardView sendbtn;
    EditText textmsg;
    RecyclerView mmmsgadapter;

    // Adapter and list for messages
    messagesAdapter messagesAdapter;
    ArrayList<msgModelclass> msgsArraylist;

    // Chat room identifiers
    public static String Sender_img; // Static fields for images (be careful with static in Activities)
    public static String Receiver_img;
    String senderRoom, receiverRoom; // Dynamic chat room IDs

    // Define DatabaseReference variables at class level so they are accessible
    DatabaseReference senderChatRef;
    DatabaseReference receiverChatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);


        firebaseauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Get data from the Intent (receiver details from useradapter)
        receiverName = getIntent().getStringExtra("nameee");
        receiverimg = getIntent().getStringExtra("receiverimg");
        receiverUid = getIntent().getStringExtra("uid");


        msgsArraylist = new ArrayList<>();
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        mmmsgadapter = findViewById(R.id.msgAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mmmsgadapter.setLayoutManager(layoutManager);

        // Initialize and set adapter for messages
        messagesAdapter = new messagesAdapter(msgsArraylist, ChatWin.this);
        mmmsgadapter.setAdapter(messagesAdapter);


        SenderUID = firebaseauth.getUid();
        Log.d("ChatWin", "SenderUID: " + SenderUID);



        senderRoom = SenderUID + receiverUid;
        receiverRoom = receiverUid + SenderUID;
        Log.d("ChatWin", "SenderRoom: " + senderRoom + ", ReceiverRoom: " + receiverRoom);



        senderChatRef = database.getReference().child("chats").child(senderRoom).child("messages");
        receiverChatRef = database.getReference().child("chats").child(receiverRoom).child("messages");

        Log.d("ChatWin", "Listening to path: " + senderChatRef.toString());



        senderChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ChatWin", "onDataChange triggered. Snapshot exists: " + snapshot.exists() + ", Children count: " + snapshot.getChildrenCount());
                msgsArraylist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass message = dataSnapshot.getValue(msgModelclass.class);
                    if (message != null) {
                        msgsArraylist.add(message);
                        Log.d("ChatWin", "Message added to list: " + message.getMessage() + " from " + message.getSenderID() + " Timestamp: " + message.getTimestamp());
                    } else {
                        Log.w("ChatWin", "Skipping null message from snapshot: " + dataSnapshot.getKey());
                    }
                }
                messagesAdapter.notifyDataSetChanged();
                if (!msgsArraylist.isEmpty()) {
                    mmmsgadapter.scrollToPosition(msgsArraylist.size() - 1);
                }
                Log.d("ChatWin", "RecyclerView updated. Total messages: " + msgsArraylist.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatWin", "Error loading messages (onCancelled): " + error.getMessage(), error.toException());
                Toast.makeText(ChatWin.this, "Error loading messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize Profile Image and Receiver Name in toolbar
        profile = findViewById(R.id.profileimg);
        Receivernname = findViewById(R.id.receiverName);
        Receivernname.setText("" + receiverName);

        // FIX: Add null/empty check for receiverimg before Picasso loads it
        if (receiverimg != null && !receiverimg.isEmpty()) {
            Picasso.get().load(receiverimg).into(profile);
        } else {
            profile.setImageResource(R.drawable.ic_launcher_background); // Ensure profile_placeholder drawable exists
        }


        // Fetch current sender's profile image (for local display or sending)
        DatabaseReference senderUserRef = database.getReference().child("users").child(firebaseauth.getUid());
        senderUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profilepic").exists() && snapshot.child("profilepic").getValue() != null) {
                    Sender_img = snapshot.child("profilepic").getValue().toString();
                    Log.d("ChatWin", "Sender_img fetched: " + Sender_img);
                } else {
                    Sender_img = "";
                    Log.d("ChatWin", "Sender_img not found in DB or null, setting to empty string.");
                }
                Receiver_img = receiverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatWin", "Error fetching sender profile: " + error.getMessage(), error.toException());
                Toast.makeText(ChatWin.this, "Error fetching sender profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatWin.this, "Enter Message first", Toast.LENGTH_SHORT).show();
                    return;
                }

                textmsg.setText("");

                Date date = new Date();
                long timestamp = date.getTime();

                msgModelclass messagess = new msgModelclass(message, SenderUID, timestamp);

                // Push message to sender's chat path
                senderChatRef.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ChatWin", "Message pushed to sender's chat path successfully.");

                            receiverChatRef.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("ChatWin", "Message pushed to receiver's chat path successfully.");
                                    } else {
                                        Log.e("ChatWin", "Failed to push message to receiver's chat path: " + task.getException().getMessage());
                                        Toast.makeText(ChatWin.this, "Failed to send message to receiver: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.e("ChatWin", "Failed to push message to sender's chat path: " + task.getException().getMessage());
                            Toast.makeText(ChatWin.this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

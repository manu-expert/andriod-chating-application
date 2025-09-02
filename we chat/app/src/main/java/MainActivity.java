package com.example.wechat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Firebase instances
    FirebaseAuth auth;
    FirebaseDatabase database;


    RecyclerView mainUserRecyclerView;
    useradapter adapter;
    ArrayList<User> arrayList;
    ImageView imglogout;
    Button YES, NO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        // if Firebase User is not logged in.
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
            return;
        }


        imglogout = findViewById(R.id.logoutimg);
        mainUserRecyclerView = findViewById(R.id.mainuserrecyclerview); // FIX: Correctly assign findViewById result


        arrayList = new ArrayList<>();


        adapter = new useradapter(MainActivity.this, arrayList);

        // Set up the RecyclerView's layout manager and adapter
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecyclerView.setAdapter(adapter);


        DatabaseReference usersRef = database.getReference().child("users");


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // FIX: Clear the ArrayList to prevent duplicates on subsequent data changes
                arrayList.clear();
                // Iterate through each user's data in the snapshot
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Convert the Firebase data (JSON) into a User object
                    User user = dataSnapshot.getValue(User.class);
                    // Add the User object to the list, but only if it's not the current user
                    // FIX: Prevent adding the current user to their own contact list
                    // And ensure 'user' is not null (getValue can return null if data doesn't match User.class)
                    if (user != null && auth.getCurrentUser() != null && !user.getUserId().equals(auth.getCurrentUser().getUid())) {
                        arrayList.add(user);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // FIX: Handle database errors (e.g., security rules, network issues)
                Toast.makeText(MainActivity.this, "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the logout confirmation dialog
                Dialog dialog = new Dialog(MainActivity.this, R.style.dialogue); // Assuming R.style.dialogue exists
                dialog.setContentView(R.layout.dialogue_layout); // Assuming dialogue_layout.xml exists


                YES = dialog.findViewById(R.id.yesbtn);
                NO = dialog.findViewById(R.id.nobtn);


                YES.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, login.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });


                NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Just close the dialog
                    }
                });
                dialog.show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

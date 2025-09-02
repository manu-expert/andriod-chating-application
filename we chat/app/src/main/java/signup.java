package com.example.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri; // Keep this import, it's correct!
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

// REMOVE THIS LINE: import java.net.URI; // This was the problematic import

import de.hdodenhof.circleimageview.CircleImageView;


public class signup extends AppCompatActivity {
    EditText email, username, password,confirmpassword;
    FirebaseAuth auth;
    Button signup;
    TextView logintext;
    CircleImageView profile;
    FirebaseDatabase database;
    FirebaseStorage storage;
    android.net.Uri imageURI;
     String Image;
    String emailPattern="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        email=findViewById(R.id.signupemail);
        username=findViewById(R.id.signupusername);
        password=findViewById(R.id.signuppassword);
        confirmpassword=findViewById(R.id.signupconfirmpassword);
        logintext=findViewById(R.id.logtext);
        profile =findViewById(R.id.signupprofile);
        signup=findViewById(R.id.signupbutton);
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name= username.getText().toString();
                String Email=email.getText().toString();
                String Password= password.getText().toString();
                String CPassword=confirmpassword.getText().toString();
                String Status="Hello! I am Using We chat";
                if (TextUtils.isEmpty(Name)||TextUtils.isEmpty(Email)||TextUtils.isEmpty(Password)||TextUtils.isEmpty(CPassword)){
                    Toast.makeText(signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!Email.matches(emailPattern)){
                    Toast.makeText(signup.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
                else if(Password.length()<6){
                    Toast.makeText(signup.this, "Password must be more than six characters ", Toast.LENGTH_SHORT).show();
                }
                else if (!Password.equals(CPassword)){
                    Toast.makeText(signup.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference=database.getReference().child("users").child(id);
                                StorageReference storageReference=storage.getReference().child("upload").child(id);
                                if (imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Image=uri.toString();
                                                        User user=new User(Email,Password,id,Name,Image,Status);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Intent intent = new Intent(signup.this, login.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else {
                                                                    Toast.makeText(signup.this, "Error While Creating your Account!", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else{
                                    Image="";
                                    User user=new User(Email,Password,id,Name,Image,Status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(signup.this, login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(signup.this, "Error While Creating your Account!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }

                            }else{
                                Toast.makeText(signup.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT); // Changed 'intent.ACTION_GET_CONTENT' to 'Intent.ACTION_GET_CONTENT'
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageURI= data.getData();
                profile.setImageURI(imageURI);
            }

        }
    }
}

package com.example.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email, password;
    Button button;
    TextView signuptext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        auth= FirebaseAuth.getInstance();
        button=findViewById(R.id.logbutton);
        email=findViewById(R.id.logemail);
        password=findViewById(R.id.logpassword);
        signuptext=findViewById(R.id.signuptext);
        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(login.this,signup.class);
                startActivity(intent);
                finish();
            }
        });
        String emailPattern="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email= email.getText().toString();
                String Password= password.getText().toString();
                if ((TextUtils.isEmpty(Email))){
                    Toast.makeText(login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if((TextUtils.isEmpty(Password))){
                    Toast.makeText(login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailPattern)) {
                    email.setError("Invalid Email Address");
                } else if (Password.length()<6) {

                    Toast.makeText(login.this, "Password must be more than 6 characters", Toast.LENGTH_SHORT).show();

                }
                else{
                    auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                try {
                                    Intent intent= new Intent(login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }}else{
                                Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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
    }
}
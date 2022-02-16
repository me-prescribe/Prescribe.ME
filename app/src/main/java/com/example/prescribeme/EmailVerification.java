package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    TextView emailVerify;
    Button btnVerify, btnSignOut;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        Intent verifyInt=getIntent();

        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        emailVerify=(TextView) findViewById(R.id.EmailVerify);
        emailVerify.setText(user.getEmail());

        btnVerify=(Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(v -> user.sendEmailVerification() //Send Email Verification to User when user clicks on Verify Email Button
                .addOnCompleteListener(EmailVerification.this, task -> {
                    if(task.isSuccessful())
                        Toast.makeText(EmailVerification.this, "Email Verification Sent Successful!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(EmailVerification.this, "There was an Error Sending Verification Message! Please Sign In Again", Toast.LENGTH_SHORT).show();
                    signUserOut();
                }));

        btnSignOut=(Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> signUserOut());
    }

    private void signUserOut() {
        mAuth.signOut();
        Toast.makeText(EmailVerification.this, "Logout Successful",Toast.LENGTH_SHORT).show();
        Intent loginInt=new Intent(EmailVerification.this, LoginActivity.class);
        loginInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginInt);
    }
}
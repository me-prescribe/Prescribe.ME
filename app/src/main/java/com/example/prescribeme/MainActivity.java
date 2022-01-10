package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView txtDrName;
    Button btnPrescribe, btnSignOut;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Receiving Intent from Login or Register Activity
        Intent mainint=getIntent();

        mAuth= FirebaseAuth.getInstance(); //Firebase Instance
        user=mAuth.getCurrentUser(); //Getting Currently Signed In User

        txtDrName=(TextView)findViewById(R.id.txtDrName);
        txtDrName.setText("Dr. " + user.getEmail()); //Displaying Email Address of Doctor(User)

        btnPrescribe=(Button)findViewById(R.id.btnPrescribe);
        btnPrescribe.setOnClickListener(view -> Toast.makeText(MainActivity.this, "Coming Soon! Please Wait!", Toast.LENGTH_LONG).show());

        btnSignOut=(Button)findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut(); //Signing Out using Firebase
            Toast.makeText(MainActivity.this, "Logout Successful",Toast.LENGTH_SHORT).show();
            //Creating Intent to go back to Login
            Intent loginint=new Intent(MainActivity.this, LoginActivity.class);
            //Flags are set so that if user tries to go 'Back' he can't come back to Main Activity
            //i.e. Once a user is logged out, he stays logged out till he signs in again
            loginint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginint);
        });
    }
}
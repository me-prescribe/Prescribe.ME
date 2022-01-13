package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView txtDrName;
    Button btnPrescribe, btnSignOut, btnGoUpdate;
    ImageView light, dark;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting preferred Mode (Light/Dark)
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false); //Maintains if Dark Mode is on/off

        //Receiving Intent from Login or Register Activity
        Intent mainint=getIntent();

        mAuth= FirebaseAuth.getInstance(); //Firebase Instance
        user=mAuth.getCurrentUser(); //Getting Currently Signed In User

        txtDrName=(TextView)findViewById(R.id.txtDrName);
        txtDrName.setText("Dr. " + user.getDisplayName()); //Displaying Name of Doctor(User)

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

        btnGoUpdate=(Button) findViewById(R.id.btnGoUpdate);
        btnGoUpdate.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UpdateProfile.class))); //Will lead user to Update Profile Class

        light=(ImageView) findViewById(R.id.LightMode); //Sun Image to show Light/Day Mode
        light.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Turns off Dark Mode
            editor.putBoolean("isDarkModeOn", false); //set Dark Mode to false on selection
            editor.apply();
        });

        dark=(ImageView) findViewById(R.id.DarkMode); //Moon Image to show Dark/Night Mode
        dark.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //Turns on Dark Mode
            editor.putBoolean("isDarkModeOn", true); //set Dark Mode to true on selection
            editor.apply();
        });

        //initial mode when launched
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //YES to Dark Mode
            light.setVisibility(View.VISIBLE); //Will show option to switch to Light Mode
            dark.setVisibility(View.GONE);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //NO to Dark Mode
            dark.setVisibility(View.VISIBLE); //Will show option to switch to Light Mode
            light.setVisibility(View.GONE);
        }
    }
}
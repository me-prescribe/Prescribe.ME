package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView txtDrName;
    Button btnPrescribe, btnSignOut, btnView, btnSign;
    CardView mainMenuCV;
    LinearLayout BackLL, AboutLL, DarkLL, LightLL, MenuLL, PrivacyLL, Paper2021LL, Paper2022LL, FeedbackLL, WebsiteLL, SignOutLL;

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
        Intent mainInt=getIntent();

        mAuth= FirebaseAuth.getInstance(); //Firebase Instance
        user=mAuth.getCurrentUser(); //Getting Currently Signed In User

        txtDrName=(TextView)findViewById(R.id.txtDrName);
        txtDrName.setText("Dr. " + user.getDisplayName()); //Displaying Name of Doctor(User)

        btnPrescribe=(Button)findViewById(R.id.btnPrescribe);
        btnPrescribe.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PatientInfo.class))); //Directs User to PatientInfo Activity

        btnSignOut=(Button)findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            mAuth.signOut(); //Signing Out using Firebase
            Toast.makeText(MainActivity.this, "Logout Successful",Toast.LENGTH_SHORT).show();
            //Creating Intent to go back to Login
            Intent loginInt=new Intent(MainActivity.this, LoginActivity.class);
            //Flags are set so that if user tries to go 'Back' he can't come back to Main Activity
            //i.e. Once a user is logged out, he stays logged out till he signs in again
            loginInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginInt);
        });

        btnView=(Button) findViewById(R.id.btnViewProfile);
        btnView.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Loading Profile Details", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, ViewProfile.class));
        }); //Will lead user to View Profile Class

        btnSign=(Button) findViewById(R.id.btnViewSign);
        btnSign.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Loading Signature", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, UpdateSignature.class));
        }); //Will lead user to Update Signature Class

        mainMenuCV = (CardView) findViewById(R.id.MainMenuCV); //References the CardView of Menu
        MenuLL = (LinearLayout) findViewById(R.id.MenuLL); //LinearLayout which shows icon & text of 'Menu'
        MenuLL.setOnClickListener(v -> mainMenuCV.setVisibility(View.VISIBLE)); //When Menu LL is clicked display CardView

        DarkLL=(LinearLayout) findViewById(R.id.DarkLL); //LinearLayout of Dark Mode Option
        LightLL=(LinearLayout) findViewById(R.id.LightLL); //LinearLayout of Light Mode Option
        AboutLL=(LinearLayout) findViewById(R.id.AboutLL); //LinearLayout of About Us Option
        PrivacyLL=(LinearLayout) findViewById(R.id.PrivacyLL); //LinearLayout of Privacy Policy Option
        WebsiteLL=(LinearLayout) findViewById(R.id.WebsiteLL); //LinearLayout of Website Option
        BackLL=(LinearLayout) findViewById(R.id.BackMenuLL); //LinearLayout of Back Option
        FeedbackLL=(LinearLayout) findViewById(R.id.FeedBackLL); //LinearLayout of Feedback Option
        Paper2021LL=(LinearLayout) findViewById(R.id.Paper2021LL); //LinearLayout of Paper 2021 Option
        Paper2022LL=(LinearLayout) findViewById(R.id.Paper2022LL); //LinearLayout of Paper 2022 Option
        SignOutLL=(LinearLayout) findViewById(R.id.SignOutLL); //LinearLayout of Sign Out Option
        /**All Linear Layouts have an Icon & a Text*/
        //Initial Dark Mode detection
        if(isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //Setting App Theme to Night Mode
            DarkLL.setVisibility(View.GONE); //Dark Mode LL will not be shown
            LightLL.setVisibility(View.VISIBLE); //Light Mode LL will be shown
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Setting App Theme to Day Mode
            DarkLL.setVisibility(View.VISIBLE); //Dark Mode LL will not be shown
            LightLL.setVisibility(View.GONE); //Light Mode LL will not be shown
        }
        DarkLL.setOnClickListener(v -> { //When Dark Mode LL is clicked
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //Switch App Theme to Night Mode
            editor.putBoolean("isDarkModeOn", true); //Change isDarkMode boolean to true
            editor.apply(); //Apply Changes
        });
        LightLL.setOnClickListener(v -> { //When Light Mode LL is clicked
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Switch App Theme to Day Mode
            editor.putBoolean("isDarkModeOn", false); //Change isDarkMode boolean to false
            editor.apply(); //Apply Changes
        });
        AboutLL.setOnClickListener(v -> openBrowser(getString(R.string.ABOUT)));
        PrivacyLL.setOnClickListener(v -> openBrowser(getString(R.string.PRIVACY)));
        WebsiteLL.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show());
        Paper2021LL.setOnClickListener(v -> openBrowser(getString(R.string.PAPER2021)));
        Paper2022LL.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show());
        FeedbackLL.setOnClickListener(v -> openBrowser(getString(R.string.FEEDBACK)));
        SignOutLL.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            Intent loginInt = new Intent(MainActivity.this, LoginActivity.class);
            loginInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginInt);
        });
        BackLL.setOnClickListener(v -> mainMenuCV.setVisibility(View.GONE));
    }

    private void openBrowser(String URL) {
        mainMenuCV.setVisibility(View.GONE);
        Intent browseInt = new Intent(MainActivity.this, InAppBrowser.class);
        browseInt.putExtra("URL", URL);
        startActivity(browseInt);
    }
}
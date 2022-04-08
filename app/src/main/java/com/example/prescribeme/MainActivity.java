package com.example.prescribeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView txtDrName;
    Button btnPrescribe, btnSignOut, btnView, btnSign;
    ImageView ProfComplete, SignComplete;
    CardView mainMenuCV;
    LinearLayout BackLL, AboutLL, DarkLL, LightLL, MenuLL, PrivacyLL, Paper2021LL, Paper2022LL, FeedbackLL, WebsiteLL, YouTubeLL, SignOutLL;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realDB;
    DatabaseReference realRef;

    private String UserID;
    Drawable Complete, Pending;

    private int CHECK = 0;
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
        checkInternet();

        mAuth= FirebaseAuth.getInstance(); //Firebase Instance
        user=mAuth.getCurrentUser(); //Getting Currently Signed In User
        UserID= user != null ? user.getUid() : null; //Retrieves unique User ID
        realDB=FirebaseDatabase.getInstance(); //Instance of Realtime Database
        realRef=realDB.getReference().child(UserID); //Reference of the Database & Child branch with name as UserID of user

        ProfComplete=(ImageView) findViewById(R.id.ProfComplete);
        SignComplete=(ImageView) findViewById(R.id.SignComplete);
        Complete=getDrawable(R.drawable.ic_complete); //References the Check Mark Drawable Icon
        Pending=getDrawable(R.drawable.ic_pending); //References the Cross Mark Drawable Icon
        checkComplete();

        txtDrName=(TextView)findViewById(R.id.txtDrName);
        txtDrName.setText("Dr. " + user.getDisplayName()); //Displaying Name of Doctor(User)
        if (txtDrName.getText().toString().trim().equals("Dr.")){
            Toast.makeText(MainActivity.this, "Please Update Your Profile First!", Toast.LENGTH_LONG).show();
            Intent updInt=new Intent(MainActivity.this, UpdateProfile.class);
            updInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(updInt);
        }

        btnPrescribe=(Button)findViewById(R.id.btnPrescribe);
        btnPrescribe.setOnClickListener(v ->{
            if (CHECK != 0)
                startActivity(new Intent(MainActivity.this, PatientInfo.class)); //Directs User to PatientInfo Activity
            else
                Toast.makeText(MainActivity.this,"Please Wait While Profile Details are fetched!", Toast.LENGTH_SHORT).show();
        });

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
        YouTubeLL=(LinearLayout) findViewById(R.id.YouTubeLL); //LinearLayout of YouTube Option
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
        YouTubeLL.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.YOUTUBE)))));
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

    //Function to check if Profile is Complete & Signature is Uploaded
    private void checkComplete() {
        realRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int j=1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String snap_value=snapshot1.getValue().toString();
                    String snap_name= snapshot1.getKey();
                    switch (snap_name)
                    {
                        case "Completed":
                            if (snap_value.equals("false")){
                                ProfComplete.setImageDrawable(Pending); //Changes to Cross Icon
                                Toast.makeText(MainActivity.this, "Please Update Profile Info Before Continuing", Toast.LENGTH_LONG).show();
                                Intent updInt = new Intent(MainActivity.this, UpdateProfile.class); //Leads User to Update Profile Activity
                                updInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(updInt);
                            }
                            else {
                                ProfComplete.setImageDrawable(Complete); //Changes to Tick/Check Icon
                                CHECK = (CHECK + 1) % 3;
                            }
                            break;
                        case "Profile Info":
                        case "Sign Extension":
                            break;
                        case "Sign Uploaded":
                            if (snap_value.equals("false")){
                                SignComplete.setImageDrawable(Pending); //Changes to Cross Icon
                                Toast.makeText(MainActivity.this, "Please Upload Signature Before Continuing", Toast.LENGTH_LONG).show();
                                Intent signInt = new Intent(MainActivity.this, UpdateSignature.class); //Leads User to Update Signature Activity
                                signInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(signInt);
                            }
                            else
                            {
                                SignComplete.setImageDrawable(Complete); //Changes to Tick/Check Icon
                                CHECK = (CHECK + 1) % 3;
                            }
                            break;
                        default:
                            Toast.makeText(MainActivity.this, (j++)+": "+snap_value,Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function to check if Internet if is working or not
    private void checkInternet() {
        if (!haveNetworkConnection()) //If Internet is Not Connected
        {
            AlertDialog.Builder noInternetBuilder = new AlertDialog.Builder(MainActivity.this); //Create Alert Dialog Box
            noInternetBuilder.setTitle("No Internet Detected!") //Title of Alert Dialog Box
                    .setMessage("Please Turn On Internet to use this App") //Message which will be displayed in the Alert Dialog Box
                    .setCancelable(false) //Cannot Cancel By Clicking Outside of the Box
                    .setIcon(R.drawable.no_internet) //Setting an Icon for the Alert Dialog Box
                    .setPositiveButton("WiFi Settings", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))) //Opens WiFi Settings when clicked
                    .setNegativeButton("Mobile Data Settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))) //Open Mobile Data Settings when clicked
                    .setNeutralButton("Exit App", (dialog, id) -> MainActivity.this.finish()); //Closes App when Clicked
            AlertDialog noInternet = noInternetBuilder.create(); //Alert Dialog Box is finally created
            noInternet.show(); //Displaying the Alert Dialog Box
        }
        else
            Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show();
    }

    //Function to check Connection Connection Status
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false, haveConnectedMobile = false; //Boolean to check N/W Connection

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Getting Connection Status
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo(); //Getting Network Information
        for (NetworkInfo networkInfo : netInfo) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WiFi")) //Getting WiFi Status
                if (networkInfo.isConnected()) //If Connected Using WiFi
                    haveConnectedWifi = true;
            if (networkInfo.getTypeName().equalsIgnoreCase("Mobile")) //Getting Mobile Data Status
                if (networkInfo.isConnected()) //If Connected using Mobile Data
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
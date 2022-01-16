package com.example.prescribeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfile extends AppCompatActivity {

    TextView viewFName, viewLName, viewAadharNo, viewQualifications, viewRegistrationNo, viewClinic, viewContact, messageBox;
    Button btnHome, btnGoUpdate;
    String UserID;
    int warn, success;
    
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realDB;
    DatabaseReference realRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        warn=ContextCompat.getColor(ViewProfile.this, R.color.red); //Saving RGB value of red color as int
        success= ContextCompat.getColor(ViewProfile.this, R.color.foreground); //Saving RGB value of foreground(gold/navy-blue) as int

        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID= user != null ? user.getUid() : null; //Retrieves unique User ID
        realDB=FirebaseDatabase.getInstance(); //Instance of Realtime Database
        realRef=realDB.getReference().child(UserID); //Reference of the Database & Child branch with name as UserID of user

        viewFName=(TextView) findViewById(R.id.viewFName);
        viewLName=(TextView) findViewById(R.id.viewLName);
        viewAadharNo=(TextView) findViewById(R.id.viewAadharNo);
        viewQualifications=(TextView) findViewById(R.id.viewQualifications);
        viewRegistrationNo=(TextView) findViewById(R.id.viewRegistrationNo);
        viewClinic=(TextView) findViewById(R.id.viewClinic);
        viewContact=(TextView) findViewById(R.id.viewContact);

        messageBox=(TextView) findViewById(R.id.messageBoxVP); //References the Message Box
        messageBox.setText("Please Wait while we load your data"); //We now show our messages through the Message Box
        messageBox.setTextColor(warn);
        displayUserProfile();

        btnGoUpdate=(Button)findViewById(R.id.btnViewUpdate);
        btnGoUpdate.setOnClickListener(view -> {
            Intent updint=new Intent(ViewProfile.this, UpdateProfile.class);
            startActivity(updint);
        });

        btnHome=(Button)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(view -> {
            Intent backint=new Intent(ViewProfile.this,MainActivity.class);
            startActivity(backint);
        });
    }

    //Function to display User Profile in their respective TextView
    private void displayUserProfile() {
        realRef.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //Loads a snapshot of all children of given branch
                messageBox.setText("You may now view your details");
                messageBox.setTextColor(success);
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String snap_value = snapshot1.getValue().toString(); //Extract Value of Individual Child
                    String snap_name = snapshot1.getKey(); //Extract Name/Key of Individual Child

                    //Alphabetical Order: Aadhar No, Clinic, Contact, First Name, Last Name, Qualifications, Registration No
                    if (!snap_value.equals("false")){
                        switch (snap_name) {
                            case "Aadhar No":
                                viewAadharNo.setText(snap_value);
                                break;
                            case "Clinic":
                                viewClinic.setText(snap_value);
                                break;
                            case "Contact":
                                viewContact.setText(snap_value);
                                break;
                            case "First Name":
                                viewFName.setText(snap_value);
                                break;
                            case "Last Name":
                                viewLName.setText(snap_value);
                                break;
                            case "Qualifications":
                                viewQualifications.setText(snap_value);
                                break;
                            case "Registration No":
                                viewRegistrationNo.setText(snap_value);
                                break;
                            default:
                                Toast.makeText(ViewProfile.this, "An Error Occurwarn while loading content", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfile.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                messageBox.setText("Error: "+error);
                messageBox.setTextColor(warn);
            }
        });
    }
}
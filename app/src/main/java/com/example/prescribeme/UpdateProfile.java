package com.example.prescribeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    EditText usrFName, usrLName, usrAadharNo, usrQualifications, usrRegistrationNo, usrClinic, usrContact, dummy;
    ImageButton[] mic = new ImageButton[7]; //Array for storing all the mic buttons
    Button update;
    String FName, LName, AadharNo, Qualifications, RegistrationNo, Clinic, Contact;
    String UserID, Name;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realDB;
    DatabaseReference realRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Receiving Intent from ViewProfile Activity
        Intent updint=getIntent();

        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID= user != null ? user.getUid() : null; //Retrieves unique User ID
        realDB=FirebaseDatabase.getInstance(); //Instance of Realtime Database
        realRef=realDB.getReference().child(UserID); //Reference of the Database & Child branch with name as UserID of user

        usrFName=(EditText) findViewById(R.id.usrFirstName);
        usrLName=(EditText) findViewById(R.id.usrLastName);
        usrAadharNo=(EditText) findViewById(R.id.usrAadharNo);
        usrQualifications=(EditText) findViewById(R.id.usrQualifications);
        usrRegistrationNo=(EditText) findViewById(R.id.usrRegistrationNo);
        usrClinic=(EditText) findViewById(R.id.usrClinic);
        usrContact=(EditText) findViewById(R.id.usrContact);

        displayUserProfile();

        mic[0]=(ImageButton) findViewById(R.id.mic_FName);
        mic[1]=(ImageButton) findViewById(R.id.mic_LName);
        mic[2]=(ImageButton) findViewById(R.id.mic_AadharNo);
        mic[3]=(ImageButton) findViewById(R.id.mic_Qualif);
        mic[4]=(ImageButton) findViewById(R.id.mic_RegNo);
        mic[5]=(ImageButton) findViewById(R.id.mic_Clinic);
        mic[6]=(ImageButton) findViewById(R.id.mic_Contact);

        mic[0].setOnClickListener(v -> micCalling(usrFName) /*mic[0] is associated with usrFName & hence, that is passed to micCalling()*/);
        mic[1].setOnClickListener(v -> micCalling(usrLName) /*mic[1] is associated with usrLName & hence, that is passed to micCalling()*/);
        mic[2].setOnClickListener(v -> micCalling(usrAadharNo) /*mic[2] is associated with usrAadharNo & hence, that is passed to micCalling()*/);
        mic[3].setOnClickListener(v -> micCalling(usrQualifications) /*mic[3] is associated with usrQualifications & hence, that is passed to micCalling()*/);
        mic[4].setOnClickListener(v -> micCalling(usrRegistrationNo) /*mic[4] is associated with usrRegistrationNo & hence, that is passed to micCalling()*/);
        mic[5].setOnClickListener(v -> micCalling(usrClinic) /*mic[5] is associated with usrClinic & hence, that is passed to micCalling()*/);
        mic[6].setOnClickListener(v -> micCalling(usrContact) /*mic[6] is associated with usrContact & hence, that is passed to micCalling()*/);

        update=(Button)findViewById(R.id.btnUpdateProfile);
        update.setOnClickListener(view -> updateUserProfile());
    }

    //Single Common Function which is called when any mic button is pressed
    public void micCalling(EditText editText)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            dummy=editText; //dummy is assigned the EditText which needs to be filled at that particular call
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(UpdateProfile.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Use this method when calling any Google API eg. Speech to Text, Sign In, etc...
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                dummy.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    //Function to Update User Profile in Relational Database
    private void updateUserProfile() {
        if (checkCredentials()){
            Name=FName+" "+LName; //Save the full name of user
            UserProfileChangeRequest profileUpdates;
            profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(Name).build(); //changes display name to currently entered Full Name
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> { //Intimation of Updating Process Result to User
                        if (task.isSuccessful())
                            Toast.makeText(UpdateProfile.this, "User Display Name Update Successful!",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(UpdateProfile.this, "User Display Name Update Unsuccessful!",Toast.LENGTH_SHORT).show();
                    });

            HashMap<String, Object> Profile=new HashMap<>(); //Similar to Dictionary to store User Profile
            //Storing is done through put() method with signature <key>,<value>
            Profile.put("First Name", FName);
            Profile.put("Last Name", LName);
            Profile.put("Aadhar No", AadharNo);
            Profile.put("Registration No", RegistrationNo);
            Profile.put("Qualifications", Qualifications);
            Profile.put("Clinic", Clinic);
            Profile.put("Contact", Contact);
            //Alphabetical Order: Aadhar No, Clinic, Contact, First Name, Last Name, Qualifications, Registration No
            realRef.child("Profile Info").updateChildren(Profile) //Here all the data fields are updated together using HashMap
                    .addOnCompleteListener(task -> { //to check if update was successful
                        if(task.isSuccessful()){
                            realRef.child("Completed").setValue(true) //Setting Field 'Completed' as true
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            Toast.makeText(UpdateProfile.this, "Database Update Successful!", Toast.LENGTH_LONG).show();
                                            Toast.makeText(UpdateProfile.this, "Loading View Profile Activity", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                                        }
                                        else Toast.makeText(UpdateProfile.this, "Database Update Unsuccessful!", Toast.LENGTH_LONG).show();
                                    });
                        }
                        else Toast.makeText(UpdateProfile.this, "Database Update Unsuccessful!", Toast.LENGTH_LONG).show();
                    });
        }
    }

    private boolean checkCredentials() { //Function to check if User has entered data correctly
        FName=usrFName.getText().toString(); //extracting FName from EditText
        LName=usrLName.getText().toString(); //extracting LName from EditText
        AadharNo=usrAadharNo.getText().toString(); //extracting AadharNo from EditText
        Qualifications=usrQualifications.getText().toString(); //extracting Qualifications from EditText
        RegistrationNo=usrRegistrationNo.getText().toString(); //extracting RegistrationNo from EditText
        Clinic=usrClinic.getText().toString(); //extracting Clinic from EditText
        Contact=usrContact.getText().toString(); //extracting Contact from EditText
        if (FName.isEmpty()){ //Blank FName
            usrFName.setError("Please Enter First Name");
            usrFName.requestFocus();
            return false;
        }
        else if (LName.isEmpty()){ //Blank LName
            usrLName.setError("Please Enter Last Name");
            usrLName.requestFocus();
            return false;
        }
        else if (AadharNo.length() != 12 || isNotNumeric(AadharNo)){ //AadharNo length is not 12 (Could be Empty) OR is not a number
            usrAadharNo.setError("Enter Valid Aadhar No");
            usrAadharNo.requestFocus();
            return false;
        }
        else if (Qualifications.isEmpty()){ //Blank Qualifications
            usrQualifications.setError("Please Enter Qualification");
            usrQualifications.requestFocus();
            return false;
        }
        else if (RegistrationNo.isEmpty()){ //Blank RegistrationNo
            usrRegistrationNo.setError("Please Enter Registration No.");
            usrRegistrationNo.requestFocus();
            return false;
        }
        else if (Clinic.isEmpty()){ //Blank Clinic
            usrClinic.setError("Enter At least 1 Clinic/Hospital");
            usrClinic.requestFocus();
            return false;
        }
        else if (Contact.length()!=8 && Contact.length()!=10 || isNotNumeric(Contact)){ //Contact length is neither 8 nor 10 (Could be Blank) OR is not a number
            usrContact.setError("Enter Valid Contact No");
            usrContact.requestFocus();
            return false;
        }
        else
            return true; //If everything is perfect we return true
    }

    //Function to check if String is Numeric
    public boolean isNotNumeric(String strNum) {
        Pattern pattern = Pattern.compile("\\d+"); //RegEx to suggest a digit(\d)
        if (strNum == null) {
            return true;
        }
        return !pattern.matcher(strNum).matches(); //compares string with given pattern
    }

    //Function to display User Profile in their respective EditText
    private void displayUserProfile() {
        realRef.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //Loads a snapshot of all children of given branch
                Toast.makeText(UpdateProfile.this, "Please Wait while we load your data", Toast.LENGTH_LONG).show();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String snap_value = snapshot1.getValue().toString(); //Extract Value of Individual Child
                    String snap_name = snapshot1.getKey(); //Enter Name/Key of Individual Child

                    //Alphabetical Order: Aadhar No, Clinic, Contact, First Name, Last Name, Qualifications, Registration No
                    if (!snap_value.equals("false")){
                        switch (snap_name) {
                            case "Aadhar No":
                                usrAadharNo.setText(snap_value);
                                break;
                            case "Clinic":
                                usrClinic.setText(snap_value);
                                break;
                            case "Contact":
                                usrContact.setText(snap_value);
                                break;
                            case "First Name":
                                usrFName.setText(snap_value);
                                break;
                            case "Last Name":
                                usrLName.setText(snap_value);
                                break;
                            case "Qualifications":
                                usrQualifications.setText(snap_value);
                                break;
                            case "Registration No":
                                usrRegistrationNo.setText(snap_value);
                                break;
                            default:
                                Toast.makeText(UpdateProfile.this, "An Error Occurred while loading content", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Toast.makeText(UpdateProfile.this, "You May Now Proceed to enter your details", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
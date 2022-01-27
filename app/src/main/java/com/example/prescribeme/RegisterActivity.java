package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    private String UserID;

    EditText ipEmailId, ipPassword;
    Button btnRegister, btnGoogle, btnFacebook;
    String emailId, password;
    ImageButton email_mic;
    TextView txtSignIn;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realDB;
    DatabaseReference realRef;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Receiving Intent from Login Activity
        Intent newACint=getIntent();

        //Initialising both EditTexts (input box)
        ipEmailId=(EditText)findViewById(R.id.EmailIDRegister);
        ipPassword=(EditText)findViewById(R.id.PasswordRegister);

        mAuth= FirebaseAuth.getInstance();
        mLoadingBar=new ProgressDialog(this);


        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> checkCredentials());

        txtSignIn = findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(v -> {
            //Calling Register Activity through Intent
            Intent oldACint=new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(oldACint);
        });

        btnFacebook=(Button)findViewById(R.id.FBsignup);
        btnFacebook.setOnClickListener(v -> Toast.makeText(RegisterActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show());

        btnGoogle=(Button)findViewById(R.id.Gsignup);
        btnGoogle.setOnClickListener(v -> Toast.makeText(RegisterActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show());

        email_mic=(ImageButton)findViewById(R.id.mic_email); //Mic Button
        email_mic.setOnClickListener(v -> { /**Calling Google Speech to Text API*/
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch (Exception e) {
                Toast.makeText(RegisterActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Use this method when calling any Google API eg. Speech to Text, Sign In, etc...
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ipEmailId.setText(Objects.requireNonNull(result).get(0));
                }
        }
    }

    private void checkCredentials() { //Function to check if User has entered data correctly
        emailId = ipEmailId.getText().toString(); //extracting email from EditText
        password = ipPassword.getText().toString(); //extracting password from EditText
        if (!emailId.contains("@")) { //Email should have '@'
            ipEmailId.setError("Email ID Invalid!");
            ipEmailId.requestFocus();
        } else if (password.length() < 8) { //Password should be greater than 8 characters
            ipPassword.setError("Password should be greater than 8 characters");
            ipPassword.requestFocus();
        } else {
            mLoadingBar.setTitle("Register");
            mLoadingBar.setMessage("Please Wait while we log you in");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(emailId, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Registration Successful", Toast.LENGTH_SHORT).show();
                    mLoadingBar.dismiss();
                    user=mAuth.getCurrentUser();
                    UserID=user.getUid();
                    realDB=FirebaseDatabase.getInstance();
                    realRef=realDB.getReference().child(UserID);
                    HashMap<String,Object> Profile=new HashMap<>();
                    Profile.put("First Name", false);
                    Profile.put("Last Name", false);
                    Profile.put("Aadhar No", false);
                    Profile.put("Registration No", false);
                    Profile.put("Qualifications", false);
                    Profile.put("Clinic", false);
                    Profile.put("Contact", false);
                    HashMap User = new HashMap();
                    User.put("Completed", false);
                    User.put("Profile Info", Profile);
                    User.put("Sign Uploaded", false);
                    realRef.updateChildren(User);
                    Intent  mainInt=new Intent(RegisterActivity.this, MainActivity.class); //Calling Main Activity
                    mainInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainInt);
                } else {
                    Toast.makeText(RegisterActivity.this, "User Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                    mLoadingBar.dismiss();
                    startActivity(new Intent(RegisterActivity.this, RegisterActivity.class)); //Reloading Register Activity due to unsuccessful Registration
                }
            });

        }
    }
}
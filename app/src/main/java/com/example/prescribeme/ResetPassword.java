package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;

    EditText ipEmailId;
    Button btnResetPwd;
    ImageButton email_mic;
    String emailId;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Receiving Intent from Login Activity
        Intent resetInt=getIntent();
        emailId=resetInt.getExtras().getString("EmailID", ""); //Getting String named "EmailID" from Login Activity

        mAuth=FirebaseAuth.getInstance();

        ipEmailId=(EditText) findViewById(R.id.EmailIDReset);
        if (!emailId.isEmpty())
        ipEmailId.setText(emailId);

        btnResetPwd=(Button)findViewById(R.id.btnResetPWD);
        btnResetPwd.setOnClickListener(v -> {
            emailId=ipEmailId.getText().toString();
            if (emailId.isEmpty() || !emailId.contains("@")){ //Checking if Email is blank or if it contains '@'
                ipEmailId.setError("Email ID Invalid!"); //shows Error on the Email ID Input Box
                ipEmailId.requestFocus();
            }
            else
            {   /**Sending Password Reset Email through Firebase*/
                mAuth.sendPasswordResetEmail(emailId)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                                //shows that Password Reset Mail is sent successfully
                                Toast.makeText(ResetPassword.this, "Password Reset Link Sent Successful! Please Sign In Again", Toast.LENGTH_SHORT).show();
                            else
                                //shows that Password Reset Mail was not sent successfully
                                Toast.makeText(ResetPassword.this, "There was an Error Password Reset Message! Please Sign In Again", Toast.LENGTH_SHORT).show();
                        });
                startActivity(new Intent(ResetPassword.this,LoginActivity.class)); //Calling Login Activity Again to Reset Layout
            }
        });

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
                Toast.makeText(ResetPassword.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
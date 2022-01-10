package com.example.prescribeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;

    TextView txtSignUp, txtForgotPassword;
    EditText ipEmailId, ipPassword;
    Button btnLogin, btnGoogle, btnFacebook;
    String emailId, password;
    ImageButton email_mic;

    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Receiving Intent from Register Activity
        Intent oldACint=getIntent();

        //Initialising both EditTexts (input box)
        ipEmailId=(EditText) findViewById(R.id.EmailIDLogin);
        ipPassword=(EditText) findViewById(R.id.PasswordLogin);

        mAuth=FirebaseAuth.getInstance(); //Instance of Firebase Authentication
        mLoadingBar=new ProgressDialog(this); //Instance of Progress Dialog Box

        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> checkCredentials());

        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(v -> {
            //Calling Register Activity through Intent
            Intent newACint=new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(newACint);
        });

        txtForgotPassword=(TextView)findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(v -> {
            emailId=ipEmailId.getText().toString();
            Intent resetInt=new Intent(LoginActivity.this, ResetPassword.class);
            resetInt.putExtra("EmailID", emailId);
            startActivity(resetInt);
        });

        btnFacebook=(Button)findViewById(R.id.FBsignup);
        btnFacebook.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show());

        btnGoogle=(Button)findViewById(R.id.Gsignup);
        btnGoogle.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "This Feature is Temporarily Suspended",Toast.LENGTH_SHORT).show());

        email_mic=(ImageButton)findViewById(R.id.mic_email); //Mic Button
        email_mic.setOnClickListener(v -> { /**Calling Google Speech to Text API*/
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch (Exception e) {
                Toast
                        .makeText(LoginActivity.this, " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
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
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    ipEmailId.setText(
                            Objects.requireNonNull(result).get(0));
                }
        }
    }

    private void checkCredentials() {
        emailId=ipEmailId.getText().toString();
        password=ipPassword.getText().toString();
        if (emailId.isEmpty() || !emailId.contains("@")){
            ipEmailId.setError("Email ID Invalid!");
            ipEmailId.requestFocus();
        }
        else if (password.isEmpty() || password.length()<8){
            ipPassword.setError("Password should be greater than 8 characters");
            ipPassword.requestFocus();
        }
        else{
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please Wait while we log you in");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show(); //Progress Dialog Box is shown

            mAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    mLoadingBar.dismiss();
                    Intent  mainint=new Intent(LoginActivity.this, MainActivity.class); //Calling Main Activity
                    mainint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainint);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Login Unsuccessful",Toast.LENGTH_SHORT).show();
                    mLoadingBar.dismiss();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class)); //Reloading Login Activity due to unsuccessful Login
                }
            });
        }
    }
}


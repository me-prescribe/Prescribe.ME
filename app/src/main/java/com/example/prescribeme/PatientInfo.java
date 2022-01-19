package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class PatientInfo extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    private final String URL = "https://prescribe-me.herokuapp.com/predict";

    EditText inFName, inLName, inAge, inGender, dummy;
    ImageButton[] mic =new ImageButton[4];
    Button proceed;

    String FName, LName, Age, Gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        inFName=(EditText) findViewById(R.id.PatFName);
        inLName=(EditText) findViewById(R.id.PatLName);
        inAge=(EditText) findViewById(R.id.PatAge);
        inGender=(EditText) findViewById(R.id.PatGender);

        mic[0]=(ImageButton) findViewById(R.id.micPatFName);
        mic[1]=(ImageButton) findViewById(R.id.micPatLName);
        mic[2]=(ImageButton) findViewById(R.id.micPatAge);
        mic[3]=(ImageButton) findViewById(R.id.micPatGender);

        mic[0].setOnClickListener(v -> micCalling(inFName) /*mic[0] is associated with inFName & hence, that is passed to micCalling()*/);
        mic[1].setOnClickListener(v -> micCalling(inLName) /*mic[1] is associated with inLName & hence, that is passed to micCalling()*/);
        mic[2].setOnClickListener(v -> micCalling(inAge) /*mic[2] is associated with inAge & hence, that is passed to micCalling()*/);
        mic[3].setOnClickListener(v -> micCalling(inGender) /*mic[3] is associated with inGender & hence, that is passed to micCalling()*/);

        proceed=(Button) findViewById(R.id.btnProceed);
        proceed.setOnClickListener(v -> {
            if(checkCredentials()){
                checkAPI();
                Intent presInt=new Intent(PatientInfo.this, Prescribe.class);
                presInt.putExtra("Name", FName+" "+LName);
                presInt.putExtra("Age", Age);
                presInt.putExtra("Gender", Gender);
                startActivity(presInt);
            }
        });
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
            Toast.makeText(PatientInfo.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean checkCredentials() { //Function to check if User has entered data correctly
        FName=inFName.getText().toString(); //Extracting FName from EditText
        LName=inLName.getText().toString(); //Extracting LName from EditText
        Age=inAge.getText().toString(); //Extracting Age from EditText
        Gender=inGender.getText().toString(); //Extracting Gender from EditText
        if (FName.isEmpty()){ //Blank FName
            inFName.setError("Please Enter First Name");
            inFName.requestFocus();
            return false;
        }
        else if (LName.isEmpty()){ //Blank LName
            inLName.setError("Please Enter Last Name");
            inLName.requestFocus();
            return false;
        }
        else if (Age.isEmpty() || isNotNumeric(Age)) { //Age is Empty or Not a Number
            inAge.setError("Enter Valid Age");
            inAge.requestFocus();
            return false;
        }
        else if (Gender.isEmpty()){ //Blank Gender
            inGender.setError("Please Enter Gender");
            inGender.requestFocus();
            return false;
        }
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

    private void checkAPI() {
        for(int j=0; j<3; j++)
        {
            // hit the API -> Volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST /*The Post Method is used for Flask API Connection*/, URL /*URL for connection is specified*/,
                    response -> Toast.makeText(PatientInfo.this,"Testing API Connection", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(PatientInfo.this, "API Connection Error", Toast.LENGTH_SHORT).show()){
                @Override
                protected Map getParams(){ //Function gets the Parameters to be passed to the API
                    Map params = new HashMap();
                    params.put("prescription","This is a test call to API"); //Adds Value to Key 'prescription' of Dictionary/HashMap
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(PatientInfo.this);
            queue.add(stringRequest);
        }
    }
}
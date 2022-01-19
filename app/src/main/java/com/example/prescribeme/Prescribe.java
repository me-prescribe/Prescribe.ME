package com.example.prescribeme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Prescribe extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    private final String URL="https://prescribe-me.herokuapp.com/predict";

    EditText inDiagnosis, inPrescription, inInformation, dummy;
    ImageButton[] mic = new ImageButton[3];
    TextView txtPrescribe, messageBox, SlotName, SlotValue;

    String Diagnosis, Prescription, Information;
    int warn, success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescribe);
        warn= ContextCompat.getColor(Prescribe.this, R.color.red); //Saving RGB value of red color as int
        success= ContextCompat.getColor(Prescribe.this, R.color.foreground); //Saving RGB value of foreground(gold/navy-blue) as int

        inDiagnosis=(EditText) findViewById(R.id.Diagnosis);
        inPrescription=(EditText) findViewById(R.id.Medicine);
        inInformation=(EditText) findViewById(R.id.Information);

        messageBox=(TextView) findViewById(R.id.messageBoxPres); //References the Message Box

        SlotName=(TextView) findViewById(R.id.slot_name);
        SlotValue=(TextView) findViewById(R.id.slot_value);
        txtPrescribe=(TextView) findViewById(R.id.txtPrescribe);
        txtPrescribe.setOnClickListener(v -> {
            Prescription=inPrescription.getText().toString();
            if(Prescription.isEmpty()) {
                inPrescription.setError("Please Enter Prescription");
                inPrescription.requestFocus();
            }
            else
                APIConnection(Prescription);
        });

        mic[0]=(ImageButton) findViewById(R.id.micDiagnosis);
        mic[1]=(ImageButton) findViewById(R.id.micMedicine);
        mic[2]=(ImageButton) findViewById(R.id.micInformation);

        mic[0].setOnClickListener(v -> micCalling(inDiagnosis) /*mic[0] is associated with inDiagnosis & hence, that is passed to micCalling()*/);
        mic[1].setOnClickListener(v -> micCalling(inPrescription) /*mic[1] is associated with inPrescription & hence, that is passed to micCalling()*/);
        mic[2].setOnClickListener(v -> micCalling(inInformation) /*mic[2] is associated with inInformation & hence, that is passed to micCalling()*/);

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
            Toast.makeText(Prescribe.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void APIConnection(String prescription) {
        txtPrescribe.setVisibility(View.GONE);
        messageBox.setText("Calling API");
        messageBox.setTextColor(warn);
        // hit the API -> Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST /*The Post Method is used for Flask API Connection*/, URL /*URL for connection is specified*/,
                response -> { //We get a String Response from API
                    messageBox.setText("Connection Successful! Results Fetched Successfully");
                    messageBox.setTextColor(success);
                    inPrescription.setText("");
                    if (!response.equals("non-prescriptive")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response); //Converting the String to JSON
                            JSONArray slots_JSONArray = jsonObject.getJSONArray("response"); //Converting Value of 'response' to array of JSON
                            JSONObject[] json_array = new JSONObject[slots_JSONArray.length()]; //Creating Array to store above created split
                            for (int i = 0, l = slots_JSONArray.length(); i < l; i++)
                                json_array[i] = new JSONObject(slots_JSONArray.getString(i)); //Storing split JSON array
                            String slots_name="Intent: ", slots_value="Prescriptive"; //Creating Strings to store Slot Name & Value starts with Intent as Prescriptive
                            for (int j = 0; j < json_array.length; j++){
                                slots_name=slots_name + "\n" + json_array[j].getString("slot") + ": "; //Extracting Slot Name from each JSON element
                                slots_value=slots_value + "\n" + json_array[j].getString("value"); //Extracting Slot Value from each JSON element
                            }
                            SlotName.setText(slots_name.trim()); //Displaying all Slot Names
                            SlotValue.setText(slots_value.trim()); //Displaying all Values
                        } catch (JSONException e) {
                            messageBox.setText("JSON Error: "+e);
                            messageBox.setTextColor(warn);
                        }
                    }
                    else {
                        SlotName.setText("Intent: ");
                        SlotValue.setText("Non Prescriptive");
                    }
                },
                error -> {
                    messageBox.setText("Couldn't Connect to API! Please Retry!");
                    messageBox.setTextColor(warn);
                }){
            @Override
            protected Map getParams(){ //Function gets the Parameters to be passed to the API
                Map params = new HashMap();
                params.put("prescription",prescription); //Adds Value to Key 'prescription' of Dictionary/HashMap
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(Prescribe.this);
        queue.add(stringRequest);
        txtPrescribe.setVisibility(View.VISIBLE);
    }

}
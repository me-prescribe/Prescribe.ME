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
import java.util.Vector;

public class Prescribe extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 110;
    private final String URL = "https://prescribe-me.herokuapp.com/predict";

    EditText inDiagnosis, inPrescription, inInformation, dummy;
    ImageButton[] mic = new ImageButton[3];
    TextView txtPrescribe, messageBox, SlotName, SlotValue;

    String Diagnosis, Prescription, Information;
    String Name, Age, Gender, PresHTML;
    int warn, success;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescribe);
        warn= ContextCompat.getColor(Prescribe.this, R.color.red); //Saving RGB value of red color as int
        success= ContextCompat.getColor(Prescribe.this, R.color.foreground); //Saving RGB value of foreground(gold/navy-blue) as int

        //Receiving Intent from PatientInfo Activity
        Intent presInt=getIntent();
        Name=presInt.getExtras().getString("Name", ""); //Getting String Named "Name"
        Age=presInt.getExtras().getString("Age", ""); //Getting String Named "Age"
        Gender=presInt.getExtras().getString("Gender", ""); //Getting String Named "Gender"

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                            /*for (int j = 0; j < json_array.length; j++){
                                slots_name=slots_name + "\n" + json_array[j].getString("slot") + ": "; //Extracting Slot Name from each JSON element
                                slots_value=slots_value + "\n" + json_array[j].getString("value"); //Extracting Slot Value from each JSON element
                            }
                            SlotName.setText(slots_name.trim()); //Displaying all Slot Names
                            SlotValue.setText(slots_value.trim()); //Displaying all Values*/
                            PresHTML=generate_prescription(json_array); //Sorts the slots & return HTML code
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generate_prescription(JSONObject[] jsonObjects) throws JSONException { //Function to separate the slots according to our needs & getting HTML code based on it
        String Drug = "", INN = "", Freq = "", CMA = "", Dur = "", Gap = "", Qty = "", ROA = "", Cond = "", Fast = "", Rhy="", Max="";
        Vector<String> inn=new Vector<>(1),rhy=new Vector<>(1), freq=new Vector<>(1),
                dur_val=new Vector<>(1), dos_val=new Vector<>(1), cond=new Vector<>(1), cma=new Vector<>(1);
        String[] drug = {"","","",""}, dur = {"",""}, qty = {"",""}, gap = {"",""}, max = {"",""};
        String slot, value;
        for(JSONObject object: jsonObjects) {
            slot = object.getString("slot");
            value = object.getString("value");

            switch (slot) {
                case "INN":
                    inn.add(value.trim());
                    break;
                case "Drug":
                    drug[1] = value.trim();
                    break;
                case "d-dos-form":
                    drug[0] = value.trim();
                    break;
                case "d-dos-val":
                    drug[2] = value.trim();
                    break;
                case "d-dos-UP":
                    drug[3] = value.trim();
                    break;
                case "rhythm-TDTE":
                case "rhythm-rec-ut":
                    rhy.add(value.trim());
                    break;
                case "rhythm-hour":
                    rhy.add("at " + value.trim());
                    break;
                case "rhythm-perday":
                    rhy.add(value.trim() + " daily");
                    break;
                case "rhythm-rec-val":
                    rhy.add("every " + value.trim());
                    break;
                case "freq-days":
                case "freq-count":
                case "freq-count-ut":
                    freq.add(value.trim());
                    break;
                case "dur-val":
                    dur_val.add(value.trim());
                    break;
                case "dur-UT":
                    dur[1] = value.trim();
                    break;
                case "cma-event":
                    cma.add(value.trim());
                    break;
                case "dos-val":
                    dos_val.add(value.trim());
                    break;
                case "dos-UF":
                    qty[1] = value.trim();
                    break;
                case "dos-cond":
                    cond.add(value.trim());
                    break;
                case "fasting":
                    Fast = value.trim();
                    break;
                case "ROA":
                    ROA = value.trim();
                    break;
                case "min-gap-val":
                    gap[0] = value.trim();
                    break;
                case "min-gap-ut":
                    gap[1] = value.trim();
                    break;
                case "max-unit-val":
                    max[0] = value.trim();
                    break;
                case "max-unit-uf":
                    max[1] = value.trim();
                    break;
            }
        }
        INN = (String.join(" + ", inn)).trim();
        if (drug[1].equals(""))
            drug[1] = inn.get(0);

        Drug = (String.join(" ",drug)).trim();
        Rhy = (String.join(", ",rhy)).trim();
        Freq = (String.join(", ",freq)).trim();
        dur[0] = (String.join(", ",dur_val)).trim();
        Gap = (String.join(" ",gap)).trim();
        Dur = (String.join(" ",dur)).trim();
        qty[0] = (String.join(", ",dos_val)).trim();
        Qty = (String.join(" ",qty)).trim();
        Cond = (String.join(", ",cond)).trim();
        Max = (String.join(" ",max)).trim();
        CMA = (String.join(" ",cma)).trim();

        if (Rhy.equals("") && !Freq.equals(""))
            Freq = Freq;
        else if (Freq.equals("") && !Rhy.equals(""))
            Freq = Rhy;
        else if (!Freq.equals("") && !Rhy.equals("")){
            Freq = Rhy + " & " + Freq;
            Freq = Freq.trim();}
        if (Max.equals("") && !Qty.equals(""))
            Qty = Qty;
        else if (Qty.equals("") && !Max.equals(""))
            Qty = "Max: " + Max;
        else if (!Qty.equals("") && !Max.equals("")){
            Qty = Qty + " & Max: " + Max;
            Qty = Qty.trim();}
        if (!Cond.equals(""))
            Cond = "if " + Cond;
        if (!Gap.equals(""))
            Gap = "Gap: " + Gap;
        if (Freq.equals(""))
            Freq = "-";
        if (CMA.equals(""))
            CMA = "-";
        if (Dur.equals(""))
            Dur = "-";
        if (Gap.equals(""))
            Gap = "-";
        if (Qty.equals(""))
            Qty = "-";
        if (ROA.equals(""))
            ROA = "-";
        if (Cond.equals(""))
            Cond = "-";
        if (Fast.equals(""))
            Fast = "-";
        String[] prescribe = {Drug, INN, Freq, CMA, Dur, Gap, Qty, ROA, Cond, Fast};
        SlotName.setText("Drug: \nINN: \nFrequency: \nConsumption: \nDuration: \nGap: \nQuantity: \nRoute: \nCondition: \nFasting: ");
        SlotValue.setText((String.join("\n",prescribe)).trim());
        PresHTML=PrescriptionHTML.getPresHTML(PresHTML, prescribe);
        return PresHTML;
    }

}
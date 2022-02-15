package com.example.prescribeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConfirmProfile extends AppCompatActivity {
    
    TextView drName, drQualifications, drContact, drClinic, patName, patAge, patGender, messageBox, txtPrescriptionNo;
    Button btnHTML, btnReset;

    String DrName, DrQualifications, DrContact, DrClinic, PatName, PatAge, PatGender;
    String Diagnosis, PresHTML, Information, UserID;
    String todayDate, PrescriptionNo, Head, DocHTML, PatHTML, DiagnosisHTML="", InfoHTML="", Footer, HTML;
    String[] doc_info, pat_info;
    int warn, success;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase realDB;
    DatabaseReference realRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_profile);
        warn= ContextCompat.getColor(ConfirmProfile.this, R.color.red); //Saving RGB value of red color as int
        success= ContextCompat.getColor(ConfirmProfile.this, R.color.foreground); //Saving RGB value of foreground(gold/navy-blue) as int

        //Receiving Intent from Prescribe Activity
        Intent confInt=getIntent();
        PatName=confInt.getExtras().getString("Name",""); //Getting String named "Name"
        PatAge=confInt.getExtras().getString("Age",""); //Getting String named "Age"
        PatGender=confInt.getExtras().getString("Gender",""); //Getting String named "Gender"
        Diagnosis=confInt.getExtras().getString("Diagnosis",""); //Getting String named "Diagnosis"
        Information=confInt.getExtras().getString("Information",""); //Getting String named "Information"
        PresHTML=confInt.getExtras().getString("PresHTML",""); //Getting String named "PresHTML"
        PrescriptionNo=confInt.getExtras().getString("Prescription No",""); //Getting String named "Prescription No"

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        UserID=user.getUid();
        realDB=FirebaseDatabase.getInstance();
        realRef=realDB.getReference().child(UserID);
        
        drName=(TextView) findViewById(R.id.viewDrName);
        drQualifications=(TextView) findViewById(R.id.viewDrQualifications);
        drContact=(TextView) findViewById(R.id.viewDrContact);
        drClinic=(TextView) findViewById(R.id.viewDrClinic);
        
        patName=(TextView) findViewById(R.id.viewPatName);
        patAge=(TextView) findViewById(R.id.viewPatAge);
        patGender=(TextView) findViewById(R.id.viewPatGender);

        messageBox=(TextView) findViewById(R.id.messageBoxCP);
        messageBox.setText("Please Wait till we load Profile Details");
        messageBox.setTextColor(warn);

        txtPrescriptionNo.setText("Prescription #" + PrescriptionNo);

        patName.setText(PatName);
        patAge.setText(PatAge);
        patGender.setText(PatGender);

        DrName=user.getDisplayName();
        drName.setText(DrName);
        displayDocInfo();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String[] splitDate=formattedDate.split("-");
        todayDate=splitDate[0]+" "+splitDate[1]+" "+splitDate[2];

        if(!Diagnosis.trim().isEmpty())
            DiagnosisHTML=PrescriptionHTML.getDiagnosisInfo("Diagnosis", Diagnosis);
        if(!Information.trim().isEmpty())
            InfoHTML=PrescriptionHTML.getDiagnosisInfo("Additional Information", Information);

        btnHTML=(Button) findViewById(R.id.btnConfirm);
        btnHTML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrName=drName.getText().toString();
                DrQualifications=drQualifications.getText().toString();
                DrContact=drContact.getText().toString();
                DrClinic=drClinic.getText().toString();
                doc_info= new String[]{DrName, DrQualifications, DrContact, DrClinic};
                pat_info= new String[]{PatName, PatAge, PatGender};
                //signURL=txtSignURL.getText().toString();
                PatHTML=PrescriptionHTML.getPatInfo(pat_info, todayDate, PrescriptionNo);
                DocHTML=PrescriptionHTML.getDocInfo(doc_info);
                Head=PrescriptionHTML.getHeadHTML();
                Footer=PrescriptionHTML.getFooterHTML(DrName);
                HTML=Head+DocHTML+PatHTML+DiagnosisHTML+PresHTML+InfoHTML+Footer;
                Intent HTMLInt=new Intent(ConfirmProfile.this, ViewHTML.class);
                HTMLInt.putExtra("HTML", HTML);
                HTMLInt.putExtra("Prescription No", PrescriptionNo);
                HTMLInt.putExtra("Date", todayDate);
                startActivity(HTMLInt);
            }
        });
    }

    //Function to display Doctor Profile in their respective TextView
    private void displayDocInfo() {
        realRef.child("Profile Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //Loads a snapshot of all children of given branch
                messageBox.setText("Details Fetched Successfully");
                messageBox.setTextColor(success);
                int j=1;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String snap_value=snapshot1.getValue().toString();
                    String snap_name= snapshot1.getKey();
                    String snap_decrypt=CaesarCipher.decrypt(snap_value);
                    //Alphabetical Order: Aadhar No, Clinic, Contact, First Name, Last Name, Qualifications, Registration No
                    switch (snap_name)
                    {
                        case "Qualifications":
                            drQualifications.setText(snap_decrypt);
                            break;
                        case "Contact":
                            drContact.setText(snap_decrypt);
                            break;
                        case "Clinic":
                            drClinic.setText(snap_decrypt);
                        case "Registration No":
                        case "First Name":
                        case "Last Name":
                        case "Aadhar No":
                            break;
                        default:
                            Toast.makeText(ConfirmProfile.this, "An Error Occurred while loading content", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmProfile.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                messageBox.setText("Error: "+error);
                messageBox.setTextColor(warn);
            }
        });
    }
}
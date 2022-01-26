package com.example.prescribeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class UpdateSignature extends AppCompatActivity {

    private final int IMAGE_REQUEST_CODE = 786;
    final int ONE_MEGABYTE=1024+1024;
    protected Uri imageUri;
    protected String UserID, signName;
    protected Bitmap signBit;

    ImageView current, selected;
    Button select, upload;
    LinearLayout selection;
    TextView messageBox;

    int warn, success;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference signRef;
    FirebaseDatabase realDB;
    DatabaseReference realRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_signature);
        warn= ContextCompat.getColor(UpdateSignature.this, R.color.red); //Saving RGB value of red color as int
        success= ContextCompat.getColor(UpdateSignature.this, R.color.foreground); //Saving RGB value of foreground(gold/navy-blue) as int

        mAuth=FirebaseAuth.getInstance(); //FireBase Authentication Instance
        user=mAuth.getCurrentUser(); //Getting Current User
        UserID=user.getUid(); //Unique User ID of User
        signName=UserID+".jpg"; //Signature Name will be UserID.jpg
        storage=FirebaseStorage.getInstance(); //Instance of FireBase Storage (for Sign)
        signRef=storage.getReference().child("Signatures").child(signName); //Getting Reference of Sign on FireBase
        realDB=FirebaseDatabase.getInstance(); //Instance of FireBase Database (for Profile)
        realRef=realDB.getReference().child(UserID); //Getting Reference of User Profile

        current=(ImageView) findViewById(R.id.imgCurrentSign);
        selected=(ImageView) findViewById(R.id.imgSelectSign);
        selection=(LinearLayout) findViewById(R.id.SelectedSignLL);
        messageBox=(TextView) findViewById(R.id.messageBoxSign);

        messageBox.setText("Loading Signature...");
        messageBox.setTextColor(warn);
        signRef.getBytes(6*1024*1024).addOnSuccessListener(bytes -> { //getBytes downloads the ByteArray of Signature on FireBase
            messageBox.setText("Image Loaded Successfully");
            messageBox.setTextColor(success);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length); //Decoding the BitMap of Signature downloaded
            current.setImageBitmap(bitmap); //Setting Image in ImageView
        }).addOnFailureListener(e -> {
            messageBox.setText("Image Loading Error: "+e);
            messageBox.setTextColor(warn);
        });

        select=(Button) findViewById(R.id.btnSignSelect);
        select.setOnClickListener(v -> openImage());

        upload=(Button) findViewById(R.id.btnSignUpload);
        upload.setOnClickListener(v -> compressImage());
    }

    //Function to open File Explorer for Selection of Image
    private void openImage() {
        Intent imageIntent=new Intent();
        imageIntent.setType("image/jpeg"); //Setting Type of File to be fetched as "jpeg" Images only
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent, IMAGE_REQUEST_CODE);
    }

    //Use this method when calling any Google API eg. Speech to Text, Sign In, etc...
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri=data.getData(); //Getting URI of Image selected
                selected.setImageURI(imageUri); //Setting Image Selected using URI
                selection.setVisibility(View.VISIBLE);
                messageBox.setText("Image Accessed Successfully");
                messageBox.setTextColor(success);
                try {
                    signBit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri); //Getting BitMap of Image Selected
                    upload.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    messageBox.setText("Error: "+e);
                    messageBox.setTextColor(warn);
                }
            }
        }
    }

    //Function to Compress the Selected Image before Uploading
    private void compressImage() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        signBit.compress(Bitmap.CompressFormat.JPEG, 40, bytes); //Compressing Image Selected
        String path = MediaStore.Images.Media.insertImage(UpdateSignature.this.getContentResolver(),signBit,signName,null); //Getting Path of Newly Compressed Image
        Uri uri = Uri.parse(path); //Converting Path into URI
        if (uri != null){
            messageBox.setText("Compression Successful!");
            messageBox.setTextColor(success);
        }
        uploadImage(uri); //Calling Function to Upload Signature
    }

    //Function to Upload Signature to FireBase
    private void uploadImage(Uri uri) {
        if (uri!=null)
        {
            messageBox.setText("Uploading Signature...");
            messageBox.setTextColor(warn);
            signRef.putFile(uri).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    messageBox.setText("Image Uploaded Successfully");
                    messageBox.setTextColor(success);
                    realRef.child("Sign Uploaded").setValue(true); //Setting Value of Sign Uploaded as true in Profile
                    Toast.makeText(UpdateSignature.this, "Signature Upload Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateSignature.this, UpdateSignature.class); //Reload the Same Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Cannot go 'Back' to earlier instance of this activity
                    startActivity(intent);
                }
                else{
                    messageBox.setText("Error: "+task.getException());
                    messageBox.setTextColor(warn);
                }
            });
        }
        else {
            messageBox.setText("URI is Empty");
            messageBox.setTextColor(warn);
        }
    }
}
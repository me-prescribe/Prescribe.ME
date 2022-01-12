package com.example.prescribeme;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//This class determines whether or not user is signed in
public class Home extends Application {

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        if(user!=null) //i.e. user does exist/is signed in
        {
            Intent intent;
            intent=new Intent(Home.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); //User cannot go back
            startActivity(intent);
        }
        else {
            Intent intent;
            intent = new Intent(Home.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}

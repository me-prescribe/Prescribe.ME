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
            if(user.isEmailVerified()) //Check if User's Email is Verified or not
            {
                Intent intent;
                intent=new Intent(Home.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                Intent verifyInt=new Intent(Home.this, EmailVerification.class);
                verifyInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(verifyInt);
            }
        }
        else {
            Intent intent;
            intent = new Intent(Home.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}

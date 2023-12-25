package com.anmol.easyauthenticationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.anmol.easyauthenticationapp.utils.FirebaseUtil;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(FirebaseUtil.isLoggedIn() || GoogleSignIn.getLastSignedInAccount(SplashScreen.this)!=null
                        || (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired())){
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                }
                else {
                    startActivity(new Intent(SplashScreen.this, LoginPhoneNumberScreen.class));
                }
                finish();
            }
        }, 1000);
    }
}
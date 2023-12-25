package com.anmol.easyauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anmol.easyauthenticationapp.model.UserModel;
import com.anmol.easyauthenticationapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameScreen extends AppCompatActivity {

    EditText usernameInput;
    Button submitButton;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username_screen);

        usernameInput = findViewById(R.id.githubEmail);
        submitButton = findViewById(R.id.githubNextButton);
        progressBar = findViewById(R.id.loginProgressBar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        submitButton.setOnClickListener((v -> {
            setUsername();
        }));

    }

    void setUsername(){

        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length()<3){
            usernameInput.setError("Username length should be at least 3 characters");
            return;
        }

        setInProgress(true);

        if(userModel!=null){
            userModel.setUsername(username);
        }
        else{
            userModel = new UserModel(phoneNumber, username, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Success OTP login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginUsernameScreen.this, MainActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        });

    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }
}
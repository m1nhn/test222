package com.anmol.easyauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GithubAuth extends AppCompatActivity {

    EditText githubEmail;
    Button githubNextButton;
    String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_auth);

        githubEmail = findViewById(R.id.githubEmail);
        githubNextButton = findViewById(R.id.githubNextButton);
        firebaseAuth = FirebaseAuth.getInstance();

        githubNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = githubEmail.getText().toString();

                if(!email.matches(emailPattern)){
                    Toast.makeText(GithubAuth.this, "Enter a proper email", Toast.LENGTH_SHORT).show();
                }
                else{
                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                    provider.addCustomParameter("login", email);

                    List<String> scopes =
                            new ArrayList<String>() {
                                {
                                    add("user:email");
                                }
                            };
                    provider.setScopes(scopes);

                    Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
                    if (pendingResultTask != null) {
                        pendingResultTask
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GithubAuth.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    } else {
                        firebaseAuth
                                .startActivityForSignInWithProvider(GithubAuth.this, provider.build())
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                //Get GitHub username and email.
                                                String githubUsername = authResult.getAdditionalUserInfo().getUsername();
                                                String githubEmail = authResult.getAdditionalUserInfo().getProfile().get("email").toString();

                                                //Save GitHub username and email to Firebase.
                                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                if (currentUser != null) {
                                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                                                    userRef.child("githubUsername").setValue(githubUsername);
                                                    userRef.child("githubEmail").setValue(githubEmail);
                                                }

                                                //Open MainActivity and pass GitHub details.
                                                openNextActivity(githubUsername, githubEmail);
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GithubAuth.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    }
                }
            }
        });

    }

    private void openNextActivity(String githubUsername, String githubEmail) {
        // Intent to send GitHub username and email to MainActivity
        Intent intent = new Intent(GithubAuth.this, MainActivity.class);
        intent.putExtra("githubUsername", githubUsername);
        intent.putExtra("githubEmail", githubEmail);
        Toast.makeText(getApplicationContext(), "Success GitHub login", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();

    }
}
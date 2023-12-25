package com.anmol.easyauthenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name, email;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        name = findViewById(R.id.homeName);
        email = findViewById(R.id.homeEmail);
        logout = findViewById(R.id.logoutButton);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        //Check if the user is logged in via Google.
        if(gAccount!=null){
            //Fetching Google details and setting text.
            String personName = gAccount.getDisplayName();
            String personEmail = gAccount.getEmail();

            name.setText("Name : "+personName);
            email.setText("Email : "+personEmail);
        }
        else{
            //User is not logged in via Google. Check if logged in via Facebook.
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if(accessToken != null && !accessToken.isExpired()){
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    //Fetching Facebook details and setting text.
                                    String fullName = object.getString("name");
                                    String id = object.getString("id");

                                    name.setText("Name : "+fullName);
                                    email.setText("Facebook ID : "+id);
                                }
                                catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            }
            else{
                //Getting data from previous activity.
                Intent intent = getIntent();
                if (intent != null) {
                    String githubUsername = intent.getStringExtra("githubUsername");
                    String githubEmail = intent.getStringExtra("githubEmail");

                    if (githubUsername != null && githubEmail != null) {
                        // If GitHub details are available, set the text in the UI
                        name.setText("GitHub Username : " + githubUsername);
                        email.setText("GitHub Email : " + githubEmail);
                    }
                    else{
                        String phoneNumber = intent.getStringExtra("phone");
                        String username = intent.getStringExtra("username");

                        //setting mobile number and username.
                        if (username != null) {
                            name.setText("Username : " + username);
                        }
                        if (phoneNumber != null) {
                            email.setText("Mobile number : " + phoneNumber);
                        }
                    }
                }
            }
        }

        //Logout button.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    //SignOut method.
    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this, LoginPhoneNumberScreen.class));
            }
        });
    }

}
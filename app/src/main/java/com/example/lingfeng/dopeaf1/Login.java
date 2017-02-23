package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.UUID;

public class Login extends AppCompatActivity {

    //Use for google login
    //private GoogleApiClient mGoogleApiClient;
    public static User loggedin;
    public DatabaseReference mDatabase;
    //private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnForgotPassword;
    private Button btnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //View as current page
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnSignUp = (Button) findViewById(R.id.register);
        btnForgotPassword = (Button) findViewById(R.id.forgotPassword);
        btnGoogleSignIn = (Button) findViewById(R.id.googleBtn);

       /*
        btnGooglePlus = (SignInButton) findViewById(R.id.googleSignin);


        //Use for google sign in,Default sign in and request the user mail
        //info from the Google server side
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        */





        if(loggedin != null) {
            email.setText(loggedin.getUserEmail());
            password.setText(loggedin.getUserPassword());
        }

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //btnGooglePlus.setOnClickListener(this);

        //triggered when click on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if email and password are valid
                //
                //todo: add more checks to the format
                if ((email.getText().length() > 0) && (password.getText().length() > 5)) {

                    final String emailU = email.getText().toString();
                    final String passwordU = password.getText().toString();

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        int flag = 0;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //TODO: update searching to hashmap
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //search through each user
                                User user = snapshot.getValue(User.class);

                                //if the email and password all match
                                if (emailU.equals(user.getUserEmail()) && passwordU.equals(user.getUserPassword())) {
                                    loggedin = user;
                                    Toast.makeText(Login.this, "Hello " + loggedin.getUsername(), Toast.LENGTH_SHORT).show();
                                    flag = 1;
                                    //define a jump
                                    Intent intent = new Intent(Login.this, AddClass.class);

                                    loggedin.updateLastlogin();
                                    mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                                    //jump to add class
                                    startActivity(intent);

                                    //if the email matches but password does not match
                                } else if (flag != 1 && emailU.equals(user.getUserEmail()) && !passwordU.equals(user.getUserPassword())) {
                                    flag = 2;
                                }
                            }

                            // if user enters a wrong password but valid email
                            if (flag == 2) {
                                Toast.makeText(Login.this, "Wrong email or password!", Toast.LENGTH_SHORT).show();

                            // if user enters new contents
                            } else if (flag == 0) {
                                Toast.makeText(Login.this, "User Not Exist!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else if(email.getText().length() < 1){
                    //notify user that the email is invalid
                    Toast.makeText(Login.this, "Email address has not entered yet!", Toast.LENGTH_SHORT).show();
                } else if(password.getText().length() <= 5) {
                    Toast.makeText(Login.this, "Password should have at least 6 characters!", Toast.LENGTH_SHORT).show();
                }
            }
        });





        //triggered when click on login button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if email and password are valid
                //
                //todo: add more checks to the format

                    final String emailU = email.getText().toString();
                    final String passwordU = password.getText().toString();

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        int flag = 0;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            //TODO: Use a random unique user ID instead
                            String uniqueID = UUID.randomUUID().toString();

                            boolean unique = false;
                            while(!unique) {
                                unique = true;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(snapshot.getValue(User.class).getUserID().toString().equals(uniqueID)) {
                                        unique = false;
                                    }
                                }
                            }

                            final User userNew = new User("NEW USER", emailU, uniqueID, passwordU);
                            //put user into users field
                            Toast.makeText(Login.this, "successfully added " + userNew.getUsername(), Toast.LENGTH_SHORT).show();
                            loggedin = userNew;
                            Toast.makeText(Login.this, "Hello " + loggedin.getUsername(), Toast.LENGTH_SHORT).show();

                            //define a jump
                            //TODO: change the view
                            Intent intent = new Intent(Login.this, Signup.class);

                            loggedin.updateLastlogin();
                            //mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                            //jump to add class
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

            }
        });

        //triggered when click on forgot password button
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* redirect to page for confirming information and retrieving information */

                //define a jump
                //TODO: change the view, and may need to PASS the EMAIL user entered, so that they does not need to enter it again
                Intent intent = new Intent(Login.this, AddClass.class);
                final User userNew = new User("Forgot Password", "Forgot Password", "Forgot Password", "Forgot Password");
                loggedin = userNew;
                mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                //jump to add class
                startActivity(intent);
            }
        });

        //triggered when click on forgot password button
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* redirect to page for confirming information and retrieving information */

                //define a jump
                //TODO: change the view, and may need to PASS the EMAIL user entered, so that they does not need to enter it again
                //Intent intent = new Intent(Login.this, GoogleSignIn.class);
                //startActivity(intent);
            }
        });
    }

}

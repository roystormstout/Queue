package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public static User loggedin;
    public static GoogleApiClient mGoogleApiClient;
    public DatabaseReference mDatabase;
    private EditText email;
    private EditText password;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnForgotPassword;
    private SignInButton googleSignin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnSignUp = (Button) findViewById(R.id.register);
        btnForgotPassword = (Button) findViewById(R.id.forgotPassword);
        googleSignin = (SignInButton) findViewById(R.id.sign_in_button);

        if (loggedin != null) {
            email.setText(loggedin.getUserEmail());
            password.setText(loggedin.getUserPassword());
        }

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

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

                                    revokeAccess();

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
                } else if (email.getText().length() < 1) {
                    //notify user that the email is invalid
                    Toast.makeText(Login.this, "Email address has not entered yet!", Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() <= 5) {
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
                        while (!unique) {
                            unique = true;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getValue(User.class).getUserID().toString().equals(uniqueID)) {
                                    unique = false;
                                }
                            }
                        }

                        final User userNew = new User("NEW USER", emailU, uniqueID, passwordU);
                        //put user into users field

                        loggedin = userNew;

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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("700971486111-difngrpa9942udtnr6s3f0vap9qiojjq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    final String name = user.getDisplayName();
                    final String email = user.getEmail();
                    //Toast.makeText(Login.this, "successfully added " + name, Toast.LENGTH_SHORT).show();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    String uid = user.getUid();

                    final User userNew = new User(name, email, uid, "123456");
                    //put user into users field
                    loggedin = userNew;

                    //define a jump
                    //TODO: change the view
                            /*if(mGoogleApiClient.isConnected()){
                                Intent intent = new Intent(Login.this, AddClass.class);

                                loggedin.updateLastlogin();

                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(Login.this, Signup.class);

                                loggedin.updateLastlogin();

                                startActivity(intent);
                            }*/
                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        int flag = 0;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //TODO: update searching to hashmap
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //search through each user
                                User user = snapshot.getValue(User.class);

                                //if the email and password all match
                                if (email.equals(user.getUserEmail()) && name.equals(user.getUsername())) {
                                    //define a jump
                                    Toast.makeText(Login.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, AddClass.class);
                                    flag = 1;
                                    loggedin.updateLastlogin();
                                    //jump to add class
                                    startActivity(intent);
                                    break;
                                    //if the email matches but password does not match
                                }
                            }

                            // if user enters a wrong password but valid email
                            if (flag == 0) {
                                Toast.makeText(Login.this, "successfully added " + name, Toast.LENGTH_SHORT).show();
                                Toast.makeText(Login.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Signup.class);
                                loggedin.updateLastlogin();
                                startActivity(intent);
                                // if user enters new contents
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

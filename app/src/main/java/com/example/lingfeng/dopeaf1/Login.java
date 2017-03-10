package com.example.lingfeng.dopeaf1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

import static android.R.string.ok;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public static User loggedin;
    public static GoogleApiClient mGoogleApiClient;
    public DatabaseReference mDatabase;
    private EditText email;
    private EditText password;
    private Button btnLogin;
    //private Button btnSignUp;
    private Button btnForgotPassword;
    private SignInButton googleSignin;
    //private CheckBox rememberMe;
    //private CheckBox autoLogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String username;
    private String pswd;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        //btnLogin = (Button) findViewById(R.id.login);
        //btnSignUp = (Button) findViewById(R.id.register);
        btnForgotPassword = (Button) findViewById(R.id.forgotPassword);
        googleSignin = (SignInButton) findViewById(R.id.sign_in_button);
        //rememberMe = (CheckBox)findViewById(R.id.rememberme);
        //autoLogin = (CheckBox)findViewById(R.id.autoLogin);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
/*
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            email.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
            autoLogin.setChecked(loginPreferences.getBoolean("autoLogin", false));
        }
        */

        googleSignin.setColorScheme(0);
        TextView textView = (TextView) googleSignin.getChildAt(0);
        textView.setText("Sign in with Google");

        if (loggedin != null) {
            email.setText(loggedin.getUserEmail());
            password.setText(loggedin.getUserPassword());
        }


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


        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "MTFKKKKK onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "MTFKKKK onAuthStateChanged:signed_out");
                    //Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };




        //TODO
        /*
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

                    mAuth.signInWithEmailAndPassword(emailU, passwordU)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Failes", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(Login.this, "Success", Toast.LENGTH_LONG).show();

                                        FirebaseUser userB = task.getResult().getUser();
                                        if(!userB.isEmailVerified()){
                                            Toast.makeText(Login.this, userB.getEmail() + " Not Email verified", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(Login.this, userB.getEmail() + " Email verified", Toast.LENGTH_LONG).show();
                                            loggedin = new User("New User",userB.getEmail(),userB.getUid(),"password");
                                            //Toast.makeText(Login.this, userB.getDisplayName(), Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Login.this, Navigation.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
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

        */

        //TODO FORGOT PSWD
        //triggered when click on forgot password button
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* redirect to page for confirming information and retrieving information */

                //define a jump
                //TODO: change the view, and may need to PASS the EMAIL user entered, so that they does not need to enter it again
                Intent intent = new Intent(Login.this, Navigation.class);
                final User userNew = new User("Forgot Password", "Forgot Password", "Forgot Password", "Forgot Password");
                loggedin = userNew;
                mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                //jump to add class
                startActivity(intent);
            }
        });
/*
        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

                username= email.getText().toString();
                pswd = password.getText().toString();

                if(!rememberMe.isChecked()) {
                    autoLogin.setChecked(false);
                }

                if(rememberMe.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", username);
                    loginPrefsEditor.putString("password", pswd);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                //Do something here if needed
            }
        });

        autoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(autoLogin.isChecked() && rememberMe.isChecked()) {
                    loginPrefsEditor.putBoolean("autoLogin", true);
                    loginPrefsEditor.commit();
                } else if(autoLogin.isChecked() && !rememberMe.isChecked()) {
                    rememberMe.performClick();
                    loginPrefsEditor.putBoolean("autoLogin", true);
                    loginPrefsEditor.commit();
                }
                else {
                    loginPrefsEditor.putBoolean("autoLogin", false);
                    loginPrefsEditor.commit();
                }
            }
        });

        */



        System.out.println("Process to this step!");

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

                System.out.println("Finished signIn");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                System.out.println("Firebase authen user");

                if (user == null){
                    System.out.println("The user is currently a null object dude");
                }
                if (user != null) {
                    // Name, email address, and profile photo Url
                    final String name = user.getDisplayName();
                    final String email = user.getEmail();

                    System.out.println(name+" FUCKKKKKK "+email);
                    //Toast.makeText(Login.this, "successfully added " + name, Toast.LENGTH_SHORT).show();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    final String uid = user.getUid();

                    //
                    //put user into users field


                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        int flag = 0;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //TODO: update searching to hashmap
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //search through each user
                                User user = snapshot.getValue(User.class);

                                //if the email and password all match
                                if (uid.equals(user.getUserID())) {
                                    //define a jump
                                    loggedin=user;
                                    Toast.makeText(Login.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Navigation.class);
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
                                final User userNew = new User(name, email, uid, "123456");
                                loggedin=userNew;
                                mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
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
//        if(autoLogin.isChecked()) {
//            btnLogin.performClick();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    private void signIn() {
        System.out.println("Entering the signIn(), line 404");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        System.out.println("Entering firebase Auth with google");
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
                            System.out.println("Clicked Login and success");
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            System.out.println("Clicked Login and failed");
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

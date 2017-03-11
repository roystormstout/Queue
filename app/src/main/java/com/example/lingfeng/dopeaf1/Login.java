package com.example.lingfeng.dopeaf1;

import android.app.ProgressDialog;
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
import com.google.android.gms.common.api.OptionalPendingResult;
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
    private Button btnSignUp;
    private Button btnForgotPassword;
    private SignInButton googleSignin;
    private CheckBox rememberMe;
    private CheckBox autoLogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private String username;
    private String pswd;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        //email = (EditText) findViewById(R.id.email);
        //password = (EditText) findViewById(R.id.password);
        //btnLogin = (Button) findViewById(R.id.login);
        //btnSignUp = (Button) findViewById(R.id.register);
        btnForgotPassword = (Button) findViewById(R.id.forgotPassword);
        googleSignin = (SignInButton) findViewById(R.id.sign_in_button);
        //rememberMe = (CheckBox)findViewById(R.id.rememberme);
        //autoLogin = (CheckBox)findViewById(R.id.autoLogin);
        //loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        //loginPrefsEditor = loginPreferences.edit();
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
/*
        if (loggedin != null) {
            email.setText(loggedin.getUserEmail());
            password.setText(loggedin.getUserPassword());
        }
*/
        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
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
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("700971486111-pijpun7dhks9l0bkc38glmdh80pv6p4f.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // Name, email address, and profile photo Url
                    final String name = user.getDisplayName();
                    final String email = user.getEmail();
                    //Toast.makeText(Login.this, "successfully added " + name, Toast.LENGTH_SHORT).show();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    final String uid = user.getUid();

                    //final User userNew =

                    //put user into users field
                    loggedin = new User(name, email, uid, "123456");

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            boolean isNewUser = true;

                            //TODO: update searching to hashmap
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //search through each user
                                User user = snapshot.getValue(User.class);

                                //if the email and password all match
                                if (uid.equals(user.getUserID())) {
                                    //define a jump
                                    System.err.println("Found the user in database!!");
                                    loggedin = user;
                                    Toast.makeText(Login.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(Login.this, Navigation.class);
                                    loggedin.updateLastlogin();
                                    //jump to add class
                                    //startActivity(intent);
                                    finish();
                                    break;
                                    //if the email matches but password does not match
                                }
                            }

                            mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                            Intent intent = new Intent(Login.this, Navigation.class);
                            loggedin.updateLastlogin();
                            //jump to Navigation class
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

        googleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        /*
        if(autoLogin.isChecked()) {
            btnLogin.performClick();
        }
        */
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
        //Toast.makeText(Login.this, "SignIn 1", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Login.this, (result.getStatus()).toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Login.this, "SigIn Fails", Toast.LENGTH_SHORT).show();
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            //Signed in successfully, show authenticated UI.
            //Intent intent = new Intent(this, Navigation.class);
            //startActivity(intent);
            finish();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}

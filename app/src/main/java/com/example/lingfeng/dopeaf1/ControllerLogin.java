package com.example.lingfeng.dopeaf1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

import static com.example.lingfeng.dopeaf1.R.layout.activity_login;

import java.io.File;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ControllerLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public static User loggedin;
    public static Uri personPhoto = null; //get the photo of the user
    public static String personName; //ge the name of the user
    public static String personEmail; // get the email of the user
    public static GoogleApiClient mGoogleApiClient;
    public DatabaseReference mDatabase;

    private Button btnForgotPassword;
    private SignInButton googleSignin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(activity_login);
        ImageView myImageView= (ImageView) findViewById(R.id.imageView);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        myImageView.startAnimation(myFadeInAnimation);


        System.err.println("Enter Login class");

        googleSignin = (SignInButton) findViewById(R.id.sign_in_button);

        googleSignin.setColorScheme(1);
        TextView textView = (TextView) googleSignin.getChildAt(0);
        textView.setText("Sign In");

        System.err.println("Finished set textview");
        System.err.println("...........");

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //TODO FORGOT PSWD
        //triggered when click on forgot password button
//        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /* redirect to page for confirming information and retrieving information */
//
//                //define a jump
//                //TODO: change the view, and may need to PASS the EMAIL user entered, so that they does not need to enter it again
//                Intent intent = new Intent(ControllerLogin.this, ViewNavigation.class);
//                final User userNew = new User("Forgot Password", "Forgot Password", "Forgot Password");
//                loggedin = userNew;
//                mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
//                //jump to add class
//                startActivity(intent);
//            }
//        });

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
                    loggedin = new User(name, email, uid);

                    mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        //
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            boolean isNewUser = true;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                //search through each user
                                User user = snapshot.getValue(User.class);

                                //if the email and password all match
                                if (uid.equals(user.getUserID())) {
                                    //define a jump
                                    System.err.println("Found the user in database!!");
                                    loggedin = user;
                                    Toast.makeText(ControllerLogin.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                    isNewUser = false;
                                    finish();
                                    break;
                                    //if the email matches but password does not match
                                }
                            }
                            if (isNewUser) {
                                String instructionMessage = "Dear " + loggedin.getUsername() + ":<br />" + "<br />" + "We are glad that you are using the product that we proudly provide, Queue.<br />" +
                                        "<br />" + "To provide you with a decent using experience, we have prepare the extension that would facilitate your using of our product.<br />" +
                                        "<br />" + "In the following links, you would be able to download the files and you would use them to add courses and task automatically. <b>Please put the download files in download directory.</b><br />" +
                                        "<br />" + "<b>https://drive.google.com/file/d/0B5cN00gQX5FebTlxaUUzSXpsVG8/view?usp=sharing<br /><br />https://drive.google.com/a/ucsd.edu/file/d/0B7Cy6hs14SfedHNoZHdwOV94bzg/view?usp=sharing</b><br />" + "<br />" + "This is your UID, copy it and paste it in the terminal when the application ask you to do so.<br />" +
                                        "<br /><b>" + loggedin.getUserID()  + "<br />" + "<br />" + "Instruction:<br />" + "Open your terminal, type in:<br />" + "<br />" + "cd downloads<br />" + "<br />" + "    2.   Type in:<br />" +
                                        "chmod 777 Queue.sh<br />" + "<br />" + "    3.  Type in:<br />" + "./Queue.sh</b><br />" + "<br />" + "And then following the instruction in the application. Enjoy the using of our application.<br />" +
                                        "<br />" + "<br />" + "Your Sincerely,<br />" + "<br />" + "Team E.X.C.I.T.E.D.<br />" + "<br />\n\n\n\n\n\n\n\n\n\n<br /><br /><br /><br /><br />";
                                sendMail(loggedin.getUserEmail(), "Instruction for Importing Your Classes", instructionMessage);
                            }

                            mDatabase.child("users").child(loggedin.getUserID()).setValue(loggedin);
                            Intent intent = new Intent(ControllerLogin.this, ViewNavigation.class);

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

        System.err.println("On Start");
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                personPhoto = account.getPhotoUrl();
                personEmail = account.getEmail();
                personName = account.getDisplayName();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(ControllerLogin.this, (result.getStatus()).toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ControllerLogin.this, "SigIn Fails", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ControllerLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfolly, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();;
            personPhoto = acct.getPhotoUrl();
            personEmail = acct.getEmail();
            personName = acct.getDisplayName();
            showProgressDialog();
            //finish();
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

    protected void sendMail(String email_to, String subject, String main_message) {
        final String username = "cse110.queue@gmail.com";
        final String password = "queuedopeaf";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", 587);
//        props.put("mail.smtp.socketFactory.port", 465);
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Log.d(TAG, "Ready to Messaging");
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email_to));
            message.setSubject("Sent from DopeAF: " + subject);
            message.setContent(main_message, "text/html;charset=utf-8");

            new SendMailTask().execute(message);
            Log.d(TAG, "Finish Messaging");
        }catch (MessagingException mex) {
            Log.d(TAG, "Fail to Messaging");
            mex.printStackTrace();
        }


    }

    private class SendMailTask extends AsyncTask<Message,String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            }
            catch(SendFailedException ee)
            {
                return "error1";
            }catch (MessagingException e) {
                return "error2";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Success"))
            {
                super.onPostExecute(result);
                Toast.makeText(ControllerLogin.this, "Mail Sent Successfully", Toast.LENGTH_LONG).show();
            }
            else
            if(result.equals("error1"))
                Toast.makeText(ControllerLogin.this, "Email Failure", Toast.LENGTH_LONG).show();
            else
            if(result.equals("error2"))
                Toast.makeText(ControllerLogin.this, "Email Sent problem2", Toast.LENGTH_LONG).show();

        }
    }

}

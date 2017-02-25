package com.example.lingfeng.dopeaf1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Signup extends AppCompatActivity {

    public DatabaseReference mDatabase;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText UCSDEmail;
    private EditText UCSDPassword;
    private Button btnRegister;
    private Button btnBack;
    public final User user = Login.loggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_signup);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        UCSDEmail = (EditText) findViewById(R.id.UCSDEmail);
        UCSDPassword = (EditText) findViewById(R.id.UCSDPassword);
        btnRegister = (Button) findViewById(R.id.register);
        btnBack = (Button) findViewById(R.id.back);

        email.setText(user.getUserEmail());
        password.setText(user.getUserPassword());
        confirmPassword.requestFocus();

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //triggered when click on register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    int flag = 0;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check if email and password are valid, and the confirm password matches the password above it
                        //
                        //todo: add more checks to the format
                        if ((email.getText().length() > 0) && (password.getText().length() > 5) &&
                                (confirmPassword.getText().toString().equals(password.getText().toString())) &&
                                (UCSDEmail.getText().length() > 0) && (UCSDPassword.getText().length() > 5)) {

                            final String emailU = email.getText().toString();
                            final String passwordU = password.getText().toString();
                            final String UCSDEmailU = UCSDEmail.getText().toString();
                            final String UCSDPasswordU = UCSDPassword.getText().toString();

                            user.setUserEmail(emailU);
                            user.setUserPassword(passwordU);

                            //check if the UCSD email has been used
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User temp = snapshot.getValue(User.class);

                                if(temp.getUCSDEmail().toString().equals(UCSDEmailU)) {
                                    flag = 1;
                                }
                            }
                            //if not used, store it
                            if(flag == 0) {
                                user.setUCSDAccount(UCSDEmailU, UCSDPasswordU);
                            }

                            user.updateLastlogin();
                            Toast.makeText(Signup.this, "successfully added " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            mDatabase.child("users").child(user.getUserID()).setValue(user);

                            //jump to main page
                            Intent intent = new Intent(Signup.this, AddClass.class);
                            startActivity(intent);

                        } else if(email.getText().length() < 1){
                            //notify user that the email is invalid
                            Toast.makeText(Signup.this, "Email address has not entered yet!", Toast.LENGTH_SHORT).show();
                        } else if(password.getText().length() <= 5) {
                            Toast.makeText(Signup.this, "Password should have at least 6 characters!", Toast.LENGTH_SHORT).show();
                        } else if(!confirmPassword.getText().toString().equals(password.getText().toString())) {
                            Toast.makeText(Signup.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        } else if(UCSDEmail.getText().length() < 1) {
                            Toast.makeText(Signup.this, "UCSD Email address has not entered yet!", Toast.LENGTH_SHORT).show();
                        } else if(UCSDPassword.getText().length() <= 5) {
                            Toast.makeText(Signup.this, "UCSD Password should have at least 6 characters!", Toast.LENGTH_SHORT).show();
                        } else if(flag == 1) {
                            Toast.makeText(Signup.this, "This UCSD Email has been registered by another account!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        //triggered when click on back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send user back to login view
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });
    }
}


package com.example.lingfeng.dopeaf1;

import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseAuth mAuth;

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
                String emailU = email.getText().toString().trim();
                String passwordU = password.getText().toString().trim();
                FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();

                final String emailB = email.getText().toString();
                final String UCSDEmailU = UCSDEmail.getText().toString();
                final String UCSDPasswordU = UCSDPassword.getText().toString();
                if(email.getText().length() < 1){
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
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(emailU, passwordU)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(Signup.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    //progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in useår can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Signup.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Signup.this, "Authentication success." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser userB = task.getResult().getUser();
                                        userB.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Signup.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            int flag = 0;

                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                user.setUserEmail(emailB);

                                                //check if the UCSD email has been used
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    User temp = snapshot.getValue(User.class);

                                                    if (temp.getUCSDEmail().toString().equals(UCSDEmailU)) {
                                                        flag = 1;
                                                    }
                                                }
                                                //if not used, store it
                                                if (flag == 0) {
                                                    user.setUCSDAccount(UCSDEmailU, UCSDPasswordU);
                                                }

                                                user.updateLastlogin();
                                                mDatabase.child("users").child(user.getUserID()).setValue(user);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        mAuth.signOut();
                                        Intent intent = new Intent(Signup.this, Login.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                }

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


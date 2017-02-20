package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    public static User loggedin;
    public DatabaseReference mDatabase;
    private EditText email;
    private EditText password;
    private Button btnLogin;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnForgotPassword = (Button) findViewById(R.id.forgotPassword);

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //triggered when click on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if email and password are valid
                //
                //todo: add more checks to the format
                if ((email.getText().length() > 1) && (password.getText().length() > 5)) {

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
                                if (emailU.equals(user.email) && passwordU.equals(user.password)) {
                                    loggedin = user;
                                    Toast.makeText(Login.this, "Hello " + loggedin.username, Toast.LENGTH_SHORT).show();
                                    flag = 1;
                                    //define a jump
                                    Intent intent = new Intent(Login.this, AddClass.class);

                                    loggedin.updateLastlogin();
                                    mDatabase.child("users").child(loggedin.userID).setValue(loggedin);
                                    //jump to add class
                                    startActivity(intent);

                                    //if the email matches but password does not match
                                } else if (flag != 1 && emailU.equals(user.email) && !passwordU.equals(user.password)) {
                                    flag = 2;
                                }
                            }

                            // if user enters a wrong password but valid email
                            if (flag == 2) {
                                Toast.makeText(Login.this, "Wrong email or password!", Toast.LENGTH_SHORT).show();

                                // if user enters new contents
                            } else if (flag == 0) {
                                final User userNew = new User("NEW USER", emailU, "NEW ID", passwordU);
                                //put user into users field
                                Toast.makeText(Login.this, "successfully added " + userNew.username, Toast.LENGTH_SHORT).show();
                                loggedin = userNew;
                                Toast.makeText(Login.this, "Hello " + loggedin.username, Toast.LENGTH_SHORT).show();

                                //define a jump
                                //TODO: change the view
                                Intent intent = new Intent(Login.this, AddClass.class);

                                loggedin.updateLastlogin();
                                mDatabase.child("users").child(loggedin.userID).setValue(loggedin);
                                //jump to add class
                                startActivity(intent);
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
                mDatabase.child("users").child(loggedin.userID).setValue(loggedin);
                //jump to add class
                startActivity(intent);
            }
        });
    }
}

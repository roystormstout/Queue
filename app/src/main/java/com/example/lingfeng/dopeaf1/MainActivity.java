package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText userID;
    private EditText name;
    private EditText password;

    private Button btnSignup;

    private Button btnLogin;

    public DatabaseReference mDatabase;
    public static User loggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.email);
        userID = (EditText) findViewById(R.id.userid);
        name  = (EditText) findViewById(R.id.name);
        password  = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnSignup = (Button) findViewById(R.id.add_user);

        //connect to our own database using google-services.json
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //triggered when click on login button
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                //check if email and password are valid
                //
                //todo: add more checks to the format
                if ((email.getText().length() > 1) && (password.getText().length()>5)){


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
                                        if(emailU.equals(user.email)&&passwordU.equals(user.password)) {
                                            loggedin = user;
                                            Toast.makeText(MainActivity.this, "Hello "+loggedin.username, Toast.LENGTH_SHORT).show();
                                            flag=1;
                                            //define a jump
                                            Intent intent = new Intent(MainActivity.this, AddClass.class);

                                            loggedin.updateLastlogin();
                                            mDatabase.child("users").child(loggedin.userID).setValue(loggedin);
                                            //jump to add class
                                            startActivity(intent);
                                        }
                                    }
                                    if(flag==0) {
                                        Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else{

                    //notify user that the fields are invalid
                    Toast.makeText(MainActivity.this, "Please enter valid value in all fields for login", Toast.LENGTH_SHORT).show();
                }
            }
            });


        //triggered when signing up
        btnSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if ((email.getText().length() > 1) && (userID.getText().length() > 0 && (name.getText().length() > 3)&& (password.getText().length()>5))){

                    //TODO: use some kind of unique auto generate code
                    final String id = userID.getText().toString();
                    String nameU = name.getText().toString();
                    String emailU = email.getText().toString();
                    String passwordU = password.getText().toString();

                    //create new user
                    final User userNew = new User(nameU, emailU, id, passwordU);
                    mDatabase.child("users")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                int flag = 0;
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        if(userNew.email.equals(user.email)) {
                                            Toast.makeText(MainActivity.this, "This email has been registered!", Toast.LENGTH_SHORT).show();
                                            flag=1;
                                            break;
                                        }
                                        if(userNew.userID.equals(user.userID)) {
                                            flag=1;
                                            Toast.makeText(MainActivity.this, "This userID has been registered!", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                    if(flag ==0) {

                                        //put user into users field
                                        Toast.makeText(MainActivity.this, "successfully added " + userNew.username, Toast.LENGTH_SHORT).show();
                                        loggedin = userNew;
                                        Toast.makeText(MainActivity.this, "Hello "+loggedin.username, Toast.LENGTH_SHORT).show();

                                        //define a jump
                                        Intent intent = new Intent(MainActivity.this, AddClass.class);

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
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter valid value in all fields for register", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}

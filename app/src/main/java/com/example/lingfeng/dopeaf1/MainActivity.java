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

    private Button btnUser;

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
        btnUser = (Button) findViewById(R.id.add_user);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if ((email.getText().length() > 1) && (password.getText().length()>5)){
                    final String emailU = email.getText().toString();
                    final String passwordU = password.getText().toString();

                    FirebaseDatabase.getInstance().getReference().child("users")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                int flag = 0;
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        if(emailU.equals(user.email)&&passwordU.equals(user.password)) {
                                            loggedin = user;
                                            Toast.makeText(MainActivity.this, "Hello "+loggedin.username, Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MainActivity.this, AddClass.class);
                                            flag=1;
                                            loggedin.updateLastlogin();
                                            mDatabase.child("users").child(loggedin.userID).setValue(loggedin);
                                            startActivity(intent);
                                        }
                                    }
                                    if(flag==0) {
                                        Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter valid value in all fields for login", Toast.LENGTH_LONG).show();
                }
            }
            });

        btnUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if ((email.getText().length() > 1) && (userID.getText().length() > 0 && (name.getText().length() > 3)&& (password.getText().length()>5))){
                    final String id = userID.getText().toString();
                    String nameU = name.getText().toString();
                    String emailU = email.getText().toString();
                    String passwordU = password.getText().toString();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    final User userNew = new User(nameU, emailU, id, passwordU);
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        if(userNew.email.equals(user.email)) {
                                            Toast.makeText(MainActivity.this, "This email has been registered!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        if(userNew.userID.equals(user.userID)) {
                                            Toast.makeText(MainActivity.this, "This userID has been registered!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        mDatabase.child("users").child(id).setValue(userNew);
                                        Toast.makeText(MainActivity.this, "successfully added "+userNew.username, Toast.LENGTH_LONG).show();
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

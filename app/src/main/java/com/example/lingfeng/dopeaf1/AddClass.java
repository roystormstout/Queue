package com.example.lingfeng.dopeaf1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ValueEventListener;

public class AddClass extends AppCompatActivity {
    private EditText cID;
    private EditText classname;
    private EditText q;
    private EditText sect;
    private EditText credits;
    public final User a = MainActivity.loggedin;
    private Button btnAdd;
    private Button btnDrop;
    public DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Toast.makeText(AddClass.this, "Hi! "+a.username+ " Add class at this page", Toast.LENGTH_SHORT).show();
        cID = (EditText) findViewById(R.id.courseID);
        classname = (EditText) findViewById(R.id.className);
        q  = (EditText) findViewById(R.id.quarter);
        credits  = (EditText) findViewById(R.id.credit);
        sect  = (EditText) findViewById(R.id.section);
        btnAdd = (Button) findViewById(R.id.add_class);
        btnDrop = (Button) findViewById(R.id.drop_class);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //add class
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((cID.getText().length() > 0) && (classname.getText().length() > 5) && (q.getText().length() > 3) &&(sect.getText().length() > 2)) {
                    double cred = Double.parseDouble(credits.getText().toString());
                    final String id = cID.getText().toString();
                    final String n = classname.getText().toString();
                    String qua = q.getText().toString();
                    String sec = sect.getText().toString();
                    final Class newClass = new Class(id, n, sec, qua, cred);
                    mDatabase.child("classes")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int newCFlag = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Class aClass = snapshot.getValue(Class.class);
                                        if(newClass.courseID.equals(aClass.courseID)) {
                                            newCFlag = 1;

                                            Toast.makeText(AddClass.this, "Class exists!", Toast.LENGTH_SHORT).show();
                                            int flag = 0;
                                            if(aClass.users!=null) {
                                                for (String u : aClass.users) {
                                                    if (u.equals(a.userID)) {
                                                        Toast.makeText(AddClass.this, "You already enrolled!", Toast.LENGTH_SHORT).show();
                                                        flag = 1;
                                                    }
                                                }
                                            }
                                            if(flag==0){
                                                Toast.makeText(AddClass.this, "Enrolling you to the course", Toast.LENGTH_SHORT).show();
                                                aClass.addStudents(a.userID);
                                                a.addCourse(id);
                                                mDatabase.child("classes").child(id).setValue(aClass);
                                                mDatabase.child("users").child(a.userID).setValue(a);
                                            }
                                            break;
                                        }

                                    }
                                    if(newCFlag==0){
                                        Toast.makeText(AddClass.this, "Adding new class!"+ n, Toast.LENGTH_SHORT).show();
                                        newClass.addStudents(a.userID);
                                        a.addCourse(id);
                                        mDatabase.child("classes").child(id).setValue(newClass);
                                        mDatabase.child("users").child(a.userID).setValue(a);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
                else{
                    Toast.makeText(AddClass.this, "Please enter valid value in all fields to enroll in classes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = cID.getText().toString();
                mDatabase.child("classes")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            int foundFlag = 0;
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Class aClass = snapshot.getValue(Class.class);
                                    if (id.equals(aClass.courseID)) {
                                            foundFlag=1;
                                            if(aClass.dropStudent(a.userID)&&a.dropCourse(aClass.courseID)){
                                                mDatabase.child("classes").child(id).setValue(aClass);
                                                mDatabase.child("users").child(a.userID).setValue(a);
                                                Toast.makeText(AddClass.this, "Course removed!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(AddClass.this, "Are you actually enrolled?", Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                    }
                                }
                                if(foundFlag==0){
                                    Toast.makeText(AddClass.this, "No course found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
            });
    }

}

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
    public DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Toast.makeText(AddClass.this, "Hi! "+a.username+ " Add class at this page", Toast.LENGTH_LONG).show();
        cID = (EditText) findViewById(R.id.courseID);
        classname = (EditText) findViewById(R.id.className);
        q  = (EditText) findViewById(R.id.quarter);
        credits  = (EditText) findViewById(R.id.credit);
        sect  = (EditText) findViewById(R.id.section);
        btnAdd = (Button) findViewById(R.id.add_class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                    FirebaseDatabase.getInstance().getReference().child("classes")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int newCFlag = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Class aClass = snapshot.getValue(Class.class);
                                        if(newClass.courseID.equals(aClass.courseID)) {
                                            newCFlag = 1;

                                            Toast.makeText(AddClass.this, "Class exists!", Toast.LENGTH_LONG).show();
                                            int flag = 0;
                                            for( String u : aClass.users){
                                                if(u.equals(a.userID)){
                                                    Toast.makeText(AddClass.this, "You already enrolled!", Toast.LENGTH_LONG).show();
                                                    flag =1;
                                                }
                                            }
                                            if(flag==0){
                                                Toast.makeText(AddClass.this, "Enrolling you to the course", Toast.LENGTH_LONG).show();
                                                aClass.addStudents(a.userID);
                                                a.addCourse(id);
                                                mDatabase.child("classes").child(id).setValue(aClass);
                                                mDatabase.child("users").child(a.userID).setValue(a);
                                            }
                                            break;
                                        }

                                    }
                                    if(newCFlag==0){
                                        Toast.makeText(AddClass.this, "Adding new class!"+ n, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(AddClass.this, "Please enter valid value in all fields to enroll in classes", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

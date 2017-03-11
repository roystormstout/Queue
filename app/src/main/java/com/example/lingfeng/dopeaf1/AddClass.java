package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddClass extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText cID;
    private EditText classname;
    private EditText q;
    private EditText sect;
    private EditText credits;
    public final User a = ControllerLogin.loggedin;
    private Button btnAdd;
    private Button btnDrop;
    private Button btnAddTask;
    private Button signOut;
    private Button btnMain;
    public DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Toast.makeText(AddClass.this, "Hi! "+a.getUsername()+ " Add class at this page", Toast.LENGTH_SHORT).show();
        cID = (EditText) findViewById(R.id.courseID);
        classname = (EditText) findViewById(R.id.className);
        q  = (EditText) findViewById(R.id.quarter);
        credits  = (EditText) findViewById(R.id.credit);
        sect  = (EditText) findViewById(R.id.section);
        btnAdd = (Button) findViewById(R.id.add_class);
        btnDrop = (Button) findViewById(R.id.drop_class);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnMain = (Button) findViewById(R.id.btnMain);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signOut = (Button) findViewById(R.id.button_sign_out);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //add class
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Class front end Check
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
                                    int classExistFlag = 0;

                                    //Iterator of the subclass
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        //Use class name, and get the return object of the snapshot
                                        Class classToCheck = snapshot.getValue(Class.class);

                                        //When the class is already exist
                                        if(newClass.courseID.equals(classToCheck.courseID)) {
                                            classExistFlag = 1;

                                            Toast.makeText(AddClass.this, "Class exists!", Toast.LENGTH_SHORT).show();
                                            int userExistFlag = 0;

                                            //Check if the user exist in the class already
                                            if(classToCheck.users!=null) {
                                                for (String u : classToCheck.users) {
                                                    if (u.equals(a.getUserID())) {
                                                        Toast.makeText(AddClass.this, "You already enrolled!", Toast.LENGTH_SHORT).show();
                                                        userExistFlag = 1;
                                                    }
                                                }
                                            }

                                            //Add the student into the class if existFLag says the student is not in the class
                                            if(userExistFlag==0){
                                                Toast.makeText(AddClass.this, "Enrolling you to the course", Toast.LENGTH_SHORT).show();
                                                classToCheck.addStudents(a.getUserID());
                                                a.addCourse(id);
                                                mDatabase.child("classes").child(id).setValue(classToCheck);
                                                mDatabase.child("users").child(a.getUserID()).setValue(a);
                                            }
                                            break;
                                        }

                                    }

                                    //When the class is new class, we add the class and enroll the student. Both.
                                    if(classExistFlag==0){
                                        Toast.makeText(AddClass.this, "Adding new class!"+ n, Toast.LENGTH_SHORT).show();
                                        newClass.addStudents(a.getUserID());
                                        a.addCourse(id);
                                        mDatabase.child("classes").child(id).setValue(newClass);
                                        mDatabase.child("users").child(a.getUserID()).setValue(a);
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
                                            if(aClass.dropStudent(a.getUserID())&&a.dropCourse(aClass.courseID)){
                                                mDatabase.child("classes").child(id).setValue(aClass);
                                                mDatabase.child("users").child(a.getUserID()).setValue(a);
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

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String id = cID.getText().toString();
                //Toast.makeText(AddTask.class, "Come to Add Task!", Toast.LENGTH_SHORT).show();
                //define a jump
                Intent intent = new Intent(AddClass.this, AddTask.class);

                a.updateLastlogin();
                mDatabase.child("users").child(a.getUserID()).setValue(a);
                //jump to add class
                startActivity(intent);
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String id = cID.getText().toString();
                //Toast.makeText(AddTask.class, "Come to Add Task!", Toast.LENGTH_SHORT).show();
                //define a jump
                Intent intent = new Intent(AddClass.this, ViewNavigation.class);

                a.updateLastlogin();
                mDatabase.child("users").child(a.getUserID()).setValue(a);
                //jump to add class
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }
    private void signOut() {

        mAuth.signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent intent = new Intent(AddClass.this, ControllerLogin.class);
                        a.updateLastlogin();
                        //jump to add class
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}

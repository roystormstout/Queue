package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTask extends AppCompatActivity {

    //Variables that link to the component in the page
    private Button   saveTask;
    private Button   cancel;
    private Switch   shareSwitch;
    private RatingBar priorityBar;
    private EditText taskName;
    private TextView courseText;
    private EditText dueDate;

    //Get the user from the Main
    public final User a = MainActivity.loggedin;

    //Use for database
    public DatabaseReference mDatabase;

    //Create the on click action
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toast.makeText(AddTask.this, "Hi! "+a.username+ " Add Task at this page", Toast.LENGTH_SHORT).show();

        saveTask = (Button) findViewById(R.id.saveTask);
        cancel = (Button) findViewById(R.id.cancel);
        shareSwitch = (Switch) findViewById(R.id.shareSwitch);
        priorityBar = (RatingBar) findViewById(R.id.priorityBar);
        taskName = (EditText) findViewById(R.id.taskName);
        courseText = (TextView) findViewById(R.id.course);
        dueDate = (EditText) findViewById(R.id.dueDate);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //add class
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Prepare the value we are about to store
                //In this part, we did not restrict the user
                //to have length of task or any other kinds
                //of restriction to the input. the only restriction
                //is the course. The user must enrolled to the course.
                //
                float priorityValue = priorityBar.getRating();
                final String nameOfTask = taskName.getText().toString();
                final String courseID = courseText.getText().toString();
                String due = dueDate.getText().toString();
                boolean share = shareSwitch.isChecked();


                final Task newTask = new Task(nameOfTask, courseID, due, priorityValue, share);

                mDatabase.child("classes")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean validCourseID = false;
                                Class taskOfClass;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Class aClass = snapshot.getValue(Class.class);

                                    //Loop through the whole data base to check if the
                                    //Course id is valid
                                    if (courseID.equals(aClass.courseID)) {

                                        taskOfClass = aClass;
                                        validCourseID = true;

                                        break;
                                    }

                                }
                                if (validCourseID) {
                                    Toast.makeText(AddTask.this, "Adding new task!" + taskName, Toast.LENGTH_SHORT).show();
                                    //taskOfClass.addSharedTasks(nameOfTask);
                                    mDatabase.child("classes").child(courseID).child(nameOfTask).setValue(newTask);
                                    mDatabase.child("users").child(a.userID).child("In Progress").setValue(newTask);

                                    //define a jump
                                    Intent intent = new Intent(AddTask.this, AddClass.class);

                                    a.updateLastlogin();
                                    mDatabase.child("users").child(a.userID).setValue(a);
                                    //jump to add class
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(AddTask.this, "Please enter valid course id", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("classes")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            int foundFlag = 0;
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                //define a jump
                                Intent intent = new Intent(AddTask.this, AddClass.class);

                                a.updateLastlogin();
                                mDatabase.child("users").child(a.userID).setValue(a);
                                //jump to add class
                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });



    }
}

package com.example.lingfeng.dopeaf1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class AddTask extends AppCompatActivity {

    //Variables that link to the component in the page
    private Button   saveTask;
    private Button   cancel;
    private Switch   shareSwitch;
    private RatingBar priorityBar;
    private EditText taskName;
    private static EditText dueDate;
    Spinner courseSpinner;
    String courseID;

    //Get the user from the Main
    public final User a = Login.loggedin;

    //Use for database
    public DatabaseReference mDatabase;

    //Create the on click action
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Welcome info
        Toast.makeText(AddTask.this, "Hi! "+a.getUsername()+ " Add Task at this page", Toast.LENGTH_SHORT).show();

        //Find element in the page
        saveTask = (Button) findViewById(R.id.saveTask);
        cancel = (Button) findViewById(R.id.cancel);
        shareSwitch = (Switch) findViewById(R.id.shareSwitch);
        priorityBar = (RatingBar) findViewById(R.id.priorityBar);
        taskName = (EditText) findViewById(R.id.taskName);
        dueDate = (EditText) findViewById(R.id.dueDate);
        courseSpinner = (Spinner) findViewById(R.id.courseList);
        final List<String> courseListToShow = a.getEnrolledCourses();
        //courseID = courseListToShow.get(0);

        //Get a instance of the firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // tie the adapter with the list of courses
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, courseListToShow);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //绑定 Adapter到控件
        courseSpinner .setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                Toast.makeText(AddTask.this, "你点击的是:"+courseListToShow.get(pos), Toast.LENGTH_LONG).show();
                courseID = courseListToShow.get(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //add task save button listener
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Prepare the value we are about to store
                //In this part, we did not restrict the user
                //to have length of task or any other kinds
                //of restriction to the input. the only restriction
                //is the course. The user must enrolled to the course
                float priorityValue = priorityBar.getRating();
                final String nameOfTask = taskName.getText().toString();
                String due = dueDate.getText().toString();
                boolean share = shareSwitch.isChecked();

                final Task newTask = new Task(nameOfTask, courseID, due, priorityValue, share);

                mDatabase.child("classes")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean validCourseID = false;
                                Class taskOfClass=new Class();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Class aClass = snapshot.getValue(Class.class);

                                    //Loop through the whole data base to check if the
                                    //Course id is valid
                                    if (courseID.equals(aClass.courseID)) {

                                        taskOfClass=aClass;
                                        validCourseID = true;

                                        break;
                                    }

                                }
                                if (validCourseID) {
                                    Toast.makeText(AddTask.this, "Adding new task! " + nameOfTask, Toast.LENGTH_SHORT).show();
                                    newTask.addUserID(a.getUserID());
                                    a.addTask(nameOfTask);
                                    mDatabase.child("tasks").child(nameOfTask).setValue(newTask);
                                    taskOfClass.addTasks(nameOfTask);
                                    mDatabase.child("classes").child(courseID).setValue(taskOfClass);
                                    mDatabase.child("users").child(a.getUserID()).child("inProgressTask").setValue(newTask);

                                    //define a jump
                                    Intent intent = new Intent(AddTask.this, Navigation.class);

                                    a.updateLastlogin();
                                    mDatabase.child("users").child(a.getUserID()).setValue(a);
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
                                Intent intent = new Intent(AddTask.this, Navigation.class);

                                a.updateLastlogin();
                                mDatabase.child("users").child(a.getUserID()).setValue(a);
                                //jump to add class
                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });

        dueDate.setInputType(InputType.TYPE_NULL);
        dueDate.setTextIsSelectable(true);
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dueDate.setText(dueDate.getText()+" "+hourOfDay+":"+minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),this,year,month,day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dueDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}

package com.example.lingfeng.dopeaf1;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class User {
    public String pid;
    public String userID;
    public String username;
    public String email;
    public int numTasks;
    public String password;
    public String createDate;
    public String lastlogin;
    public List<String> enrolledCourses;
    public List<String> finishedTask;
    public List<String> inProgressTask;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    User(String username, String email, String userID, String password) {
        this.pid="";
        this.password = password;
        this.userID = userID;
        this.username = username;
        this.email = email;
        numTasks = 0;
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        createDate = df.format(dateobj);
        lastlogin="";
        enrolledCourses = new ArrayList<String>();
        finishedTask = new ArrayList<String>();
        inProgressTask = new ArrayList<String>();
    }

    public void updateLastlogin(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        lastlogin=df.format(dateobj);
    }

    public void addCourse(String courseID){
        if(enrolledCourses==null)
            enrolledCourses =new ArrayList<String>();
        enrolledCourses.add(courseID);
    }

    public boolean dropCourse(String courseID){
        if(enrolledCourses==null)
            return false;
        if(enrolledCourses.contains(courseID))
            enrolledCourses.remove(enrolledCourses.indexOf(courseID));
        else
            return false;
        return true;
    }

}

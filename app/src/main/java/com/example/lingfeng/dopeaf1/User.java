package com.example.lingfeng.dopeaf1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

@IgnoreExtraProperties
public class User {
    private String userID;
    private String username;
    private String email;
    private int numTasks;
    private String password;
    private String createDate;
    private String lastlogin;
    private String UCSDEmail;
    private String UCSDPassword;
    public List<String> enrolledCourses;
    public List<String> finishedTask;
    public List<String> inProgressTask;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    User(String username, String email, String userID, String password) {
        this.password = password;
        this.userID = userID;
        //TODO: Do we really need USERNAME? Personally, I think we'd better not to have it
        this.username = username;
        this.email = email;
        UCSDEmail = "";
        UCSDPassword = "";
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

    public void addTask(String task){
        if( inProgressTask == null){
            inProgressTask = new ArrayList<String>();
        }

        inProgressTask.add(task);
    }

    public void finishTask(String task){
        if( inProgressTask == null) return;

        if( inProgressTask.contains(task)){
            inProgressTask.remove(task);

            if( finishedTask == null){
                finishedTask = new ArrayList<String>();
                finishedTask.add(task);
            }
        }
    }



    public void setUCSDAccount(String UCSDEmail, String UCSDPassword) {
        setUCSDEmail(UCSDEmail);
        setUCSDPassword(UCSDPassword);
    }

    public String getUCSDEmail() {
        return UCSDEmail;
    }

    public void setUCSDEmail(String UCSDEmail) {
        this.UCSDEmail = UCSDEmail;
    }

    public String getUCSDPassword() {
        return UCSDPassword;
    }

    public void setUCSDPassword(String UCSDPassword) {
        this.UCSDPassword = UCSDPassword;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String email) {
        this.email = email;
    }

    public String getUserPassword() {
        return password;
    }

    public void setUserPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

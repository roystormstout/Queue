package com.example.lingfeng.dopeaf1;

import java.util.ArrayList;
import java.util.*;
/**
 * Created by yechenwei on 2/18/17.
 */

public class Task {
    public String taskName;
    public String taskID;
    public ArrayList<String> userID = new ArrayList<String>();
    public String courseID;
    public String dueDate;
    public float priority;
    public String taskDescription = "";
    //public double completePercentage = 0;
    //public int completedPerson;
    //public boolean share = false;
    public boolean verified = false;
    //public int status;
    // 0: in progress; 1: completed; 2: missed

    public Task() {

    }

    public Task(String taskName, String courseID, String dueDate, float priority) {
        this.taskName = taskName;
        this.courseID = courseID;
        this.dueDate = dueDate;
        this.priority = priority;
        //this.share = shared;

    }

    public void addUserID(String id) {
        userID.add(id);
    }

    public void updateDueDate(String date) {
        dueDate = date;
    }

    public void updateDiscription(String dis) {
        taskDescription = dis;
    }

    //W level
/*
    public void updatePercentage() {
        completedPerson++;
        //completePercentage = completedPerson / (double) userID.size();
    }
*/
    public void addTaskID(String taskName){
        taskID = taskName;
    }
    public void deleteUserID(String uid){
        if(userID!=null&&userID.contains(uid))
            userID.remove(uid);
    }

    public String taskDescription() {
        return taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }
}
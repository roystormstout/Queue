package com.example.lingfeng.dopeaf1;

import java.util.ArrayList;

/**
 * Created by yechenwei on 2/18/17.
 */

public class Task {
    public String taskName;
    public String taskID;
    public ArrayList<String> userID = new ArrayList<String>();
    public String courseID;
    //public String startTime;
    //public String endTime;
    public String dueDate;
    public float priority;
    public String taskDescription = "";
    public double completePercentage = 0;
    public int completedPerson;
    //public int status;
    // 0: in progress; 1: completed; 2: missed

    public Task() {

    }

    public Task(String taskName, String courseID, String dueDate, float priority,
                boolean shared) {
        this.taskName = taskName;
        this.taskID = taskID;
        this.courseID = courseID;
        this.dueDate = dueDate;
        this.priority = priority;

        this.taskID = getTaskID(taskName);

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


    public void updatePercentage() {
        completedPerson++;
        completePercentage = completedPerson / (double) userID.size();
    }

    public String getTaskID(String taskName){
        return "";
    }
}
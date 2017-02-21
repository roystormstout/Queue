package com.example.lingfeng.dopeaf1;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Jas on 2017/2/20.
 */

public class Task {

    private int taskId;
    private int courseID;
    private int userID;
    private Timestamp startTime;
    private Timestamp dueTime;
    private Timestamp finishTime;
    private int priority;
    private String taskName;
    private String taskDescription;
    private boolean shared;
    private String status;


    public Task(){}

    public Task( int t_id, int c_id, int u_id,
                 Timestamp startTime, Timestamp dueTime,
                 Timestamp finishTime,
                 int priority, String t_name,
                 String taskDescription, boolean shared,
                  String status){

        this.taskId = t_id;
        this.courseID=c_id;
        this.taskName=t_name;
        this.userID = u_id;
        this.startTime = startTime;
        this.dueTime = dueTime;
        this.finishTime = finishTime;
        this.shared=shared;
        this.priority = priority;
        this.taskDescription = taskDescription;
        this.status = status;
    }


    public void finish_Task(){
        this.status = "Finished";
    }

    public void changePriority(int p){
        priority = p;
    }

    public void updateName(String name){
        taskName = name;
    }

    public void shareTask(){
        this.shared = true;
    }

    public void privateTask(){
        this.shared = false;
    }



}

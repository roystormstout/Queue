package com.example.lingfeng.dopeaf1;

<<<<<<< HEAD
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
    //public int priority;
    public String taskDescription = "";
    public double completePercentage = 0;
    public int completedPerson;
    //public int status;
    // 0: in progress; 1: completed; 2: missed

    public Task(){

    }

    public Task(String taskName, String taskID, String courseID, String dueDate, int priority,
                boolean shared){
        this.taskName = taskName;
        this.taskID = taskID;
        this.courseID = courseID;
        this.dueDate = dueDate;

    }

    public void addUserID(String id){
        userID.add(id);
    }

    public void updateDueDate(String date){
        dueDate = date;
    }

    public void updateDiscription(String dis){
        taskDescription = dis;
    }



    public void updatePercentage(){
        completedPerson++;
        completePercentage = completedPerson/(double)userID.size();
=======
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
>>>>>>> 98c797abf8d3288067a6c77157720ebcdc982d6d
    }



}

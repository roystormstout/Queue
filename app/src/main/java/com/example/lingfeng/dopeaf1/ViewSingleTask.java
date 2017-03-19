package com.example.lingfeng.dopeaf1;

/**
 * Created by Lingfeng on 2017/3/13.
 */
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
public class ViewSingleTask extends AppCompatActivity {

    String taskDetail = ViewNavigation.taskToPresent.taskDescription();
    String taskName = ViewNavigation.taskToPresent.getTaskName();
    String priorityLevel = String.valueOf(ViewNavigation.taskToPresent.priority);
    String dueDate = ViewNavigation.taskToPresent.dueDate;
    String courseName = ViewNavigation.taskToPresent.courseID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();
        adsElement.setTitle("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nWe want to heard from you!");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(taskName)
                .addItem(new Element().setTitle("Course Name:"+"\n\n"+courseName))
                .addItem(new Element().setTitle("Task Detail:"+"\n\n"+taskDetail))
                .addItem(new Element().setTitle("Priority Level:\n\n"+priorityLevel))
                .addItem(new Element().setTitle("Due Date:\n\n"+dueDate))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("cse110.queue@gmail.com")
                .addGitHub("lihao0718")
                .create();

        setContentView(aboutPage);
    }



    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

}

package com.example.lingfeng.dopeaf1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class TaskPresenter extends AppCompatActivity {

        private static final String TAG = "TaskPresenter";
        private RecyclerView mRecyclerView;
        private MyAdapter mMyAdapter;
        private User user = Login.loggedin;
        private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        private FloatingActionButton fab_plus,fab_add_class, fab_add_task;
        Animation FabOpen,FabClose,FabClock,FabAntiClock;

        boolean fabOpen = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mMyAdapter = new MyAdapter(TaskPresenter.this, initData());
            mRecyclerView.setAdapter(mMyAdapter);
            fab_plus = (FloatingActionButton) findViewById(R.id.fab_add);
            fab_add_class = (FloatingActionButton) findViewById(R.id.fab_add_class);
            fab_add_task = (FloatingActionButton) findViewById(R.id.fab_add_task);

            FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
            FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

            FabClock = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_clockwise);
            FabAntiClock = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_anticlockwise);


            if(user.inProgressTask == null || user.inProgressTask.size() == 0){
                Toast.makeText(TaskPresenter.this, "Horrray!! No Due!!!!.", Toast.LENGTH_LONG).show();
            }

            fab_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(fabOpen){
                        fab_add_task.startAnimation(FabAntiClock);
                        fab_add_task.startAnimation(FabClose);

                        fab_add_class.startAnimation(FabAntiClock);
                        fab_add_class.startAnimation(FabClose);

                        fab_plus.startAnimation(FabAntiClock);

                        fab_add_task.setClickable(false);
                        fab_add_class.setClickable(false);

                        fabOpen = false;
                    }

                    else{

                        //fab_add_task.setText("Add Task");
                        //fab_add_class.setLabelText("Add Class");

                        fab_add_task.startAnimation(FabClock);
                        fab_add_task.startAnimation(FabOpen);

                        fab_add_class.startAnimation(FabClock);
                        fab_add_class.startAnimation(FabOpen);

                        fab_plus.startAnimation(FabClock);

                        fab_add_task.setClickable(true);
                        fab_add_class.setClickable(true);

                        fabOpen = true;

                    }
                }
            });

            fab_add_class.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //define a jump
                    Intent intent = new Intent(TaskPresenter.this, AddClass.class);
                    user.updateLastlogin();
                    startActivity(intent);
                }
            });

            fab_add_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //define a jump
                    Intent intent = new Intent(TaskPresenter.this, AddTask.class);
                    user.updateLastlogin();
                    startActivity(intent);
                }
            });

            mMyAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                @Override
                public void onClick(View parent, int position) {
                    //mMyAdapter.addData(position,"add item:"+position);
                    //mMyAdapter.notifyItemInserted(position);
                }
            });
            mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
                @Override
                public boolean onLongClick(View parent, int position) {
                    Toast.makeText(TaskPresenter.this, "You have delete the task.", Toast.LENGTH_SHORT).show();
                    user.finishTask(user.inProgressTask.get(position));
                    mdatabase.child("users").child(user.getUserID()).setValue(user);
                    mMyAdapter.notifyItemRemoved(position);
                    return false;
                }
            });



        }

        private List<String> initData() {
            return user.inProgressTask;
        }





}
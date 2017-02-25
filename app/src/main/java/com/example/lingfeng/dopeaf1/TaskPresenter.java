package com.example.lingfeng.dopeaf1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class TaskPresenter extends AppCompatActivity {

        private static final String TAG = "TaskPresenter";
        private RecyclerView mRecyclerView;
        private MyAdapter mMyAdapter;
        private User user = Login.loggedin;
        private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mMyAdapter = new MyAdapter(TaskPresenter.this, initData());
            mRecyclerView.setAdapter(mMyAdapter);

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
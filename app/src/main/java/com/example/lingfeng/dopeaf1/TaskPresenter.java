package com.example.lingfeng.dopeaf1;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jas on 2017/2/22.
 */

public class TaskPresenter extends AppCompatActivity {

        private static final String TAG = "TaskPresenter";
        private RecyclerView mRecyclerView;
        private MyAdapter mMyAdapter;
        private User user = Login.loggedin;

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
                    mMyAdapter.notifyItemRemoved(position);
                    return false;
                }
            });

        }

        private List<String> initData() {
            return user.inProgressTask;
        }





}
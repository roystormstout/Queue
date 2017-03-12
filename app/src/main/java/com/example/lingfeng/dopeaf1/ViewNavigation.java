package com.example.lingfeng.dopeaf1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;


public class ViewNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private RecyclerView mRecyclerView;
    private View mainView;
    private MyAdapter mMyAdapter;
    private User user = ControllerLogin.loggedin;
    private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    private FloatingActionButton fab_plus,fab_add_class, fab_add_task;
    Animation FabOpen,FabClose,FabClock,FabAntiClock;
    private ItemTouchHelper mItemTouchHelper;
    boolean fabOpen = false;
    private SmartTabLayout myTab;
    private ViewPager mViewPager;
    private boolean isPersonal = true;
    private String currentClass = "All Tasks";
    private Menu drawerMenu;
    private NavigationView navigationView;

    Comparator<Task> Order =  new Comparator<Task>(){
        public int compare(Task o1, Task o2) {
            // TODO Auto-generated method stub
            Date date1 = new Date();
            Date date2= new Date();
            String dateS1= o1.dueDate;
            String dateS2 = o2.dueDate;
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                date1=format.parse(dateS1);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            try {
                date2=format.parse(dateS2);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            Timestamp timestamp1 = new java.sql.Timestamp(date1.getTime());
            Timestamp timestamp2 = new java.sql.Timestamp(date2.getTime());

            if(timestamp2.after( timestamp1))
            {

                return -1;
            }
            else if(timestamp2.before( timestamp1))
            {

                return 1;
            }
            else
            {
                return 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        myTab.setViewPager(mViewPager);


        mainView = findViewById(R.id.activity_main);

        mRecyclerView = (RecyclerView) mainView.findViewById(R.id.rv_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyAdapter = new MyAdapter(ViewNavigation.this, initPersonalData());
        mRecyclerView.setAdapter(mMyAdapter);
        mMyAdapter.sortData();

        fab_plus = (FloatingActionButton) mainView.findViewById(R.id.fab_add);
        fab_add_class = (FloatingActionButton) mainView.findViewById(R.id.fab_add_class);
        fab_add_task = (FloatingActionButton) mainView.findViewById(R.id.fab_add_task);

        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        FabClock = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_clockwise);
        FabAntiClock = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_anticlockwise);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mMyAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        if(user.inProgressTask == null || user.inProgressTask.size() == 0) {
            //System.err.println("Entering Navigation class "+user.getUsername());
            Snackbar.make(findViewById(R.id.rv_main), "Hooooray No Task At ALLLLL!!!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
                Intent intent = new Intent(ViewNavigation.this, AddClass.class);

                startActivity(intent);
            }
        });

        fab_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define a jump
                Intent intent = new Intent(ViewNavigation.this, AddTask.class);

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
       /* mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(View parent, int position) {
                Toast.makeText(Navigation.this, "You have delete the task.", Toast.LENGTH_SHORT).show();
                user.finishTask(user.inProgressTask.get(position));
                mdatabase.child("users").child(user.getUserID()).setValue(user);
                mMyAdapter.notifyItemRemoved(position);
                return false;
            }
        });*/

       myTab.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
           @Override
           public void onTabClicked(int position) {
               if (isPersonal != (position == 0)) {
                   if (isPersonal) {
                       drawerMenu.clear();
                       initMenu();
                       setSharableTasks();
                   } else {
                       drawerMenu.add("Completed Tasks");
                       setPersonalTasks();
                   }
               }
               isPersonal = position == 0;
           }
       });


        drawerMenu = navigationView.getMenu();
        initMenu();
        drawerMenu.add("Completed Tasks");
        initInfo();

    }

    private void initInfo() {
        View info = navigationView.getHeaderView(0);
        //View info = findViewById(R.id.info);
        //ImageView imageV = (ImageView) info.findViewById(R.id.user_icon);
        if (ControllerLogin.personPhoto != null) {
            ImageView imgView = (ImageView) info.findViewById(R.id.user_icon);
            // Download photo and set to image
            Context context = imgView.getContext();
            Picasso.with(context).load(ControllerLogin.personPhoto).transform(new circleTransformation()).resize(150, 150).into(imgView);
        }
        TextView textView_name = (TextView) info.findViewById(R.id.user_name);
        textView_name.setText(ControllerLogin.personName);
        TextView textView_email = (TextView) info.findViewById(R.id.user_email);
        textView_email.setText(ControllerLogin.personEmail);

    }

    private void initMenu() {
        drawerMenu.add("All Tasks");
        if (user.enrolledCourses != null) {
            for (String str : user.enrolledCourses) {
                drawerMenu.add(str);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    private void setPersonalTasks() {

        if (currentClass.equalsIgnoreCase("Completed Tasks")) {
            mMyAdapter.setData(new ArrayList<Task>());
            mMyAdapter.notifyDataSetChanged();
            initCompletedTasks();
        }

        if (currentClass.equalsIgnoreCase("All Tasks")) {
            mMyAdapter.setData(initPersonalData());
            mMyAdapter.notifyDataSetChanged();
        }

        if (user.enrolledCourses != null) {
            for (String str : user.enrolledCourses) {
                if (currentClass.equalsIgnoreCase(str)) {
                    mMyAdapter.setData(new ArrayList<Task>());
                    mMyAdapter.notifyDataSetChanged();
                    personalSpecificCourseTask(str);
                    mMyAdapter.sortData();
                    break;
                }
            }
        }
    }

    private void setSharableTasks() {

        if (user.enrolledCourses == null) {
            mMyAdapter.setData(new ArrayList<Task>());
            mMyAdapter.notifyDataSetChanged();
            return;
        }

        if (currentClass.equalsIgnoreCase("Completed Tasks")) {
            mMyAdapter.setData(new ArrayList<Task>());
            mMyAdapter.notifyDataSetChanged();
            return;
        }

        if (currentClass.equalsIgnoreCase("All Tasks")) {
            mMyAdapter.setData(new ArrayList<Task>());
            mMyAdapter.notifyDataSetChanged();
            sharableAllTasks();
            mMyAdapter.sortData();
        } else {
            for (String str : user.enrolledCourses) {
                if (currentClass.equalsIgnoreCase(str)) {
                    mMyAdapter.setData(new ArrayList<Task>());
                    mMyAdapter.notifyDataSetChanged();
                    shareableSpecificCourseTask(str);
                    mMyAdapter.sortData();
                    break;
                }
            }
        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        currentClass = item.toString();

        if (isPersonal) {
            setPersonalTasks();
        } else {
            setSharableTasks();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(item.toString());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private List<Task> initPersonalData() {
        System.out.println("initing data!!");
        final ArrayList<Task> newDatas = new ArrayList<Task>();
        if (user.inProgressTask == null) {
            user.inProgressTask = new ArrayList<String>();
            return newDatas;
        } else {
            for (int i = 0; i < user.inProgressTask.size(); ++i) {

                final String taskID = user.inProgressTask.get(i);

                mdatabase.child("tasks").child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Task task = dataSnapshot.getValue(Task.class);
                        newDatas.add(task);
                        Collections.sort(newDatas,Order);
                        mMyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }

        return newDatas;
    }

    private void initCompletedTasks() {
        if (user.finishedTask == null) {
            user.finishedTask = new Stack<>();
            return;
        } else {
            for (int i = 0; i < user.finishedTask.size(); ++i) {

                final String taskID = user.finishedTask.get(i);

                mdatabase.child("tasks").child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Task task = dataSnapshot.getValue(Task.class);
                        mMyAdapter.addData(task);
                        mMyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }

        return;
    }

    private void personalSpecificCourseTask(final String courseID) {

        System.err.println("Now updating the course specific task as "+courseID);

        if (user.inProgressTask == null || user.inProgressTask.size() == 0)
            return;

        for (int i = 0; i < user.inProgressTask.size(); ++i) {

            final String taskID = user.inProgressTask.get(i);

            mdatabase.child("tasks").child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Task task = dataSnapshot.getValue(Task.class);
                    String course = task.courseID;
                    if (course.equals(courseID)) {
                        mMyAdapter.addData(task);
                        mMyAdapter.sortData();
                        mMyAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
    }

    private void shareableSpecificCourseTask(final String courseID) {
        mdatabase.child("classes").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshoto) {
                Class thisClass = dataSnapshoto.getValue(Class.class);
                if (thisClass.sharedtaskList == null) {
                    return;
                }
                for (String taskID: thisClass.sharedtaskList) {
                    mdatabase.child("tasks").child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshoti) {
                            Task task = dataSnapshoti.getValue(Task.class);
                            mMyAdapter.addData(task);
                            mMyAdapter.sortData();
                            mMyAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sharableAllTasks() {
        mdatabase.child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshoto) {
                for (DataSnapshot dss: dataSnapshoto.getChildren()) {
                    Class classInList = dss.getValue(Class.class);
                    Log.d("hello", classInList.courseID);
                    if (user.enrolledCourses.contains(classInList.courseID)) {
                        Log.d("hello", "should come here" + classInList.courseID);
                        if (classInList.sharedtaskList == null) {
                            continue;
                        }
                        for (String taskID: classInList.sharedtaskList) {
                            mdatabase.child("tasks").child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshoti) {
                                    Task task = dataSnapshoti.getValue(Task.class);
                                    mMyAdapter.addData(task);
                                    mMyAdapter.sortData();
                                    mMyAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}

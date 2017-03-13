package com.example.lingfeng.dopeaf1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 3/12/17.
 */


public class MyFinishedAdapter extends RecyclerView.Adapter<MyFinishedAdapter.MyViewHolder> implements onSwipeListener {

    private static final String TAG = "MyFinishedAdapter";
    private List<Task> datas;
    private LayoutInflater inflater;
    public OnItemClickListener mOnItemClickListener;
    public OnItemLongClickListener mOnItemLongClickListener;
    private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    private User user = ControllerLogin.loggedin;

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
            //System.out.println("Almost there!!!!!");
            if(timestamp2.after( timestamp1))
            {
                //System.out.println("second one older!");
                return -1;
            }
            else if(timestamp2.before( timestamp1))
            {
                //System.out.println("first one older!");
                return 1;
            }
            else
            {
                //System.out.println("same");
                return 0;
            }
        }
    };


    public MyFinishedAdapter(Context context, List<Task> datas) {
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        public void onClick(View parent, int position);
    }

    public interface OnItemLongClickListener {
        public boolean onLongClick(View parent, int position);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.mOnItemLongClickListener = l;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rv_main_item_title);
            subtitle = (TextView)itemView.findViewById(R.id.rv_date);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "create a new item");
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.content_main_task_present_single_style, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.e(TAG, "set value to item:" + position);
        holder.title.setText(datas.get(position).taskName);
        holder.subtitle.setText(datas.get(position).dueDate.substring(0, 11));
        // 设置事件响应
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onClick(holder.itemView, pos);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemLongClickListener.onLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    public void sortData(){
        Collections.sort(datas,Order);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {

        if(datas == null)
        {
            return 0;
        }

        return datas.size();
    }

    public void addData(int position, Task content){
        datas.add(position,content);
    }

    public Task getData(int pos) { return datas.get(pos); }

    public void addData(Task content) { datas.add(content); }

    public void setData(List<Task> newData) { datas = newData; }

    //  public void removeData(int position){
    //String toRemove = datas[position]
    //   datas.remove(position);
    //   }


    public void removeData(Task task) {
        datas.remove(task);
    }

    @Override
    public void onItemDismiss(int position) {
        System.out.println("From what user have in their inProgrss list size "+user.inProgressTask.size());
        System.out.println("Datas updated, size is now "+datas.size());
        System.out.println("Happ the position we intent to access is...."+position);

        Task currTask = getData(position);
        user.deleteTask(currTask.taskID);
        System.err.println(currTask+" was deleting");
        System.out.println("Current list has size as......"+datas.size());
        System.out.println("And the position we intent to access is...."+position);
        mdatabase.child("users").child(user.getUserID()).setValue(user);
        //删除mItems数据
        removeData(currTask);
        //删除RecyclerView列表对应item
        notifyItemRemoved(position);
    }



}

package com.example.lingfeng.dopeaf1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

/**
 * Created by Jas on 2017/2/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements onSwipeListener {

    private static final String TAG = "MyAdapter";
    private List<String> datas;
    private LayoutInflater inflater;
    public OnItemClickListener mOnItemClickListener;
    public OnItemLongClickListener mOnItemLongClickListener;
    private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    private User user = Login.loggedin;

    public MyAdapter(Context context, List<String> datas) {
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

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rv_main_item_title);
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
        holder.title.setText(datas.get(position));

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

    @Override
    public int getItemCount() {

        if(datas == null)
        {
            return 0;
        }

        return datas.size();
    }

    public void addData(int position, String content){
        datas.add(position,content);
    }

    public String getData(int pos) { return datas.get(pos); }

    public void addData(String content) { datas.add(content); }

    public void setData(List<String> newData) { datas = newData; }

  //  public void removeData(int position){
        //String toRemove = datas[position]
     //   datas.remove(position);
 //   }

    @Override
    public void onItemDismiss(int position) {
        System.out.println("From what user have in their inProgrss list size "+user.inProgressTask.size());
        System.out.println("Datas updated, size is now "+datas.size());
        System.out.println("Happ the position we intent to access is...."+position);

        String currTask = getData(position);
        user.finishTask(currTask);
        System.err.println(currTask+" was deleteing");
        System.out.println("Current list has size as......"+datas.size());
        System.out.println("And the position we intent to access is...."+position);
        mdatabase.child("users").child(user.getUserID()).setValue(user);
        //notifyItemRemoved(position);
        //删除mItems数据
       datas.remove(currTask);
        //删除RecyclerView列表对应item
        notifyItemRemoved(position);
    }



}
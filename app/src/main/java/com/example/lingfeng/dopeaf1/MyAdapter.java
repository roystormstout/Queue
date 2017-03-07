package com.example.lingfeng.dopeaf1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Jas on 2017/2/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {

    private static final String TAG = "MyAdapter";
    private List<String> datas;
    private LayoutInflater inflater;
    public OnItemClickListener mOnItemClickListener;
    public OnItemLongClickListener mOnItemLongClickListener;

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

    public void setData(List<String> newData) { datas = newData; }

    public void removeData(int position){
        //String toRemove = datas[position]
        datas.remove(position);
    }





}
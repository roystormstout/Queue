package com.example.lingfeng.dopeaf1;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Lingfeng on 2017/3/9.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private onSwipeListener adapter;
    public SimpleItemTouchHelperCallback(onSwipeListener listener){
        adapter = listener;
    }
    /**这个方法是用来设置我们拖动的方向以及侧滑的方向的*/
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(0,swipeFlags);
    }
    /**当我们拖动item时会回调此方法*/
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        return false;
    }
    /**当我们侧滑item时会回调此方法*/
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}

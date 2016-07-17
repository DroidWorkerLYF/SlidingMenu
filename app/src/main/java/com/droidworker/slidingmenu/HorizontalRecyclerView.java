package com.droidworker.slidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class HorizontalRecyclerView extends RecyclerView {
    private int mItemCount;
    private int mRow;

    public HorizontalRecyclerView(Context context) {
        this(context, null);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        super.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new MyAdapter());
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {

    }

    public void setRow(int row) {
        mRow = row;
    }

    public void setItemCount(int count) {
        mItemCount = count;
        getAdapter().notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .layout_horizontal_item, parent, false);
            final MyViewHolder holder = new MyViewHolder(view);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(HorizontalRecyclerView.this, mRow + " " + holder.position, Snackbar
                            .LENGTH_SHORT)
                            .show();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            int position;

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}

package com.droidworker.slidingmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class ContentFragment extends Fragment {
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_content, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.content_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
    mRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        private Random mRandom = new Random();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_content_item, parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mHorizontalRecyclerView.setRow(position);
            holder.mHorizontalRecyclerView.setItemCount(mRandom.nextInt(6) + position + 1);
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            HorizontalRecyclerView mHorizontalRecyclerView;

            public MyViewHolder(View itemView) {
                super(itemView);

                mHorizontalRecyclerView = (HorizontalRecyclerView) itemView.findViewById(R.id.item_container);
            }
        }
    }
}

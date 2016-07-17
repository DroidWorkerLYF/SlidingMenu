package com.droidworker.slidingmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author https://github.com/DroidWorkerLYF
 */
public class MenuFragment extends Fragment {
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View contentView){
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.menu_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .layout_menu_item, parent, false);
            final MyViewHolder holder = new MyViewHolder(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(mRecyclerView, String.valueOf(holder.position), Snackbar
                            .LENGTH_SHORT)
                            .show();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String title = getString(R.string.menu) + position;
            holder.titleView.setText(title);
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView titleView;
            int position;

            public MyViewHolder(View itemView) {
                super(itemView);

                titleView = (TextView) itemView.findViewById(R.id.tv_menu_item);
            }
        }
    }
}

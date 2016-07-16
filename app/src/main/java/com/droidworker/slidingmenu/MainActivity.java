package com.droidworker.slidingmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.droidworker.lib.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SlidingMenu slidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        slidingMenu.setSlidingMenuListener(new SlidingMenu.SlidingMenuListener() {
            @Override
            public void opened() {
                Toast.makeText(getApplicationContext(), "opened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(getApplicationContext(), "closed", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("lyf", "toggle");
                slidingMenu.toggle();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new Adapter());
    }

    private class Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            }
            return convertView;
        }
    }
}

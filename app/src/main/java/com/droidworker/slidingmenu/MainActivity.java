package com.droidworker.slidingmenu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.droidworker.lib.SlidingMenu;

public class MainActivity extends AppCompatActivity implements SlidingMenu.onSlidingListener {
    private MenuFragment mMenuFragment;
    private ContentFragment mContentFragment;
    private SlidingMenu mSlidingMenu;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar();
        mSlidingMenu.setOnSlidingListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.menu_container, new MenuFragment()).commit();
        fragmentManager.beginTransaction().add(R.id.content_container, new ContentFragment()).commit();
    }

    private void setActionBar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                mSlidingMenu.toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void opened() {
        Log.e("lyf", "opened");
        Snackbar.make(mSlidingMenu, R.string.opened, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void closed() {
        Log.e("lyf", "closed");
        Snackbar.make(mSlidingMenu, R.string.closed, Snackbar.LENGTH_SHORT).show();
    }
}

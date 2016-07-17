package com.droidworker.slidingmenu;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.droidworker.lib.SlidingMenu;

public class MainActivity extends AppCompatActivity {
    private MenuFragment mMenuFragment;
    private ContentFragment mContentFragment;
    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.menu_container, new MenuFragment()).commit();
        fragmentManager.beginTransaction().add(R.id.content_container, new ContentFragment()).commit();
    }
}

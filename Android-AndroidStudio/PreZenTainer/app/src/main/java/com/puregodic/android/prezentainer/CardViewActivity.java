
package com.puregodic.android.prezentainer;

import java.util.Calendar;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CardViewActivity extends AppCompatActivity {
    
    ActionBarDrawerToggle mToggle;
    DrawerLayout mDrawer;
    Toolbar mToolbar;

    LinearLayout container;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        mDrawer.setDrawerListener(mToggle);
        setTitleColor(Color.WHITE);
        setTitle("Android Support");
        
       
    }
    
    public void google(View v) {
        Toast.makeText(this, "google", Toast.LENGTH_SHORT).show();
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    public void play(View v) {
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    public void gmail(View v) {
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    public void plus(View v) {
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    public void drive(View v) {
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    public void keep(View v) {
        /*Intent i =new Intent(this,.class);
        startActivity(i);*/
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }


    }

}


package com.puregodic.android.prezentainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.puregodic.android.prezentainer.navigationadapter.FragmentDrawer;
import com.puregodic.android.prezentainer.login.LoginActivity;
import com.puregodic.android.prezentainer.login.SessionManager;

public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Fragment fragment;
    private String userId;
    private SessionManager mSessionManager;
    private boolean isTwice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId != null) {
            fragment = new SettingFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();               // set the toolbar title
            setTitle(userId + " 님");
        } else {
            Toast.makeText(this, "다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
            Intent back = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(back);
            finish();
        }

        // Session Manager
        mSessionManager = new SessionManager(getApplicationContext());

        if (!mSessionManager.isLoggedIn()) {
            logoutUser();
        }

    }

    /**
     * Logout
     * Back to LoginActivity
     */
    private void logoutUser() {
        mSessionManager.setLogin(false, userId);
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logOut) {

            // AlertDialog
            AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                    HomeActivity.this);
            mAlertBuilder.setTitle("로그아웃")
                    .setMessage(userId + " 님\n정말로 로그아웃 하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //로그아웃
                            logoutUser();

                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                // 취소 버튼 클릭시 설정
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();

                }
            });

            AlertDialog dialog = mAlertBuilder.create();

            dialog.show();


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new SettingFragment();
                title = getString(R.string.nav_item_setting);
                break;
            case 1:
                fragment = new LoadFragment();
                title = getString(R.string.nav_item_load);
                break;
            case 2:

                fragment = new WebFragment();
                title = getString(R.string.nav_item_homePage);
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();               // set the toolbar title
            setTitle(title);
        }
    }

    // When back button pressed twice, finish
    @Override
    public void onBackPressed() {

        if (!isTwice) {
            Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
                    .show();
            MyKillTimer mKillTimer = new MyKillTimer(2000, 1);
            mKillTimer.start();

        } else {
            //android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }

    }

    public class MyKillTimer extends CountDownTimer {

        public MyKillTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            isTwice = true;
        }

        @Override
        public void onFinish() {
            isTwice = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

    }
}

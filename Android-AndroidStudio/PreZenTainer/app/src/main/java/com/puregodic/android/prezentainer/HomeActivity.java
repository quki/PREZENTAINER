
package com.puregodic.android.prezentainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.puregodic.android.prezentainer.login.LoginActivity;
import com.puregodic.android.prezentainer.login.SessionManager;

public class HomeActivity extends AppCompatActivity {


    private String yourId;

    private SessionManager session;
    
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setTitleColor(Color.WHITE);



        Intent intent = getIntent();
        yourId = intent.getStringExtra("yourId");

        if (yourId != null)
            setTitle(yourId+" 님");
        else {
            Toast.makeText(this, "다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
            setTitle("계정 오류");
            logoutUser();
        }

        // Session Manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

    }

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        switch (v.getId()) {
            case R.id.homeStartBtn: {
                Intent i = new Intent(HomeActivity.this, SettingActivity.class);
                i.putExtra("yourId", yourId);
                startActivity(i);
                break;
            }
            case R.id.homeLoadBtn: {
                Intent i = new Intent(HomeActivity.this, LoadActivity.class);
                i.putExtra("yourId", yourId);
                startActivity(i);
                break;
            }

        }

    }

    private void logoutUser() {
        session.setLogin(false,yourId);
        // Launching the login activity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logOut) {

            // AlertDialog
            AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                    HomeActivity.this);
            mAlertBuilder.setTitle("로그아웃")
                    .setMessage(yourId+" 님\n정말로 로그아웃 하시겠습니까?")
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

}

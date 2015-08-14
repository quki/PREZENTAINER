
package com.puregodic.android.prezentainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.puregodic.android.prezentainer.login.LoginActivity;
import com.puregodic.android.prezentainer.login.SessionManager;

public class HomeActivity extends AppCompatActivity {
    // participate
    Button homeStartBtn, homeLoadBtn, logoutBtn;

    TextView emailTxtView;

    private static String emailStatic;

    private String emailNonStatic;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        emailTxtView = (TextView)findViewById(R.id.emailTxtView);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // static 변수로 초기화
        if (email != null)
            emailStatic = email;

        if (email != null)
            emailTxtView.setText(email);
        else {
            // email String을 Non-Static으로 관리
            emailNonStatic = emailStatic;
            emailTxtView.setText(emailNonStatic);
        }

        // Session Manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Logout button click event
        logoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        switch (v.getId()) {
            case R.id.homeStartBtn: {
                Intent i = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(i);
                break;
            }
            case R.id.homeLoadBtn: {
                Intent i = new Intent(HomeActivity.this, LoadActivity.class);
                startActivity(i);
                break;
            }

        }

    }

    private void logoutUser() {
        session.setLogin(false);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

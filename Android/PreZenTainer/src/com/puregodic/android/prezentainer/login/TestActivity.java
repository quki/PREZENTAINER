
package com.puregodic.android.prezentainer.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.puregodic.android.prezentainer.R;


public class TestActivity extends AppCompatActivity {
    
    private TextView txtEmail;
    private Button btnLogout;
    private static String  emailStatic ;
    private String emailNonStatic ;
    
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        if(email!=null)
            emailStatic = email;
            
        
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
 
        if(email!=null)
            txtEmail.setText(email);
        else{
            emailNonStatic = emailStatic;
            txtEmail.setText(emailNonStatic);
        }
            
            
        
        // session manager
        session = new SessionManager(getApplicationContext());
 
        if (!session.isLoggedIn()) {
            logoutUser();
        }
 
 
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    
    }
    
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
 
        // Launching the login activity
        Intent intent = new Intent(TestActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

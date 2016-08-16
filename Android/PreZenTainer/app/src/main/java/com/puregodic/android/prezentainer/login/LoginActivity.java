
package com.puregodic.android.prezentainer.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.puregodic.android.prezentainer.HomeActivity;
import com.puregodic.android.prezentainer.R;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.network.AppController;
import com.puregodic.android.prezentainer.network.NetworkConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = LoginActivity.class.getSimpleName();
    private DialogHelper mDialogHelper;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText inputUserId = (EditText) findViewById(R.id.userId);
        final EditText inputPassword = (EditText) findViewById(R.id.password);
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final TextView btnLinkToRegister = (TextView) findViewById(R.id.btnLinkToRegisterScreen);
        final LinearLayout rootView = (LinearLayout)findViewById(R.id.loginActivityView);

        // Hide keyboard when user touch the display
        rootView.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        
        // Set userId and password at the edit text from RegisterActivity
        Intent fromRegisterIntent = getIntent();
        String fromRegisterUserId = fromRegisterIntent.getStringExtra("userId");
        String fromRegisterPwd = fromRegisterIntent.getStringExtra("password");
        
        if(fromRegisterUserId !=null && fromRegisterPwd != null){
            inputUserId.setText(fromRegisterUserId);
            inputPassword.setText(fromRegisterPwd);
        }
        
        
        // Progress dialog
        mDialogHelper = new DialogHelper(this);
        
        // Session manager
        mSessionManager = new SessionManager(getApplicationContext());

        // User login session check
        if (mSessionManager.isLoggedIn()) {
            
            // Get user account info from session
            HashMap<String, String> user = mSessionManager.getUserDetails();
            String userId  = user.get(SessionManager.KEY_USER_ID);
            // Already logged in -> HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
 
        // Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String userId = inputUserId.getText().toString();
                String password = inputPassword.getText().toString();
 
                // Check for empty data in the form
                if (userId.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(userId, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(LoginActivity.this,
                            "빈칸이 있는지 확인하세요.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
 
        });
 
        // Link to RegisterActivity
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Verify user account in DB by php [POST]
     * @param userId
     * @param password
     */
    private void checkLogin(final String userId, final String password) {


        mDialogHelper.showPdialog("잠시만 기다려주세요...", false);
        
        
        StringRequest strReq = new StringRequest(Method.POST,
                NetworkConfig.URL_ACCOUNT, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        mDialogHelper.hidePdialog();
                        
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
 
                            if (!error) {
                                // User successfully logged in
                                // Create login session
                                mSessionManager.setLogin(true,userId);
                                
                                // Launch Homeactivity
                                Intent intent = new Intent(LoginActivity.this,
                                        HomeActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.e(TAG,"계정정보 불일치 : "+ errorMsg);
                                Toast.makeText(LoginActivity.this,"아이디와 비밀번호가 일치하지 않습니다"
                                        , Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
 
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(LoginActivity.this,"로그인 실패!\n네트워크가 불안정 합니다", Toast.LENGTH_SHORT).show();
                        mDialogHelper.hidePdialog();
                    }
                }) {
 
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "login");
                params.put("user_id", userId);
                params.put("password", password);
                return params;
            }
 
        };
 
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG);
    }
    

}

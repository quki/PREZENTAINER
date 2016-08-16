
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

import lecho.lib.hellocharts.model.Line;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private DialogHelper mDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText inputUserId = (EditText)findViewById(R.id.userId);
        final EditText inputPassword = (EditText)findViewById(R.id.password);
        final EditText inputPasswordCheck = (EditText)findViewById(R.id.password_check);
        final Button btnRegister = (Button)findViewById(R.id.btnRegister);
        final TextView btnLinkToLogin = (TextView)findViewById(R.id.btnLinkToLoginScreen);
        final LinearLayout rootView = (LinearLayout)findViewById(R.id.registerActivityView);
        
        // Hide keyboard when user touch the display
        rootView.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        // Progress dialog
        mDialogHelper = new DialogHelper(this);

        // Register user account
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String userId = inputUserId.getText().toString();
                final String password = inputPassword.getText().toString();
                final String passwordCheck = inputPasswordCheck.getText().toString();

                if (userId.trim().length() > 0 && password.trim().length() > 0 && passwordCheck.trim().length()>0) {
                    
                    if(password.equals(passwordCheck)){
                        registerUser(userId, password);
                    }else{
                        Toast.makeText(RegisterActivity.this, "비밀번호가 서로 다릅니다", Toast.LENGTH_LONG).show();
                        inputPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "빈칸이 있는지 확인하세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Launch to Login activity
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Insert user account into DB by php [POST]
     * @param userId
     * @param password
     */
    private void registerUser(final String userId, final String password) {

        mDialogHelper.showPdialog("회원정보 등록 중 ...", false);

        StringRequest strReq = new StringRequest(Method.POST, NetworkConfig.URL_ACCOUNT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        
                        mDialogHelper.hidePdialog();
                        
                        try {

                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            // Success to insert into DB
                             if (!error) {
                            // Launch login activity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("userId",userId );
                            intent.putExtra("password",password );
                            startActivity(intent);
                            finish();
                            
                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다!",
                                    Toast.LENGTH_LONG).show();

                           // Fail to insert into DB
                           } else{

                               String errorMsg = jObj.getString("error_msg");
                               if(errorMsg.equals("User already existed")){
                                   Toast.makeText(RegisterActivity.this,
                                           "이미 등록된 아이디입니다", Toast.LENGTH_LONG).show();
                               }else{
                                   Toast.makeText(RegisterActivity.this,
                                           errorMsg, Toast.LENGTH_LONG).show();
                               }
                           }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        mDialogHelper.hidePdialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("user_id", userId);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG);

    }

}

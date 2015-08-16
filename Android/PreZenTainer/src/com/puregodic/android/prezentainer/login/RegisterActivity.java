
package com.puregodic.android.prezentainer.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.puregodic.android.prezentainer.HomeActivity;
import com.puregodic.android.prezentainer.R;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.network.AppConfig;
import com.puregodic.android.prezentainer.network.AppController;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnRegister,btnLinkToLogin;

    private EditText inputFullName,inputEmail,inputPassword,inputPasswordCheck;
    private LinearLayout rootView;
    private DialogHelper mDialogHelper;

    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText)findViewById(R.id.name);
        inputEmail = (EditText)findViewById(R.id.email);
        inputPassword = (EditText)findViewById(R.id.password);
        inputPasswordCheck = (EditText)findViewById(R.id.password_check);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button)findViewById(R.id.btnLinkToLoginScreen);
        rootView = (LinearLayout)findViewById(R.id.registerActivityView);
        
        // 공백을 클릭시 EditText의 focus와 자판이 사라지게 하기
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

        // Session manager
        mSessionManager = new SessionManager(getApplicationContext());

        // 유저가 한번 로그인 했었는지 체크
        if (mSessionManager.isLoggedIn()) {
            // 유저가 이미 로그인 했었을 때...
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String passwordCheck = inputPasswordCheck.getText().toString();

                if (name.trim().length() > 0 && email.trim().length() > 0 && password.trim().length() > 0 && passwordCheck.trim().length()>0) {
                    
                    if(password.equals(passwordCheck)){
                        registerUser(name, email, password);
                    }else{
                        Toast.makeText(RegisterActivity.this, "비밀번호가 서로 다릅니다", Toast.LENGTH_LONG).show();
                        inputPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "빈칸이 있는지 확인하세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

   // 서버에 회원 정보 insert
    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        mDialogHelper.showPdialog("회원정보 등록 중 ...", false);

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        
                        mDialogHelper.hidePdialog();
                        
                        try {

                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                             if (!error) {
                                 // User successfully stored in MySQL

                            
                            // Launch login activity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("email",email );
                            intent.putExtra("password",password );
                            startActivity(intent);
                            finish();
                            
                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다!",
                                    Toast.LENGTH_LONG).show();

                           } else{
                               // Error occurred in registration. Get the error message
                               
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("tag", "register");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}

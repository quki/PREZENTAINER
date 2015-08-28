
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

public class LoginActivity extends AppCompatActivity {
    
 // LogCat tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private DialogHelper mDialogHelper;
    private SessionManager mSessionManager;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        
        
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        rootView = (LinearLayout)findViewById(R.id.loginActivityView);
        
        // ������ Ŭ���� EditText�� focus�� ������ ������� �ϱ�
        rootView.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        
        // RegisterActivity���Ŀ� ��ĭ�� ����
        Intent fromRegisterIntent = getIntent();
        String fromRegisterEmail = fromRegisterIntent.getStringExtra("email");
        String fromRegisterPwd = fromRegisterIntent.getStringExtra("password");
        
        if(fromRegisterEmail !=null && fromRegisterPwd != null){
            inputEmail.setText(fromRegisterEmail);
            inputPassword.setText(fromRegisterPwd);
        }
        
        
        // Progress dialog
        mDialogHelper = new DialogHelper(this);
        
        // Session manager
        mSessionManager = new SessionManager(getApplicationContext());
        
        // �α��� ���� ������ üũ
        if (mSessionManager.isLoggedIn()) {
            
            // get user data from session
            HashMap<String, String> user = mSessionManager.getUserDetails();
            String yourEmail  = user.get(SessionManager.KEY_YOUR_EMAIL);
            // �̹� �α����� �Ǿ������� HomeActivity��
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("yourId",yourEmail);
            startActivity(intent);
            finish();
        }
 
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
 
                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(LoginActivity.this,
                            "��ĭ�� �ִ��� Ȯ���ϼ���.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
 
        });
 
        // Link to Register Screen
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
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
 
        mDialogHelper.showPdialog("��ø� ��ٷ��ּ���...", false);
        
        
        
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        mDialogHelper.hidePdialog();
                        
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
 
                            // Check for error node in json
                            if (!error) {
                                // user successfully logged in
                                // Create login session
                                mSessionManager.setLogin(true,email);
                                
                                // Launch main activity
                                Intent intent = new Intent(LoginActivity.this,
                                        HomeActivity.class);
                                intent.putExtra("yourId", email);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.e(TAG,"�������� ����ġ : "+ errorMsg);
                                Toast.makeText(LoginActivity.this,"���̵�� ��й�ȣ�� ��ġ���� �ʽ��ϴ�"
                                        , Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
 
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(LoginActivity.this,"�α��� ����!\n��Ʈ��ũ�� �Ҿ��� �մϴ�", Toast.LENGTH_SHORT).show();
                        mDialogHelper.hidePdialog();
                    }
                }) {
 
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
 
                return params;
            }
 
        };
 
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    

}

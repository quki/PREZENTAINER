package com.puregodic.android.prezentainer.login;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
 
    // Shared Preferences
    SharedPreferences pref;
 
    Editor editor;
    Context _context;
 
    // Shared pref mode
    int PRIVATE_MODE = 0;
 
    // Shared preferences file name
    private static final String PREF_NAME = "Prezentainer_Login";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_YOUR_EMAIL = "yourEmail";
 
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    // Log in 상태를 저장 ( 당신의 Log in된 email을 갖고 있다. )
    public void setLogin(boolean isLoggedIn,String email) {
 
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_YOUR_EMAIL,email);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }
     
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    
    
    /* *
     * 
     *  ex) ["yourEmail","quki09@naver.com"]
     *    
     * */
    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
         
        // user email id
        user.put(KEY_YOUR_EMAIL, pref.getString(KEY_YOUR_EMAIL, null));
         
        // return user
        return user;
    }
    
        
}

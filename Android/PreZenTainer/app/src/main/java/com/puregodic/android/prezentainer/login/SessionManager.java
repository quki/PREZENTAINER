package com.puregodic.android.prezentainer.login;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();
    private SharedPreferences pref;
 
    private Editor editor; // to edit value in SharedPreferences

    // Shared pref mode
    int PRIVATE_MODE = 0;
 
    // Shared preferences file name
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";

    // Initialize SessionManager
    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    /**
     * Set Login status
     * @param isLoggedIn
     * @param userId
     */
    public void setLogin(boolean isLoggedIn, String userId) {
        editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_USER_ID,userId);
        // commit changes
        editor.apply();
        Log.d(TAG, "User login session modified!");
    }
     
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false); // default: false
    }
    
    
    /* *
     * 
     *  ex) "userId" : "test1"
     *    
     * */
    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        return user;
    }
}

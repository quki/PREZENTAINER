package com.puregodic.android.prezentainer.network;

import java.lang.reflect.Field;

import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application{

    public static final String TAG = AppController.class.getSimpleName();
 
    private RequestQueue mRequestQueue;
 
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDefaultTypeface();
    }
    
    public static synchronized AppController getInstance() {
        return mInstance;
    }
    
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
    
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    
    // Custom Font ¼³Á¤
    private void initDefaultTypeface() {
        try {
            Typeface defaultTypeface = Typeface.createFromAsset(getAssets(), "fonts/nanumgothic/NanumGothic.ttf");
            final Field field = Typeface.class.getDeclaredField("DEFAULT");
            field.setAccessible(true);
            field.set(null, defaultTypeface);
        } catch ( NoSuchFieldException e ) {
             e.printStackTrace();
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
         }
    }
}

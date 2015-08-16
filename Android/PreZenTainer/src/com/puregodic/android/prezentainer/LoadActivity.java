
package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.puregodic.android.prezentainer.adapter.LoadPtTitleAdapter;
import com.puregodic.android.prezentainer.adapter.LoadPtTitleData;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.login.RegisterActivity;
import com.puregodic.android.prezentainer.network.AppConfig;
import com.puregodic.android.prezentainer.network.AppController;

public class LoadActivity extends AppCompatActivity {
    
    
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    
    private RecyclerView.LayoutManager mLayoutManager;
    private DialogHelper mDialogHelper;
    private String yourId;
    private ArrayList<LoadPtTitleData> mDataList = new ArrayList<LoadPtTitleData>();
    private RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        
        mDialogHelper = new DialogHelper(this);
        
        Intent intent = getIntent();
        yourId = intent.getStringExtra("yourId");
        Toast.makeText(getApplicationContext(), yourId, Toast.LENGTH_SHORT).show();
        
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LoadPtTitleAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);
        
        setDataByVolley();
        
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
            
            @Override
            public void onLongClick(View view, int position) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onClick(View view, int position) {
                
                final String ptTitle = ((TextView)view.findViewById(R.id.loadPtTitle)).getText().toString();
                Toast.makeText(LoadActivity.this, ptTitle, Toast.LENGTH_SHORT).show();
            }
        }));
     
        
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        
        
        
       
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load, menu);
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
    
    /* 
     * Click, Touch Event Listener 커스터마이징
     * */
    public static interface ClickListener {
        
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
    static class  RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
 
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            // TODO Auto-generated method stub
            
        }
    }
 
    // DB로 부터 response받고,JSON파싱 이후 adapter에 저장 (데이터 변화 감지)
    private void setDataByVolley(){
        
        mDialogHelper.showPdialog("잠시만 기다려주세요...", true);
        
        
        
        
        JsonObjectRequest request = new JsonObjectRequest(AppConfig.URL_FETCH, null,
                
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                           mDialogHelper.hidePdialog();
                           
                         // parsing
                           try {
                               
                               JSONArray mJsonArray = response.getJSONArray("myJson");
                               Log.v(TAG, mJsonArray.toString());
                               
                               for(int i=0; i<mJsonArray.length(); i++){
                                   JSONObject jsonObj = (JSONObject)mJsonArray.get(i);
                                   String title = jsonObj.getString("title");
                                   String date = jsonObj.getString("date");
                                   LoadPtTitleData mData =  new LoadPtTitleData();
                                   mData.setTitle(title);
                                   mData.setDate(date);
                                   mDataList.add(mData);
                                   }
                               
                        } catch (JSONException e) {
                            
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                           
                           mAdapter.notifyDataSetChanged();
                           
                    }
            
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDialogHelper.hidePdialog();
                        Log.e(TAG, "Server Error: " + error.getMessage());
                        
                    }
                });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
        
        
    }
    
}

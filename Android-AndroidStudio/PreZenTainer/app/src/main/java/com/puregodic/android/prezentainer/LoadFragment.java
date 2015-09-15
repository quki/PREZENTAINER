
package com.puregodic.android.prezentainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.puregodic.android.prezentainer.adapter.LoadPtTitleAdapter;
import com.puregodic.android.prezentainer.adapter.LoadPtTitleData;
import com.puregodic.android.prezentainer.decoration.CustomItemAnimator;
import com.puregodic.android.prezentainer.decoration.DividerItemDecoration;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.login.RegisterActivity;
import com.puregodic.android.prezentainer.network.NetworkConfig;
import com.puregodic.android.prezentainer.network.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadFragment extends Fragment {
    
    
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    
    private RecyclerView.LayoutManager mLayoutManager;
    private DialogHelper mDialogHelper;
    private String yourId;
    private ArrayList<LoadPtTitleData> mDataList = new ArrayList<LoadPtTitleData>();
    private RecyclerView.Adapter mAdapter;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_load, container, false);
        // Inflate the layout for this fragment
        mDialogHelper = new DialogHelper(getActivity());

        Intent intent = getActivity().getIntent();
        yourId = intent.getStringExtra("yourId");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new LoadPtTitleAdapter(rootView.getContext(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        // this is the default; this call is actually only necessary with custom ItemAnimators
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        setDataByVolley();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(rootView.getContext(), mRecyclerView, new ClickListener() {

            @Override
            public void onLongClick(View view, int position) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onClick(View view, int position) {

                final String ptTitle = ((TextView)view.findViewById(R.id.loadPtTitle)).getText().toString();
                final String currDate = ((TextView)view.findViewById(R.id.loadCurrDate)).getText().toString();

                startActivity(new Intent(getActivity(),ResultActivity.class)
                        .putExtra("yourId",yourId)
                        .putExtra("title", ptTitle)
                        .putExtra("date", currDate));
            }
        }));



        return rootView;
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
        
        StringRequest strReq = new StringRequest(Method.POST, NetworkConfig.URL_FETCH,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        
                        mDialogHelper.hidePdialog();
                        try {
                            // String response -> JSON Array -> JSON Object 추출 -> 개별 항목 parsing
                            JSONArray jArray = new JSONArray(response);
                            
                            if(jArray.length()==0){
                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container_body, new SettingFragment())
                                        .commit();
                                getActivity().setTitle("시작하기");
                                Toast.makeText(getActivity(), "발표를 먼저 시작하세요", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.e("PARSING", jArray.toString());
                                for(int i = 0; i<jArray.length(); i++){
                                    JSONObject jObj = (JSONObject)jArray.get(i);
                                    String title = jObj.getString("title");
                                    String date = jObj.getString("date");
                                    LoadPtTitleData mData =  new LoadPtTitleData();
                                    mData.setTitle(title);
                                    mData.setDate(date);
                                    mDataList.add(mData);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException : " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        mDialogHelper.hidePdialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("yourId", yourId);
                return params;
            }
            // Setting Encoding at Volley
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(new String(utf8String), HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
        
    }
    @Override
    public void onAttach (Activity activity){
        super.onAttach(activity);
    }
    @Override
    public void onDetach () {
        super.onDetach();
    }
    
}

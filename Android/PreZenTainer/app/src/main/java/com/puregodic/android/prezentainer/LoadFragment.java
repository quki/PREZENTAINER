
package com.puregodic.android.prezentainer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.puregodic.android.prezentainer.decoration.DividerItemDecoration;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.login.RegisterActivity;
import com.puregodic.android.prezentainer.network.AppController;
import com.puregodic.android.prezentainer.network.NetworkConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadFragment extends Fragment {


    private static final String TAG = RegisterActivity.class.getSimpleName();


    private DialogHelper mDialogHelper;
    private ArrayList<LoadPtTitleData> mDataList = new ArrayList<LoadPtTitleData>();
    private RecyclerView.Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_load, container, false);
        // Inflate the layout for this fragment
        mDialogHelper = new DialogHelper(getActivity());

        final String userId = getActivity().getIntent().getStringExtra("userId");

        final RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new LoadPtTitleAdapter(rootView.getContext(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchDataByVolley(userId);


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(rootView.getContext(), mRecyclerView, new ClickListener() {

            // Long Click시 data 삭제
            @Override
            public void onLongClick(View view, int position) {

                final String title = ((TextView) view.findViewById(R.id.loadPtTitle)).getText().toString();
                final String date = ((TextView) view.findViewById(R.id.loadCurrDate)).getText().toString();

                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                aBuilder.setTitle(title)
                        .setMessage("\n" + date + "에 저장한 " + title + " 발표를\n\n" + "정말로 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDataByVolley(userId,title, date);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = aBuilder.create();
                dialog.show();

            }

            // Click시 계정정보, PPT Title, date 값을 ResultActivity에 인텐트 및 전달
            @Override
            public void onClick(View view, int position) {

                final String ptTitle = ((TextView) view.findViewById(R.id.loadPtTitle)).getText().toString();
                final String currDate = ((TextView) view.findViewById(R.id.loadCurrDate)).getText().toString();

                startActivity(new Intent(getActivity(), ResultActivity.class)
                        .putExtra("userId", userId)
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

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;


            // GestureDectector : 제스처 커스터마이징, 짧게 눌렀을때, 오래 눌렀을 때
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


                //손가락을 땟을 때
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

        }
    }



    /**
     * post userId and fetch data from DB to make the list
     * @param userId
     */
    private void fetchDataByVolley(final String userId) {
        mDataList.clear();
        mDialogHelper.showPdialog("잠시만 기다려주세요...", true);

        StringRequest strReq = new StringRequest(Method.POST, NetworkConfig.URL_FETCH_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        mDialogHelper.hidePdialog();
                        Log.i(TAG,response);

                        // String response -> JSON Array -> JSON Object
                        try{
                            JSONArray jArray = new JSONArray(response);

                            // If no data -> back to SettingFragment
                            if (jArray.length() != 0) {

                                for(int i = 0; i<jArray.length();i++){
                                    JSONObject jObject = (JSONObject) jArray.get(i);
                                    String title = jObject.getString("title");
                                    String date = jObject.getString("created");
                                    JSONArray jArrayHbr;

                                    // Heart rate measurement error -> catch "undefined" exception
                                    if (jObject.getString("heart_rate").equals("undefined")) {
                                        jArrayHbr = new JSONArray("[60]");
                                    } else {
                                        jArrayHbr = new JSONArray(jObject.getString("heart_rate"));
                                    }

                                    // Heart rate(JSON Array) -> float ArrayList
                                    ArrayList<Float> heartRateList = new ArrayList<>();

                                    for (int j = 0; j < jArrayHbr.length(); j++) {
                                        float heartRateValue = Float.parseFloat(jArrayHbr.get(j).toString());
                                        heartRateList.add(heartRateValue);
                                    }

                                    // Set field to LoadPtTitleData instance
                                    LoadPtTitleData mPt = new LoadPtTitleData();
                                    mPt.setTitle(title);
                                    mPt.setDate(date);
                                    mPt.setHbr(heartRateList);
                                    mDataList.add(mPt);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }else{
                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container_body, new SettingFragment())
                                        .commit();
                                getActivity().setTitle("시작하기");
                                Toast.makeText(getActivity(), "발표를 먼저 시작하세요", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            Log.e(TAG, "JSONException : " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());

                mDialogHelper.hidePdialog();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_body, new SettingFragment())
                        .commit();
                getActivity().setTitle("시작하기");
                Toast.makeText(getActivity(), "네트워크 연결을 확인하세요", Toast.LENGTH_SHORT).show();

            }
        }) {
            // Posting userId to fetch data by php
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }


    /**
     * If long click, delete row in DB
     * @param userId
     * @param title
     * @param date
     */
    private void deleteDataByVolley(final String userId, final String title, final String date) {

        mDialogHelper.showPdialog("데이터를 삭제 중 입니다...", true);

        StringRequest strReq = new StringRequest(Method.POST, NetworkConfig.URL_DELETE,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        mDialogHelper.hidePdialog();
                        Toast.makeText(getActivity(), "삭제 완료", Toast.LENGTH_SHORT).show();

                        // View reload asynchronous
                        fetchDataByVolley(userId);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                mDialogHelper.hidePdialog();
                Toast.makeText(getActivity(), title + "\n삭제 실패", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("created", date);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

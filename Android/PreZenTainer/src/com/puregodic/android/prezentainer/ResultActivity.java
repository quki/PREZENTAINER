
package com.puregodic.android.prezentainer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lecho.lib.hellocharts.samples.R;
import lecho.lib.hellocharts.samples.LineChartActivity.PlaceholderFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.network.AppConfig;
import com.puregodic.android.prezentainer.network.AppController;

public class ResultActivity extends AppCompatActivity {

    private String title,yourId,date;
    private DialogHelper mDialogHelper;
    private static final String TAG = ResultActivity.class.getSimpleName();
    
    LinearLayout chart_area ;
    Button buttonPlay;
    Button buttonStop;
    SeekBar seekbar;
    MediaPlayer audio;
    
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        mDialogHelper = new DialogHelper(this);
        
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
        date = getIntent().getStringExtra("date");
        
        fetchDataByVolley();
        
        
    }



    
    private void fetchDataByVolley(){
        

        mDialogHelper.showPdialog("잠시만 기다려주세요...", true);
        
       
        
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_FETCH_GRAPH,
                new Response.Listener<String>() {
            
            

                    @Override
                    public void onResponse(String response) {
                        
                        
                        mDialogHelper.hidePdialog();
                        
                        try {
                            
                            // String response -> JSON Object -> JSON Array 추출 -> 개별 항목 parsing
                            JSONObject jObj = new JSONObject(response);
                            Log.d("PARSING", jObj.toString());
                            
                                JSONArray time = new JSONArray(jObj.getString("time"));
                                JSONArray hbr = new JSONArray(jObj.getString("hbr"));
                                
                                ArrayList<Float> heartRateList= new ArrayList<Float>();
                                ArrayList<Float> eventTimeList= new ArrayList<Float>(); 
                                
                                for(int i = 0; i<hbr.length(); i++){
                                    float heartRateValue = Float.parseFloat(hbr.get(i).toString());
                                   heartRateList.add(heartRateValue);
                                }
                                
                                for(int i = 0; i<time.length(); i++){
                                    float eventTimeValue = Float.parseFloat(time.get(i).toString());
                                    eventTimeList.add(eventTimeValue);
                                 }
                                
                                Log.d("PARSING", heartRateList.toString());
                                Log.d("PARSING", eventTimeList.toString());
                                
                             // Set Fragment
                                getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.container, new PlaceholderFragment(heartRateList, eventTimeList))
                                .commit();
                                
                                
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
                // Posting params to register url ( 해당 id && 해당 title인 row )
                Map<String, String> params = new HashMap<String, String>();
                params.put("yourId", yourId);
                params.put("title", title);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
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
}

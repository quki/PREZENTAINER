
package com.puregodic.android.prezentainer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

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
    private ArrayList<Double> heartRateList;
    private ArrayList<Double> eventTimeList;
    
    
    protected GraphicalView mChartView;
    Button buttonPlay;
    Button buttonStop;
    SeekBar seekbar;
    MediaPlayer audio;
    
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        // ����ڵ����� eventTime�� ���� ArrayList
        heartRateList = new ArrayList<Double>();
        eventTimeList = new ArrayList<Double>();
        
        mDialogHelper = new DialogHelper(this);
        
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
        date = getIntent().getStringExtra("date");
        Toast.makeText(getApplicationContext(), title+yourId+date, Toast.LENGTH_SHORT).show();
        
        
        fetchDataByVolley();
        
        
        Uri audioPath = Uri.parse(FileTransferRequestedActivity.DIR_PATH + title+date+".amr");
        
        audio = MediaPlayer.create(this,audioPath);
        
        String[] titles = new String[] { "PPT" , "Slide_num"};
        List<double[]> x = new ArrayList<double[]>();
        List<double[]> values = new ArrayList<double[]>();
        
        //double[] hbr_x= new double[audio.getDuration()/5000];      //�ɹڼ� x�� 
        double[] hbr_x= new double[]{5,10,15,20,25,30,35,40,45,50,55,60};
        for(int i=0; i<audio.getDuration()/5000 ; i++)
        {
           hbr_x[i]=i*5;
        }                                                        
        x.add(hbr_x);                                    
        
        //double[] hbr_y= new double[audio.getDuration()/5000];      //�ɹڼ� y��                                                       //�ɹڼ� y��
        double[] hbr_y= new double[]{60,65,68,72,77,81,59,78,63,66,67,78};
         
        values.add(hbr_y);                                         
         
         //double[] slide_x= new double[audio.getDuration()/5000];     //�����̵� �ѱ�ð� x ��
         double[] slide_x= new double[]{13,36,60};
         x.add(slide_x);
         
         
         
         //values.add(new double[] { 40,40, 40, 40, 40});               //�����̵� �ѱ�ð� y ��
         values.add(new double[] {50,50,50});
         
         int[] colors = new int[] { Color.BLUE ,Color.RED};
         PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE ,PointStyle.SQUARE};
         XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
         int length = renderer.getSeriesRendererCount();
         for (int i = 0; i < length; i++) {
           ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
         }
        
         setChartSettings(renderer, "MY PREZENTATION", "Minute", "Heart Rate", 0, audio.getDuration()/1000, 30, 170,
                 Color.LTGRAY, Color.LTGRAY);                                         //x�� ���ۿ��� ��������
             renderer.setXLabels(12);
             renderer.setYLabels(10);
             renderer.setShowGrid(true);
             renderer.setXLabelsAlign(Align.RIGHT);
             renderer.setYLabelsAlign(Align.RIGHT);
             renderer.setZoomButtonsVisible(true);
             renderer.setPanLimits(new double[] { 0,audio.getDuration()/1000 , 0, 180  });
             renderer.setZoomLimits(new double[] { 0,audio.getDuration()/1000 , 0, 180   });
             
             LinearLayout chart_area = (LinearLayout) findViewById(R.id.chart_area);
             
             mChartView = ChartFactory.getCubeLineChartView(this, buildDataset(titles, x, values), renderer, 0.33f);
             chart_area.addView(mChartView);
             
             

               
               /**
                * ���� �ݺ��� �����ϴ� �κ�
                * true : ���ѹݺ� ����, false : ���ѹݺ� ����
                */
               audio.setLooping(true);
               
               buttonPlay = (Button) findViewById(R.id.buttonPlay);
               buttonStop = (Button) findViewById(R.id.buttonStop);
               seekbar = (SeekBar) findViewById(R.id.seekBar1);
               
               /**
                * seekbar�� �ִ��� ������ �ִ����, �� music.getDuration()�� ���� ���� �����մϴ�
                */
               seekbar.setMax(audio.getDuration());
               
               /**
                * ��ũ�ٸ� ���������� ���� ��� ��ġ�� ���Ҽ� �ֵ��� �����մϴ�
                */
               seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                  @Override
                  public void onStopTrackingTouch(SeekBar seekBar) {
                     // TODO Auto-generated method stub
                  }
                  
                  @Override
                  public void onStartTrackingTouch(SeekBar seekBar) {
                     // TODO Auto-generated method stub
                  }
                  
                  @Override
                  public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                     // TODO Auto-generated method stub
                     /**
                      * ����°�� �Ѿ���� boolean fromUser�� ��� true�϶��� ����ڰ� ���� �����ΰ��,
                      * false�ΰ�쿡�� �ҽ���, ���û󿡼� �����ΰ���̸�
                      * ���⼭�� ����ڰ� ���� ������ ��쿡�� �۵��ϵ��� if���� �������
                      * 
                      * ���� : if���� { } ��ȣ ���� ���� �����ϰ�� ������ �����մϴ�
                      */
                     if (fromUser)
                        audio.seekTo(progress);
                  }
               });
        
    }
    

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        
        Toast.makeText(getApplicationContext(), "onPostCreate", Toast.LENGTH_SHORT).show();
        
        Log.d("PARSING", heartRateList.toString());
        Log.d("PARSING", eventTimeList.toString());
        
        super.onPostCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        audio.stop();
        finish();
        
        super.onDestroy();
    }


    //�׷��� �����Լ�///////////////////////////
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
           String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
           int labelsColor) {
         renderer.setChartTitle(title);
         renderer.setXTitle(xTitle);
         renderer.setYTitle(yTitle);
         renderer.setXAxisMin(xMin);
         renderer.setXAxisMax(xMax);
         renderer.setYAxisMin(yMin);
         renderer.setYAxisMax(yMax);
         renderer.setAxesColor(axesColor);
         renderer.setLabelsColor(labelsColor);
       }
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
         XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
         setRenderer(renderer, colors, styles);
         return renderer;
       }
    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
         renderer.setAxisTitleTextSize(16);
         renderer.setChartTitleTextSize(20);
         renderer.setLabelsTextSize(15);
         renderer.setLegendTextSize(15);
         renderer.setPointSize(5f);
         renderer.setMargins(new int[] { 20, 30, 15, 20 });
         int length = colors.length;
         for (int i = 0; i < length; i++) {
           XYSeriesRenderer r = new XYSeriesRenderer();
           r.setColor(colors[i]);
           r.setPointStyle(styles[i]);
           renderer.addSeriesRenderer(r);
         }
       }
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
           List<double[]> yValues) {
         XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
         addXYSeries(dataset, titles, xValues, yValues, 0);
         return dataset;
       }
    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
           List<double[]> yValues, int scale) {
         int length = titles.length;
         for (int i = 0; i < length; i++) {
           XYSeries series = new XYSeries(titles[i], scale);
           double[] xV = xValues.get(i);
           double[] yV = yValues.get(i);
           int seriesLength = xV.length;
           for (int k = 0; k < seriesLength; k++) {
             series.add(xV[k], yV[k]);
           }
           dataset.addSeries(series);
         }
       } 
    //���� ����////////////////////////////////////    
    public void buttonPlay(View v){
        /**
         * music.isPlaying()�� true : ������ ���� ������Դϴ�, false : ������� �ƴմϴ�
         */
        // ������ �����մϴ�
        if(audio.isPlaying()) {
           //����
           audio.pause();
           buttonPlay.setText("���");
        }
        else {
           //���
           audio.start();
           buttonPlay.setText("�Ͻ�����");
        }         
        /**
        * �����带 ���� 1�ʸ��� SeekBar�� �����̰� �մϴ�
        */
        Thread();
     }
     
     public void buttonStop(View v){
        //buttonStop ����� ������ ����

        audio.stop();
        try {
           // ������ ����Ұ�츦 ����� �غ��մϴ�
           // prepare()�� ���ܰ� 2������ �ʿ��մϴ�
           audio.prepare();
        } catch (IllegalStateException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
        // ���� ���� ������ 0, �� ó������ �ǵ����ϴ�
        audio.seekTo(0);

        // ��ư�� ���ڸ� ��������, ��ũ�ٸ� ó������ �ǵ����ϴ�
        seekbar.setProgress(0);
        buttonPlay.setText("���");

     }
     
     /**
      * ������� �ѹ��� ����Ҽ� �����Ƿ� ���� �޼ҵ�ȭ �Ͽ� ����ø��� �ٽ� �����մϴ�
      * 
      * ���� : http://indy9052.blog.me/120142002766
      * http://naiacinn.tistory.com/109
      * http://nephilim.tistory.com/56
      * (�����尡 �̹� ������� ���¿��� start()�޼ҵ尡 2���̻� ȣ��Ǹ� �������� ������ �߻��ϸ�
      * ������� �ѹ��� �����Ҽ� �ֽ��ϴ�, �� �ѹ� ������ ������ �մϴ�)
      */
     public void Thread(){
        Runnable task = new Runnable(){
           public void run(){
              /**
               * while���� ������ ������ �������϶� �Լ� ���ư��� �մϴ�
               */
              while(audio.isPlaying()){
                 try {
                    Thread.sleep(1000);
                 } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                 }
                 /**
                  * music.getCurrentPosition()�� ���� ���� ��� ��ġ�� �������� ���� �Դϴ�
                  */
                 seekbar.setProgress(audio.getCurrentPosition());
              }
           }
        };
        Thread thread = new Thread(task);
        thread.start();
     }
    
    private void fetchDataByVolley(){
        

        mDialogHelper.showPdialog("��ø� ��ٷ��ּ���...", true);
        
       
        
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_FETCH_GRAPH,
                new Response.Listener<String>() {
            
            

                    @Override
                    public void onResponse(String response) {
                        
                        
                        mDialogHelper.hidePdialog();
                        
                        try {
                            
                            
                            
                            // String response -> JSON Object -> JSON Array ���� -> ���� �׸� parsing
                            JSONObject jObj = new JSONObject(response);
                            Log.d("PARSING", jObj.toString());
                            
                                JSONArray time = new JSONArray(jObj.getString("time"));
                                JSONArray hbr = new JSONArray(jObj.getString("hbr"));
                                
                                for(int i = 0; i<hbr.length(); i++){
                                   double Y_axisHeartRate = Double.valueOf(hbr.get(i).toString()).doubleValue();
                                   heartRateList.add(Y_axisHeartRate);
                                }
                                
                                for(int i = 0; i<time.length(); i++){
                                    double X_axisEventTime = Double.valueOf(time.get(i).toString()).doubleValue();
                                    eventTimeList.add(X_axisEventTime);
                                 }
                                
                                Log.d("PARSING", heartRateList.toString());
                                Log.d("PARSING", eventTimeList.toString());
                                
                                
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
                // Posting params to register url ( �ش� id && �ش� title�� row )
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

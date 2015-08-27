
package com.puregodic.android.prezentainer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
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

    private static final String TAG = ResultActivity.class.getSimpleName();
 
    private String title,yourId,date;
    private DialogHelper mDialogHelper;
    private String mFilePath;
    private static final int SEND_THREAD_INFOMATION = 1;
    private TextView pptTitle;
    
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        pptTitle = (TextView)findViewById(R.id.pptTitle);
        mDialogHelper = new DialogHelper(this);
        
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
        date = getIntent().getStringExtra("date");
        // 녹음 파일 경로
        String fileExtension = ".amr";
        mFilePath= FileTransferRequestedActivity.DIR_PATH + title + date+ fileExtension;
        
        pptTitle.setText(title);
        
        fetchDataByVolley();
        
    }
    
    /* Fragment
     * Chart, SeekBar 모두 PlaceholderFragment에서 작업*/
    public class PlaceHolderFragment extends Fragment{
        
        private ArrayList<Float> heartRateList;
        private ArrayList<Float> eventTimeList; 
        
        private LineChartView chart;
        private LineChartData data;
        private LineChartData preData;
        private PreviewLineChartView previewChart;
        private LineChartData previewData;
        Button buttonPlay;
        Button buttonStop;
        SeekBar seekbar;
        MediaPlayer audio;
        TextView textViewTime;
        TextView textViewHR;
        String meanHeartRate = null;
        int audioSize;
        private int numberOfLines = 2;
        
        private boolean hasYaxis = true;
        private ValueShape shape = ValueShape.CIRCLE;
        
        public final Handler timeHandler = new TimeHandler(this);
        
        public PlaceHolderFragment() {
        }
        
        // Volley로 부터 받아온 ArrayList 초기화 작업
        public PlaceHolderFragment(ArrayList<Float> heartRateList ,ArrayList<Float> eventTimeList) {
            this.heartRateList = heartRateList;
            this.eventTimeList = eventTimeList;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
            
            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);
            chart.setOnValueTouchListener(new ValueTouchListener());
            textViewTime = (TextView) findViewById(R.id.textViewTime);
            textViewHR = (TextView) findViewById(R.id.textViewHR);
            
            
            
            //아래부터 Audio 및 SeekBar작업
            Uri audioPath = Uri.parse(mFilePath);
            audio = MediaPlayer.create(getApplicationContext(), audioPath);
            
            audio.setLooping(true);
            
            buttonPlay = (Button) findViewById(R.id.buttonPlay);
            buttonStop = (Button) findViewById(R.id.buttonStop);
            seekbar = (SeekBar) findViewById(R.id.seekBar1);

            
            /**
             * seekbar의 최댓값을 음악의 최대길이, 즉 music.getDuration()의 값을 얻어와 지정
             */
            audioSize = audio.getDuration();
            seekbar.incrementProgressBy(1);
            seekbar.setMax(audioSize);
            
            // 최초에에 chart에 뿌려 줄 data 생성 
            generateData();
            viewPortSetting();
            // 자동으로 chart가 계산 되는 것 방지
            chart.setViewportCalculationEnabled(false);
            
            
            /**
             * 시크바를 움직였을떄 음악 재생 위치도 변할수 있도록 지정
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
                       
                   /*
                    *          < currentSecond >
                    * 1. millisecond -> index 변환
                    * 2. progress/1000 (초) 
                    * 3. (progress/1000)-1 (인덱스)
                    * 
                    *           < realIndex >
                    * 1. mIndex중 5의 배수를 찾아서 realIndex에 대입
                    * 2. 단, 일의 자리(5단위)에서 내림
                    * ex)  43 -> 40, 47 -> 45     
                    *     
                    * */

                   int currentSecond = ((progress/1000)-1);   
                   int xValue = 0;
                   if( currentSecond % 5 == 0 ){
                       xValue = currentSecond;
                   }
                   else{
                       xValue = currentSecond-(currentSecond % 5);
                   }

                   int lastXvalue = (data.getLines().get(0).getValues().size()-1)*5;

                   Log.d("mIndex!!", "currentsecond : "+currentSecond);
                   Log.d("rIndex!!", "xValue : "+xValue);
                   Log.d("LastIndex!!","LastIndex : "+lastXvalue);
                   float line0ValueY = 0;
                   
                   /*
                    * progress로 ArrayList의 index를 참조할 때,
                    * IndexOutOfBoudsException 방지를 위해
                    * 
                    * */
                   if(xValue <= lastXvalue){
                       line0ValueY = data.getLines().get(0).getValues().get((xValue/5)).getY(); // 심박수 Value
                       data.getLines().get(1).getValues().get(0).set(xValue, line0ValueY);
                   }else{
                       xValue = 0;
                       line0ValueY = data.getLines().get(0).getValues().get((xValue/5)).getY();
                       data.getLines().get(1).getValues().get(0).set(xValue, line0ValueY);
                   }
                   chart.setLineChartData(data);

                   // user가 클릭할 경우 해당 position으로 progressbar이동
                   if (fromUser)
                       audio.seekTo(progress);
               }
            });
            
            
            buttonPlay.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    buttonPlay();
                }
            });
            
            buttonStop.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    buttonStop();
                }
            });
            
            return rootView;
        }

        @Override
        public void onDestroy() {
            audio.stop();
            finish();
            super.onDestroy();
        }
        
        // buttonPlay event callback
        public void buttonPlay(){
            if(audio.isPlaying()) {
               audio.pause();
               buttonPlay.setText("play");
            }
            else {
               audio.start();
               buttonPlay.setText("pause");
            }         
            Thread();
         }
         
        // buttonStop event callback
         public void buttonStop(){
            audio.stop();
            try {
                // stream을 준비
               audio.prepare();
            } catch (IllegalStateException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
            audio.seekTo(0);
            seekbar.setProgress(0);
            buttonPlay.setText("play");
         }
         
         // audio의 시간을 측정하는 별도의 Thread
         public void Thread(){Runnable task = new Runnable() {
             public void run() {
                 
                 // SeekBar 갱신을 위한 Code를 넣어줌
                 while (audio.isPlaying()) {
                     try {
                         // 1초 마다
                         Thread.sleep(1000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     // UI 갱신
                     seekbar.setProgress(audio.getCurrentPosition());
                     
                     Log.d("audio.getCurrentPosition()",
                             ":" + audio.getCurrentPosition());

                     Message msg = timeHandler.obtainMessage();
                     
                     msg.what = SEND_THREAD_INFOMATION; // 핸들러에 보내기 위한 식별 Id
                     msg.arg1 = Integer.valueOf(audio.getCurrentPosition()); // 핸들러에 보내는 인자 값 Integer
                     timeHandler.sendMessage(msg);

                 }
             }
         };
         Thread thread = new Thread(task);
         thread.start();
         }
         
         // MainThread에 접근하기 위한 Handler
         public class TimeHandler extends Handler {
              private final WeakReference<PlaceHolderFragment> mActivity;
             
              public TimeHandler(PlaceHolderFragment activity) {
                  mActivity = new WeakReference<PlaceHolderFragment>(activity);
              }

              @Override
              public void handleMessage(Message msg) {
                  
                  // msg = 현재 오디오 위치(Integer)
                  String stringTime = null;
                  String stringHR = null;
                  String stringWholeTime = null;
                  PlaceHolderFragment activity = mActivity.get();
                  super.handleMessage(msg);

                  switch (msg.what) {
                  case SEND_THREAD_INFOMATION:
                      stringTime = changeTimeForHuman(msg.arg1);
                      stringHR = currentHeartRateValue(heartRateList, msg.arg1);
                      stringWholeTime = changeTimeForHuman(audio.getDuration());
                      activity.textViewHR.setText("   "+stringHR + " / " + meanHeartRateValue(heartRateList));  // 현재심박수 / 평균 심박수
                      activity.textViewTime.setText("       "+stringTime + " / " + stringWholeTime);  // 현재시간 / 총 오디오 길이
                      break;
                  default:
                      break;
                  }

              }
              
          }
          
          // 총 심박수의 평균 값 구하기
          public String meanHeartRateValue(ArrayList<Float> heartRateList) {
              float heartRateSum = 0;
              String meanHeartRate = null;
              for (int i = 0; i < heartRateList.size(); i++) {
                  heartRateSum += heartRateList.get(i); 
              }
              meanHeartRate = Integer.toString((int)((heartRateSum / heartRateList.size())));
              return meanHeartRate;
          }
          
          // 현재 시간에 해당하는 심박수 값 구하기
          public String currentHeartRateValue(ArrayList<Float> heartRateList, int time) {
              int heartRateValueToInt = 0;
              String heartRateValueToString = null;
              
              if (time / (1000 * 5) > heartRateList.size() - 1) {
                  heartRateValueToInt =(int)((float)heartRateList.get(heartRateList.size() - 1));
                  heartRateValueToString = Integer.toString(heartRateValueToInt);
                  return heartRateValueToString;
              }else {
                  heartRateValueToInt = (int)((float)heartRateList.get(time / (1000 * 5)));
                  heartRateValueToString = Integer.toString(heartRateValueToInt);
              }
              
              return heartRateValueToString; 
          }
          
          // milliseconds를 사람이 볼 수 있는 시간으로 변환 ex) 02:37
          public String changeTimeForHuman (int time) {
              int secondTime = 0;
              int minuteTime = 0;
              int hourTime = 0;
                      
              String stringTime = null;
              String secondTimeToString = null;
              String minuteTimeToString = null;
              String hourTimeToString = null;
                      
              if (time / (1000 * 60 * 60) > 0) {
                  time = time / 1000;
                  secondTime = time % 60;
                  minuteTime = time / 60;
                  hourTime = minuteTime / 60;
                  
                  secondTimeToString = Integer.toString(secondTime);
                  minuteTimeToString = Integer.toString(minuteTime);
                  hourTimeToString = Integer.toString(hourTime);
                  
                  stringTime = hourTimeToString + ":" + minuteTimeToString + ":" + secondTimeToString;
              }
              else {
                  time = time / 1000;
                  secondTime = time % 60;
                  minuteTime = time / 60;
                  
                  secondTimeToString = Integer.toString(secondTime);
                  minuteTimeToString = Integer.toString(minuteTime);
                  
                  stringTime = minuteTimeToString + ":" + secondTimeToString;
                  }
              
              return stringTime;
          }
          // 최초에에 chart에 뿌려 줄 data 생성 
          private void generateData() {
              

              List<Line> lines = new ArrayList<Line>();
              List<Line> linesForPreData = new ArrayList<Line>();  //미리보기 데이터를 위한 List
              List<String> slideNum = new ArrayList<String>();
              
              // 축 값 설정 (슬라이드 번호)
              List<AxisValue> axisXvalue = new ArrayList<AxisValue>();
              for (int j = 0; j < eventTimeList.size(); ++j) {
                  slideNum.add(j+1+"번");
                  axisXvalue.add(new AxisValue(eventTimeList.get(j)/1000).setLabel(slideNum.get(j)));
              }
              for(int i=0; i<axisXvalue.size(); i++){
                  Log.d("axisXvalue", ""+axisXvalue.get(i));
              }
              
              for (int i = 0; i < numberOfLines; ++i) {

                  List<PointValue> values = new ArrayList<PointValue>();

                  for (int j = 0; j < heartRateList.size(); ++j) {

                      if(i == 1 && j == 0) {
                          values.add(new PointValue(j, lines.get(0).getValues().get(0).getY()));
                          Log.d("!!!!!!!!!!!", ""+lines.get(0).getValues().get(0).getY());
                          break;
                      } else {
                          values.add(new PointValue(j*5, heartRateList.get(j))); //adding point to the first line
                      }
                  }

                  Line line = new Line(values);
                  line.setColor(ChartUtils.COLORS[i]);
                  
                  // progress에 따라 움직이는 Point
                  if(i==1){
                      line.setHasLabels(true);
                  }
                  // 최초에 뿌려지는 chart의 line
                  else
                  {
                      line.setShape(shape); // point -> circle
                      line.setCubic(true); // line -> curve
                      line.setFilled(true); // area 채우기
                      line.setHasLabels(false);
                      line.setPointRadius(1);
                      line.setHasLabelsOnlyForSelected(false); //눌렀을 때, 라벨 표시
                  }
                  lines.add(line);
                  if(i==0){       
                    //미리보기 데이터에는 심장박동수 라인만 넣기!
                      linesForPreData.add(line);
                  }
              }

              data = new LineChartData(lines); // 최초에 뿌려진 data chart
              preData = new LineChartData(linesForPreData); // 미리보기 chart
              
              // X축은 무조건 설정
              Axis axisX = new Axis()
              .setHasLines(true)
              .setLineColor(ChartUtils.COLOR_RED).setTextColor(getResources().getColor(R.color.dark_black))
              .setValues(axisXvalue); 
              data.setAxisXBottom(axisX);
              // Y 축을 갖고 싶을 때
              if (hasYaxis) {
                  
                  Axis axisY = new Axis()
                  .setHasLines(true)
                  .setHasTiltedLabels(true)  // 글자 기울임
                  .setName("심박수");
                  data.setAxisYLeft(axisY);
                  
               // Y 축이 필요 없을 때
              } else {
                  data.setAxisYLeft(null);
              }

              data.setBaseValue(Float.NEGATIVE_INFINITY);
              
              chart.setLineChartData(data);
              chart.setZoomEnabled(false);
              chart.setScrollEnabled(false);
              
              // 미리 보기 설정
              previewData = new LineChartData(preData);
              previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
              previewChart.setLineChartData(previewData);
              previewChart.setViewportChangeListener(new ViewportListener());

              previewX(true);
              
          }
          
          /*
           * View Port Setting x축 y축 범위 지정 (미리보기)
           * X축 방향으로만 움직임, param으로 true를 주면 animation효과
           * 
           * */
          private void previewX(boolean animate) {
              Viewport tempViewport = new Viewport(chart.getMaximumViewport());
              tempViewport.top = 150;
              tempViewport.bottom = 50;
              tempViewport.left=0;
              tempViewport.right = audioSize/1000;
              if (animate) {
                  previewChart.setMaximumViewport(tempViewport);  // 설정한 ViewPort 값을 최대로 지정한다
                  previewChart.setCurrentViewportWithAnimation(tempViewport); // 현재 보여지는 창을 설정한 최대 ViewPort로 한다
              } else {
                  previewChart.setMaximumViewport(tempViewport);
                  previewChart.setCurrentViewport(tempViewport);
              }
              previewChart.setZoomType(ZoomType.HORIZONTAL);
          }
          
          // View Port Setting x축 y축 범위 지정
          private void viewPortSetting(){
              
              final Viewport v = new Viewport(chart.getMaximumViewport());
              v.bottom = 50;
              v.top = 150;
              v.left=0;
              v.right = audioSize/1000;
              chart.setMaximumViewport(v);
              chart.setCurrentViewportWithAnimation(v);
          }
          
          // Y축 없애는 option
          private void toggleYaxis() {
              hasYaxis = !hasYaxis;
              generateData();
          }
          
          private class ViewportListener implements ViewportChangeListener {

              @Override
              public void onViewportChanged(Viewport newViewport) {

                  chart.setCurrentViewport(newViewport);
              }

          }
          
          private class ValueTouchListener implements LineChartOnValueSelectListener {

              @Override
              public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                  Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
              }
              @Override
              public void onValueDeselected() {
                  // TODO Auto-generated method stub
              }
          }
          
          @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
              inflater.inflate(R.menu.result, menu);
              super.onCreateOptionsMenu(menu, inflater);
        }

          @Override
          public boolean onOptionsItemSelected(MenuItem item) {
              int id = item.getItemId();
              if (id == R.id.action_reset) {
                  generateData();
                  return true;
              }
              if (id == R.id.action_toggle_axes) {
                  toggleYaxis();
                  return true;
              }
              return super.onOptionsItemSelected(item);
          }
        
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
                                .add(R.id.chartContainer, new PlaceHolderFragment(heartRateList, eventTimeList))
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
                //params.put("date", date);
                
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

}

package com.puregodic.android.prezentainer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.puregodic.android.prezentainer.calculator.HeartRateCalculator;
import com.puregodic.android.prezentainer.dialog.DialogHelper;
import com.puregodic.android.prezentainer.network.AppController;
import com.puregodic.android.prezentainer.network.NetworkConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
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

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = ResultActivity.class.getSimpleName();
 
    private String title,yourId,date;
    private DialogHelper mDialogHelper;
    private String mFilePath;
    private static final int SEND_THREAD_INFOMATION = 1;
    private TextView pptTitle;
    private TextView pptDate;
    private Toolbar mToolbar;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        pptTitle = (TextView)findViewById(R.id.pptTitle);
        pptDate = (TextView)findViewById(R.id.pptDate);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(null);

        mDialogHelper = new DialogHelper(this);
        
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
        date = getIntent().getStringExtra("date");
        // �끃�쓬 �뙆�씪 寃쎈줈
        String fileExtension = ".amr";
        mFilePath= FileTransferRequestedActivity.DIR_PATH + title + date+ fileExtension;

        pptTitle.setText(title);
        pptDate.setText(date);

        fetchDataByVolley();
     }
    
    /* Fragment
     * Chart, SeekBar 紐⑤몢 PlaceholderFragment�뿉�꽌 �옉�뾽*/
    public class PlaceHolderFragment extends Fragment{
        
        private ArrayList<Float> heartRateList;
        private ArrayList<Float> rightEventTimeList, leftEventTimeList; 
        
        private LineChartView chart;
        private LineChartData data;
        private LineChartData preData;
        private PreviewLineChartView previewChart;
        private LineChartData previewData;
        private HeartRateCalculator heartCalcultor;

        Button buttonPlay;
        Button buttonStop;
        SeekBar seekbar;
        MediaPlayer audio;
        TextView heartRate;
        TextView highHeartrate;
        TextView lowHeartrate;
        TextView averageHeartrate;
        TextView runningTime,wholeTime;
        TextView score;

        int audioSize;
        private int numberOfLines = 2;
        
        private boolean hasYaxis = true;
        private ValueShape shape = ValueShape.CIRCLE;
        
        // Viewport�뒗 �돺寃� 留먰빐 �솕硫� (View)�씪怨� �깮媛곹븯硫� �맂�떎. 二쇰줈 蹂댁뿬吏��뒗 踰붿쐞瑜� 吏��젙�븷 �븣 二쇰줈 �궗�슜�맂�떎.
        private Viewport maxViewport,currentViewport;
        
        public final Handler timeHandler = new TimeHandler(this);
        
        public PlaceHolderFragment() {
        }
        
        // Volley濡� 遺��꽣 諛쏆븘�삩 ArrayList 珥덇린�솕 �옉�뾽
        public PlaceHolderFragment(ArrayList<Float> heartRateList ,ArrayList<Float> rightEventTimeList, ArrayList<Float> leftEventTimeList) {
            this.heartRateList = heartRateList;
            this.rightEventTimeList = rightEventTimeList;
            this.leftEventTimeList = leftEventTimeList;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
            
            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);
            chart.setOnValueTouchListener(new ValueTouchListener());

            heartRate = (TextView) findViewById(R.id.heartRate);
            highHeartrate = (TextView) findViewById(R.id.highHeartrate);
            lowHeartrate = (TextView) findViewById(R.id.lowHeartrate);
            averageHeartrate = (TextView) findViewById(R.id.averageHeartrate);
            runningTime = (TextView) findViewById(R.id.runningTime);
            wholeTime = (TextView) findViewById(R.id.wholeTime);
            score = (TextView) findViewById(R.id.score);

            buttonPlay = (Button) findViewById(R.id.buttonPlay);
            buttonStop = (Button) findViewById(R.id.buttonStop);
            seekbar = (SeekBar) findViewById(R.id.seekBar1);

            maxViewport = new Viewport(chart.getMaximumViewport());
            currentViewport = new Viewport(chart.getCurrentViewport());



            //�븘�옒遺��꽣 Audio 諛� SeekBar�옉�뾽
            Uri audioPath = Uri.parse(mFilePath);
            audio = MediaPlayer.create(getApplicationContext(), audioPath);

            if(audio != null){

                audio.setLooping(true);
                /**
                 * seekbar�쓽 理쒕뙎媛믪쓣 �쓬�븙�쓽 理쒕�湲몄씠, 利� music.getDuration()�쓽 媛믪쓣 �뼸�뼱�� 吏��젙
                 */
                audioSize = audio.getDuration();
                seekbar.incrementProgressBy(1);
                seekbar.setMax(audioSize);
                // 理쒖큹�뿉�뿉 chart�뿉 肉뚮젮 以� data �깮�꽦
                generateData();

                // �옄�룞�쑝濡� chart媛� 怨꾩궛 �릺�뒗 寃� 諛⑹�
                chart.setViewportCalculationEnabled(false);


                /**
                 * �떆�겕諛붾�� ��吏곸��쓣�뻹 �쓬�븙 �옱�깮 �쐞移섎룄 蹂��븷�닔 �엳�룄濡� 吏��젙
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
                    * 1. millisecond -> index 蹂��솚
                    * 2. progress/1000 (珥�)
                    * 3. (progress/1000)-1 (�씤�뜳�뒪)
                    *
                    *           < realIndex >
                    * 1. mIndex以� 5�쓽 諛곗닔瑜� 李얠븘�꽌 realIndex�뿉 ���엯
                    * 2. �떒, �씪�쓽 �옄由�(5�떒�쐞)�뿉�꽌 �궡由�
                    * ex)  43 -> 40, 47 -> 45
                    *
                    * */

                        int currentSecond = ((progress / 1000) - 1);
                        int xValue = 0;
                        if (currentSecond % 5 == 0) {
                            xValue = currentSecond;
                        } else {
                            xValue = currentSecond - (currentSecond % 5);
                        }

                        int lastXvalue = (data.getLines().get(0).getValues().size() - 1) * 5;

                        Log.d("mIndex!!", "currentsecond : " + currentSecond);
                        Log.d("rIndex!!", "xValue : " + xValue);
                        Log.d("LastIndex!!", "LastIndex : " + lastXvalue);
                        float line0ValueY = 0;

                   /*
                    * progress濡� ArrayList�쓽 index瑜� 李몄“�븷 �븣,
                    * IndexOutOfBoudsException 諛⑹�瑜� �쐞�빐
                    *
                    * */
                        if (xValue <= lastXvalue) {
                            line0ValueY = data.getLines().get(0).getValues().get((xValue / 5)).getY(); // �떖諛뺤닔 Value
                            data.getLines().get(1).getValues().get(0).set(xValue, line0ValueY);
                        } else {
                            xValue = 0;
                            line0ValueY = data.getLines().get(0).getValues().get((xValue / 5)).getY();
                            data.getLines().get(1).getValues().get(0).set(xValue, line0ValueY);
                        }
                        chart.setLineChartData(data);

                        // user媛� �겢由��븷 寃쎌슦 �빐�떦 position�쑝濡� progressbar�씠�룞
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

                //heartRate�젙蹂� 異쒕젰
                heartCalcultor = new HeartRateCalculator(heartRateList);
                averageHeartrate.setText("" + heartCalcultor.meanHeartRateValue());// �룊洹� �떖諛뺤닔
                highHeartrate.setText(heartCalcultor.HighHeartRateValue());        // 理쒓퀬 �떖諛뺤닔
                lowHeartrate.setText(heartCalcultor.LowHeartRateValue());          // 理쒖� �떖諛뺤닔
                score.setText(heartCalcultor.standardDeviation());                 // �젏�닔
                wholeTime.setText(" / "+changeTimeForHuman(audio.getDuration()));    // �삤�뵒�삤 珥� 湲몄씠
            }else{
                // �궗�슜�옄媛� �끃�쓬�뙆�씪�쓣 �룿�뿉�꽌 �궘�젣�븳 寃쎌슦 媛뺤젣 醫낅즺
                Toast.makeText(ResultActivity.this, "�끃�쓬�뙆�씪�씠 �뾾�꽕�슂", Toast.LENGTH_SHORT).show();
                finish();
            }

            return rootView;
        }

        @Override
        public void onDestroy() {
            if(audio!=null){
                audio.stop();
                finish();
            }
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
                // stream�쓣 以�鍮�
               audio.prepare();
            } catch (IllegalStateException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
            audio.seekTo(0);
            seekbar.setProgress(0);
            buttonPlay.setText("play");
            runningTime.setText("00:00");
         }
         
         // audio�쓽 �떆媛꾩쓣 痢≪젙�븯�뒗 蹂꾨룄�쓽 Thread
         public void Thread(){Runnable task = new Runnable() {
             public void run() {
                 
                 // SeekBar 媛깆떊�쓣 �쐞�븳 Code瑜� �꽔�뼱以�
                 while (audio.isPlaying()) {
                     try {
                         // 1珥� 留덈떎
                         Thread.sleep(1000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     // UI 媛깆떊
                     seekbar.setProgress(audio.getCurrentPosition());
                     
                     Log.d("audio.getCurrentPosition()",
                             ":" + audio.getCurrentPosition());

                     Message msg = timeHandler.obtainMessage();
                     
                     msg.what = SEND_THREAD_INFOMATION; // �빖�뱾�윭�뿉 蹂대궡湲� �쐞�븳 �떇蹂� Id
                     msg.arg1 = Integer.valueOf(audio.getCurrentPosition()); // �빖�뱾�윭�뿉 蹂대궡�뒗 �씤�옄 媛� Integer
                     timeHandler.sendMessage(msg);

                 }
             }
         };
         Thread thread = new Thread(task);
         thread.start();
         }
         
         // MainThread�뿉 �젒洹쇳븯湲� �쐞�븳 Handler
         public class TimeHandler extends Handler {
              private final WeakReference<PlaceHolderFragment> mActivity;
             
              public TimeHandler(PlaceHolderFragment activity) {
                  mActivity = new WeakReference<PlaceHolderFragment>(activity);
              }

              @Override
              public void handleMessage(Message msg) {
                  
                  // msg = �쁽�옱 �삤�뵒�삤 �쐞移�(Integer)
                  String stringTime = null;
                  String stringHR = null;
                  String stringWholeTime = null;
                  PlaceHolderFragment activity = mActivity.get();
                  super.handleMessage(msg);

                  switch (msg.what) {
                  case SEND_THREAD_INFOMATION:
                      stringTime = changeTimeForHuman(msg.arg1);
                      stringHR = heartCalcultor.currentHeartRateValue(msg.arg1);

                      activity.heartRate.setText(stringHR);                                // �쁽�옱 �떖諛뺤닔
                      activity.runningTime.setText(stringTime);  // �쁽�옱�떆媛� / 珥� �삤�뵒�삤 湲몄씠

                      break;
                  default:
                      break;
                  }

              }
              
          }

          // milliseconds瑜� �궗�엺�씠 蹂� �닔 �엳�뒗 �떆媛꾩쑝濡� 蹂��솚 ex) 02:37
          public String changeTimeForHuman (int time) {

              SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
              SimpleDateFormat sdfH = new SimpleDateFormat("hh:mm:ss");

              String stringTime;

              // 3600珥� �씠�긽�씪�븣, 利�, 1�떆媛꾩씠 �꽆�뼱媛� �븣
              if (time / (1000 * 60 * 60) > 0) {

                  stringTime = sdfH.format(time);
              }
              // 3600珥� �씠�븯�씪�븣, 利�, 1�떆媛꾩씠 �븞�맆 �븣
              else {
                  stringTime = sdf.format(time);
                  }
              
              return stringTime;
          }
          // 理쒖큹�뿉�뿉 chart�뿉 肉뚮젮 以� data �깮�꽦 
          private void generateData() {

              List<Line> lines = new ArrayList<Line>();
              List<Line> linesForPreData = new ArrayList<Line>();  //誘몃━蹂닿린 �뜲�씠�꽣瑜� �쐞�븳 List
              List<String> slideNum = new ArrayList<String>();


              for (int i = 0; i < leftEventTimeList.size(); i++) {
                  for (int j = 0; j < rightEventTimeList.size(); j++) {
                      if (leftEventTimeList.get(i) < rightEventTimeList.get(j)) {
                          rightEventTimeList.remove(j - 1);
                          leftEventTimeList.set(i, Float.valueOf(-1));
                          break;
                      }
                  }
              }

              for (int i = 0; i < leftEventTimeList.size(); i++) {
                  if (leftEventTimeList.get(i).compareTo(Float.valueOf(-1)) != 0 && !rightEventTimeList.isEmpty()) {
                      rightEventTimeList.remove(rightEventTimeList.size() - 1);
                  }
              }
              // 異� 媛� �꽕�젙 (�뒳�씪�씠�뱶 踰덊샇)
              List<AxisValue> axisXvalue = new ArrayList<AxisValue>();
              for (int j = 0; j < rightEventTimeList.size(); ++j) {
                  slideNum.add(j + 1 + "踰�");
                  axisXvalue.add(new AxisValue(rightEventTimeList.get(j) / 1000).setLabel(slideNum.get(j)));
              }
              for (int i = 0; i < axisXvalue.size(); i++) {
                  Log.d("axisXvalue", "" + axisXvalue.get(i));
              }

              for (int i = 0; i < numberOfLines; ++i) {

                  List<PointValue> values = new ArrayList<PointValue>();

                  for (int j = 0; j < heartRateList.size(); ++j) {

                      if (i == 1 && j == 0) {
                          values.add(new PointValue(j, lines.get(0).getValues().get(0).getY()));
                          break;
                      } else {
                          values.add(new PointValue(j * 5, heartRateList.get(j))); //adding point to the first line
                      }
                  }

                  Line line = new Line(values);
                  line.setColor(ChartUtils.COLORS[i]);

                  // progress�뿉 �뵲�씪 ��吏곸씠�뒗 Point
                  if (i == 1) {
                      line.setHasLabels(true);
                  }
                  // 理쒖큹�뿉 肉뚮젮吏��뒗 chart�쓽 line
                  else {
                      line.setShape(shape); // point -> circle
                      line.setCubic(true); // line -> curve
                      line.setFilled(true); // area 梨꾩슦湲�
                      line.setHasLabels(false);
                      line.setPointRadius(1);
                      line.setHasLabelsOnlyForSelected(false); //�닃���쓣 �븣, �씪踰� �몴�떆
                  }
                  lines.add(line);
                  if (i == 0) {
                      //誘몃━蹂닿린 �뜲�씠�꽣�뿉�뒗 �떖�옣諛뺣룞�닔 �씪�씤留� �꽔湲�!
                      linesForPreData.add(line);
                  }
              }

              data = new LineChartData(lines); // 理쒖큹�뿉 肉뚮젮吏� data chart
              preData = new LineChartData(linesForPreData); // 誘몃━蹂닿린 chart

              // X異뺤� 臾댁“嫄� �꽕�젙
              Axis axisX = new Axis()
                      .setHasLines(true)
                      .setLineColor(ChartUtils.COLOR_RED).setTextColor(getResources().getColor(R.color.dark_black))
                      .setValues(axisXvalue);
              data.setAxisXBottom(axisX);
              // Y 異뺤쓣 媛뽮퀬 �떢�쓣 �븣
              if (hasYaxis) {

                  Axis axisY = new Axis()
                          .setHasLines(true)
                          .setHasTiltedLabels(true)  // 湲��옄 湲곗슱�엫
                          .setName("�떖諛뺤닔");
                  data.setAxisYLeft(axisY);

                  // Y 異뺤씠 �븘�슂 �뾾�쓣 �븣
              } else {
                  data.setAxisYLeft(null);
              }
                  data.setBaseValue(Float.NEGATIVE_INFINITY);

                  chart.setLineChartData(data);
                  chart.setZoomEnabled(false);
                  chart.setScrollEnabled(false);

                  // 誘몃━ 蹂닿린 �꽕�젙
                  previewData = new LineChartData(preData);
                  previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                  previewChart.setLineChartData(previewData);
                  previewChart.setViewportChangeListener(new ViewportListener());
                  previewChart.setZoomType(ZoomType.HORIZONTAL); // X異� 諛⑺뼢�쑝濡쒕쭔 ��吏곸엫

                  maxViewport();
                  currentViewport();

          }
          // 理쒕� Viewport 媛� 吏��젙
          private void maxViewport(){
              
              maxViewport.top = 120;
              maxViewport.bottom = 45;
              maxViewport.left=0;
              maxViewport.right = audioSize/1000;
              chart.setMaximumViewport(maxViewport);
              previewChart.setMaximumViewport(maxViewport);
              
          }
          // �쁽�옱 蹂댁뿬吏� Viewport 媛� 吏��젙
          private void currentViewport(){

              currentViewport.left=0;
              currentViewport.bottom=45;
              currentViewport.top=120;
              currentViewport.right = maxViewport.width() / 3;
              previewChart.setCurrentViewportWithAnimation(currentViewport);
              chart.setCurrentViewportWithAnimation(currentViewport);
          }
          
          
          // Y異� �뾾�븷�뒗 option
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

        mDialogHelper.showPdialog("�옞�떆留� 湲곕떎�젮二쇱꽭�슂...", true);
        
        StringRequest strReq = new StringRequest(Method.POST, NetworkConfig.URL_FETCH_GRAPH,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        
                        mDialogHelper.hidePdialog();
                        
                        // String response -> JSON Object -> JSON Array 異붿텧 -> 媛쒕퀎 �빆紐� parsing
                        try {

                            
                            
                            /* 
                             *                       < JSON Object >
                             *           {
                             * 
                             *               "time":"{"right":[8113,11380,13672],"left":[17250,19736,21428]}", 
                             *               "hbr":"[73,76]"
                             *   
                             *           }
                             *           
                             *               ERROR : Unterminated object at character 
                             *                        "right" -> 'right' , "left" -> 'left'
                             * 
                             * */
                            Log.d("PARSING", response);
                            response = response.replaceAll("\"right\"","\'right\'").replaceAll("\"left\"", "\'left\'");
                            Log.e("CHANGED_RESPONSE", response);
                                JSONObject jObj = new JSONObject(response);
                                JSONObject time = new JSONObject(jObj.getString("time"));
                                JSONArray timeRight = (JSONArray)time.get("right");
                                JSONArray timeLeft = (JSONArray)time.get("left");

                            // HeartRate媛� �븘�삁 痢≪젙�씠 �븞�맂 寃쎌슦, undefined �삁�쇅 泥섎━
                            JSONArray hbr ;
                            if(jObj.getString("hbr").equals("undefined")){
                                hbr = new JSONArray("[60]");
                            }else{
                                hbr = new JSONArray(jObj.getString("hbr"));
                            }
                                ArrayList<Float> heartRateList= new ArrayList<>();
                                ArrayList<Float> rightEventTimeList= new ArrayList<>();
                                ArrayList<Float> leftEventTimeList= new ArrayList<>();

                            // Heart Rate媛� �꽆�뼱�삤吏� �븡�뒗 寃쎌슦 諛⑹�
                            if(hbr.length() != 0){
                                for(int i = 0; i<hbr.length(); i++){
                                    float heartRateValue = Float.parseFloat(hbr.get(i).toString());
                                    heartRateList.add(heartRateValue);
                                }
                            }else{
                                heartRateList.add((float)60);
                            }

                                
                                for(int i = 0; i<timeRight.length(); i++){
                                    float eventTimeValue = Float.parseFloat(timeRight.get(i).toString());
                                    rightEventTimeList.add(eventTimeValue);
                                 }
                                for(int i = 0; i<timeLeft.length(); i++){
                                    float eventTimeValue = Float.parseFloat(timeLeft.get(i).toString());
                                    leftEventTimeList.add(eventTimeValue);
                                 }

                                Log.d("HRLIST", heartRateList.toString());
                                Log.d("RIGHTLIST", rightEventTimeList.toString());
                                Log.d("LEFTLIST", leftEventTimeList.toString());
                                
                                
                             // Set Fragment
                                getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.chartContainer, new PlaceHolderFragment(heartRateList, rightEventTimeList,leftEventTimeList))
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
                        finish();
                        Toast.makeText(ResultActivity.this,"�꽕�듃�썙�겕 �뿰寃곗쓣 �솗�씤�븯�꽭�슂", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url ( �빐�떦 id && �빐�떦 title�씤 row )
                Map<String, String> params = new HashMap<String, String>();
                params.put("yourId", yourId);
                params.put("title", title);
                params.put("date", date);
                
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

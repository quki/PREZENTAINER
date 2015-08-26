package lecho.lib.hellocharts.samples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
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
import lecho.lib.hellocharts.samples.network.AppConfig;
import lecho.lib.hellocharts.samples.network.AppController;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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


public class LineChartActivity extends AppCompatActivity {
	
    private DialogHelper mDialogHelper;
    private static final String TAG = LineChartActivity.class.getSimpleName();
	
    
    
    private static final int SEND_THREAD_INFOMATION = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
       
        mDialogHelper = new DialogHelper(this);
        
        fetchDataByVolley();
        
    }

    /**
     * A fragment containing a line chart.
     */
    public class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private LineChartData data;
        private LineChartData pre_data;
        private PreviewLineChartView previewChart;
        private LineChartData previewData;
        Button buttonPlay;
        Button buttonStop;
        SeekBar seekbar;
        MediaPlayer audio;
        TextView textViewTime;
        TextView textViewHR;
        String meanHeartRate = null;
        public TimeHandler timeHandler = new TimeHandler(this);
        
        private int numberOfLines = 1;
        private int maxNumberOfLines = 4;
        private int numberOfPoints = 20;
        private int audio_time;
        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    
        private ArrayList<Float> heartRateList;
        private ArrayList<Float> eventTimeList; 
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;

        public PlaceholderFragment() {
        }
        public PlaceholderFragment(ArrayList<Float> heartRateList ,ArrayList<Float> eventTimeList) {
            this.heartRateList = heartRateList;
            this.eventTimeList = eventTimeList;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_preview_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);
            chart.setOnValueTouchListener(new ValueTouchListener());

            // Generate some randome values.
           // generateValues();

            generateData();

            // Disable viewpirt recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);

            // 설정
           // resetViewport();
            
            
            ///////////////////////////////////////////SeekBar///////////////////////////////////////////////////////////
            Uri audioPath = Uri.parse("/sdcard/melon/스폰서.mp3");
            audio = MediaPlayer.create(getApplicationContext(), audioPath);
            
            audio.setLooping(true);
            
            buttonPlay = (Button) findViewById(R.id.buttonPlay);
            buttonStop = (Button) findViewById(R.id.buttonStop);
            seekbar = (SeekBar) findViewById(R.id.seekBar1);
            audio_time=audio.getDuration()/1000;
            
            textViewTime = (TextView) findViewById(R.id.textViewTime);
            textViewHR = (TextView) findViewById(R.id.textViewHR);
            /**
             * seekbar의 최댓값을 음악의 최대길이, 즉 music.getDuration()의 값을 얻어와 지정합니다
             */
            seekbar.setMax(audio.getDuration());
            
            /**
             * 시크바를 움직였을떄 음악 재생 위치도 변할수 있도록 지정합니다
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
                   * 세번째로 넘어오는 boolean fromUser의 경우 true일때는 사용자가 직접 움직인경우,
                   * false인경우에는 소스상, 어플상에서 움직인경우이며
                   * 여기서는 사용자가 직접 움직인 경우에만 작동하도록 if문을 만들었다
                   * 
                   * 참고 : if문등 { } 괄호 안의 줄이 한줄일경우 생략이 가능합니다
                   */
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
            ///////////////////////////////////////////SeekBar///////////////////////////////////////////////////////////
            
            
            
            
            return rootView;
        }
        
        @Override
        public void onDestroy() {
            audio.stop();
            finish();
            super.onDestroy();
        }
        public void buttonPlay(){
            /**
             * music.isPlaying()�씠 true : �쓬�븙�씠 �쁽�옱 �옱�깮以묒엯�땲�떎, false : �옱�깮以묒씠 �븘�떃�땲�떎
             */
            // �쓬�븙�쓣 �떎�뻾�빀�땲�떎
            if(audio.isPlaying()) {
               //硫덉땄
               audio.pause();
               buttonPlay.setText("play");
            }
            else {
               //�옱�깮
               audio.start();
               buttonPlay.setText("pause");
            }         
            /**
            * �벐�옒�뱶瑜� �룎�젮 1珥덈쭏�떎 SeekBar瑜� ��吏곸씠寃� �빀�땲�떎
            */
            Thread();
         }
         
         public void buttonStop(){
            //buttonStop �옱�깮�쓣 �셿�쟾�엳 硫덉땄

            audio.stop();
            try {
               // �쓬�븙�쓣 �옱�깮�븷寃쎌슦瑜� ��鍮꾪빐 以�鍮꾪빀�땲�떎
               // prepare()�� �삁�쇅媛� 2媛�吏��굹 �븘�슂�빀�땲�떎
               audio.prepare();
            } catch (IllegalStateException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            // �쓬�븙 吏꾪뻾 �젙�룄瑜� 0, 利� 泥섏쓬�쑝濡� �릺�룎由쎈땲�떎
            audio.seekTo(0);

            // 踰꾪듉�쓽 湲��옄瑜� �떆�옉�쑝濡�, �떆�겕諛붾�� 泥섏쓬�쑝濡� �릺�룎由쎈땲�떎
            seekbar.setProgress(0);
            buttonPlay.setText("play");

         }
         
         public void Thread(){Runnable task = new Runnable() {
             public void run() {

                 while (audio.isPlaying()) {
                     try {
                         Thread.sleep(1000);
                     } catch (InterruptedException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                     seekbar.setProgress(audio.getCurrentPosition());
                     Log.e("audio.getCurrentPosition()",
                             ":" + audio.getCurrentPosition());
                     // textViewTime.setText(audio.getCurrentPosition()); 占쎈퓠占쎌쑎獄쏆뮇源�

                     // 占쎈툡占쎌뒄占쎈씨占쎈뮉 �겫占썽겫占�(audio.getCurrentPosition()占쎌뱽 占쎌뵠占쎌뒠占쎈퉸 獄쏅뗀以� 雅뚯눊�ц쳸�룇釉섓옙猷� 占쎈쭆占쎈뼄. 占쎈퉾占쎈굶筌랃옙
                     // 占쎌굙占쎈뻻�몴占� 占쎌맄占쎈퉸 占쎈쑅占쎈꼦占쎌벉)

                     // 筌롫뗄�뻻筌욑옙 占쎈섯占쎈선占쎌궎疫뀐옙
                     Message msg = timeHandler.obtainMessage();

                     // 筌롫뗄�뻻筌욑옙 ID 占쎄퐬占쎌젟
                     msg.what = SEND_THREAD_INFOMATION;

                     // 筌롫뗄�뻻筌욑옙 占쎌젟癰귨옙 占쎄퐬占쎌젟 (int 占쎌굨占쎈뻼)
                     msg.arg1 = Integer.valueOf(audio.getCurrentPosition());
                     // 占쎈퉾占쎈굶占쎌쑎嚥∽옙 筌롫뗄苑�筌욑옙 占쎌읈占쎈꽊
                     timeHandler.sendMessage(msg);

                 }
             }
         };
         Thread thread = new Thread(task);
         thread.start();
         }
         public class TimeHandler extends Handler {
              private final WeakReference<PlaceholderFragment> mActivity;
             
              public TimeHandler(PlaceholderFragment activity) {
                  mActivity = new WeakReference<PlaceholderFragment>(activity);
              }

              @Override
              public void handleMessage(Message msg) {
                  String stringTime = null;
                  String stringHR = null;
                  String stringWholeTime = null;
                  PlaceholderFragment activity = mActivity.get();
                  // 占쎈툡占쎌뒄占쎈씨占쎈뮉 �겫占썽겫占�(textViewTime.setText(audio.getCurrentPosition());占쎌뱽 獄쏅뗀以� 占쎈쑅占쎈즲
                  // 揶쏉옙占쎈뮟, 占쎈퉾占쎈굶筌랃옙 占쎌굙占쎈뻻�몴占� 占쎌맄占쎈퉸 占쎈쑅占쎈꼦占쎌벉)
                  super.handleMessage(msg);

                  switch (msg.what) {
                  case SEND_THREAD_INFOMATION:
                      //雅뚯눘�벥 setText占쎈뮉 獄쏆꼶諭띰옙�뻻 string占쎌몵嚥∽옙 獄쏅떽��占쎈선占쎄퐣 占쎌뵥占쎌쁽嚥∽옙 占쎌읈占쎈뼎 占쎈퉸占쎈튊占쎈립占쎈뼄.(int嚥∽옙 占쎈릭筌롳옙 error)
                      //占쎈뻻揶쏄쑬而�饔낅뗄苑� 占쎈뮞占쎈뱜筌띻낯�몵嚥∽옙 獄쏅떽�벊雅뚯눖�뮉 占쎈맙占쎈땾
                      stringTime = ChangeTime(msg.arg1);
                      //占쎌겱占쎌삺 占쎌삺占쎄문占쎈뻻揶쏄쑴肉됵옙苑� 揶쏉옙占쎌삢 揶쏉옙繹먮슣�뒲 占쎈뼎占쎌삢獄쏅베猷욑옙�땾�몴占� 筌≪뼚釉섆틠�눖�뮉 占쎈맙占쎈땾
                      stringHR = FindHeartRateValue(heartRateList, msg.arg1);
                      stringWholeTime = ChangeTime(audio.getDuration());
                      activity.textViewHR.setText("   "+stringHR + " / " + FindMeanHeartRateValue(heartRateList));
                      activity.textViewTime.setText("       "+stringTime + " / " + stringWholeTime);
                      
                      break;
                  default:
                      break;
                  }

              }
              
          }
          
          //占쎈뼎占쎌삢獄쏅베猷욑옙�땾 占쎈즸域뱀쥒而� �뤃�뗫릭占쎈뮉 占쎈맙占쎈땾
          public String FindMeanHeartRateValue(ArrayList<Float> heartRateList) {
              double heartRateSum = 0;
              String meanHeartRate = null;
              for (int i = 0; i < heartRateList.size(); i++) {
                  heartRateSum += heartRateList.get(i); 
              }
              meanHeartRate = Integer.toString((int)((double)(heartRateSum / heartRateList.size())));
              return meanHeartRate;
          }
          
          //占쎌겱占쎌삺 占쎌삺占쎄문占쎈뻻揶쏄쑴肉됵옙苑� 揶쏉옙占쎌삢 揶쏉옙繹먮슣�뒲 占쎈뼎占쎌삢獄쏅베猷욑옙�땾�몴占� 筌≪뼚釉섆틠�눖�뮉 占쎈맙占쎈땾
          public String FindHeartRateValue(ArrayList<Float> heartRateList, int time) {
              int HeartRateValueToInt = 0;
              String HeartRateValueToString = null;
              
              if (time / (1000 * 5) > heartRateList.size() - 1) {
                  HeartRateValueToInt = (int)((double)heartRateList.get(heartRateList.size() - 1));
                  HeartRateValueToString = Integer.toString(HeartRateValueToInt);
                  return HeartRateValueToString;
              }
              else {
                  HeartRateValueToInt = (int)((double)heartRateList.get(time / (1000 * 5)));
                  HeartRateValueToString = Integer.toString(HeartRateValueToInt);
              }
              
              return HeartRateValueToString; 
          }
          
          //獄쏉옙�뵳�딄쉭�뚢뫀諭띄몴占� 癰귣떯由� 占쎈젶占쎈릭野껓옙 獄쏅떽�벊雅뚯눖�뮉 占쎈맙占쎈땾.
          public String ChangeTime (int time) {
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

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                reset();
                generateData();
                return true;
            }
            if (id == R.id.action_add_line) {
                addLineToData();
                return true;
            }
            if (id == R.id.action_toggle_lines) {
                toggleLines();
                return true;
            }
            if (id == R.id.action_toggle_points) {
                togglePoints();
                return true;
            }
            if (id == R.id.action_toggle_cubic) {
                toggleCubic();
                return true;
            }
            if (id == R.id.action_toggle_area) {
                toggleFilled();
                return true;
            }
            if (id == R.id.action_point_color) {
                togglePointColor();
                return true;
            }
            if (id == R.id.action_shape_circles) {
                setCircles();
                return true;
            }
            if (id == R.id.action_shape_square) {
                setSquares();
                return true;
            }
            if (id == R.id.action_shape_diamond) {
                setDiamonds();
                return true;
            }
            if (id == R.id.action_toggle_labels) {
                toggleLabels();
                return true;
            }
            if (id == R.id.action_toggle_axes) {
                toggleAxes();
                return true;
            }
            if (id == R.id.action_toggle_axes_names) {
                toggleAxesNames();
                return true;
            }
            if (id == R.id.action_animate) {
                prepareDataAnimation();
                chart.startDataAnimation();
                return true;
            }
            if (id == R.id.action_toggle_selection_mode) {
                toggleLabelForSelected();

                Toast.makeText(getActivity(),
                        "Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_toggle_touch_zoom) {
                chart.setZoomEnabled(!chart.isZoomEnabled());
                Toast.makeText(getActivity(), "IsZoomEnabled " + chart.isZoomEnabled(), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_zoom_both) {
                chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_zoom_horizontal) {
                chart.setZoomType(ZoomType.HORIZONTAL);
                return true;
            }
            if (id == R.id.action_zoom_vertical) {
                chart.setZoomType(ZoomType.VERTICAL);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateValues() {
            for (int i = 0; i < maxNumberOfLines; ++i) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    randomNumbersTab[i][j] = (float) Math.random() * 100f;
                }
            }
        }

        private void reset() {
            numberOfLines = 1;

            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = true;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;

            chart.setValueSelectionEnabled(hasLabelForSelected);
            //resetViewport();
        }

        private void resetViewport() {
            
            
            // Reset viewport height range to Y : (0,200), X : ( 0, 배열갯수 )
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 120;
            v.left = 0;
            v.right = audio_time;
            v.offset(5, 5);
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
            
        }

        
        private void generateData() {

            List<Line> lines = new ArrayList<Line>();
            List<String> slideNum = new ArrayList<String>();
            // 축 값 설정
            List<AxisValue> axisXvalue = new ArrayList<AxisValue>();
            for (int j = 0; j < eventTimeList.size(); ++j) {
                slideNum.add(j+1+"번");
                axisXvalue.add(new AxisValue(eventTimeList.get(j)/1000).setLabel(slideNum.get(j)));
            }
            
            for (int i = 0; i < numberOfLines; ++i) {

            	List<PointValue> values = new ArrayList<PointValue>();
            	
            	    for (int j = 0; j < heartRateList.size(); ++j) {
                        values.add(new PointValue(j*5, heartRateList.get(j)));
                    }
            	
            	Log.e("!!!!!!!!!!!", values.toString());
            	Line line = new Line(values);
            	line.setColor(ChartUtils.COLORS[i]);
            	line.setShape(shape);
            	line.setCubic(isCubic);
            	line.setFilled(isFilled);
            	line.setHasLabels(hasLabels);
            	line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            	line.setHasLines(hasLines);
            	line.setHasPoints(hasPoints);
            	if (pointsHaveDifferentColor){
            		line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            	}
            	lines.add(line);
            }

            data = new LineChartData(lines);
            pre_data = new LineChartData(lines);
            // 축이 있을 때
            if (hasAxes) {
                Axis axisX = new Axis().setHasLines(true);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setLineColor(ChartUtils.COLOR_RED);
                    axisX.setValues(axisXvalue);
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
                
            // 없을 때
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);
            
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);
            
            previewData = new LineChartData(pre_data);
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
            

            previewChart.setLineChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);
            //previewXY();
        }
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart.
                chart.setCurrentViewport(newViewport);
            }

        }
        private void previewY() {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dy = tempViewport.height() / 4;
            tempViewport.inset(0, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
            previewChart.setZoomType(ZoomType.VERTICAL);
        }

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            if (animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            } else {
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        private void previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            // Make temp viewport smaller.
            float dx = tempViewport.width() / 4;
            float dy = tempViewport.height() / 4;
            tempViewport.inset(dx, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        }

        /**
         * Adds lines to data, after that data should be set again with
         * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
         */
        private void addLineToData() {
            if (data.getLines().size() >= maxNumberOfLines) {
                Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                ++numberOfLines;
            }

            generateData();
        }

        private void toggleLines() {
            hasLines = !hasLines;

            generateData();
        }

        private void togglePoints() {
            hasPoints = !hasPoints;

            generateData();
        }

        private void toggleCubic() {
            isCubic = !isCubic;

            generateData();

            if (isCubic) {
                // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
                // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
                // parameter or just set top and bottom values manually.
                // In this example I know that Y values are within (0,100) range so I set viewport height range manually
                // to (-5, 105).
                // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
                // modifying viewport.
                // Remember to set viewport after you call setLineChartData().
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = -5;
                v.top = 105;
                // You have to set max and current viewports separately.
                chart.setMaximumViewport(v);
                // I changing current viewport with animation in this case.
                chart.setCurrentViewportWithAnimation(v);
            } else {
                // If not cubic restore viewport to (0,100) range.
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = 0;
                v.top = 100;

                // You have to set max and current viewports separately.
                // In this case, if I want animation I have to set current viewport first and use animation listener.
                // Max viewport will be set in onAnimationFinished method.
                chart.setViewportAnimationListener(new ChartAnimationListener() {

                    @Override
                    public void onAnimationStarted() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationFinished() {
                        // Set max viewpirt and remove listener.
                        chart.setMaximumViewport(v);
                        chart.setViewportAnimationListener(null);

                    }
                });
                // Set current viewpirt with animation;
                chart.setCurrentViewportWithAnimation(v);
            }

        }

        private void toggleFilled() {
            isFilled = !isFilled;

            generateData();
        }

        private void togglePointColor() {
            pointsHaveDifferentColor = !pointsHaveDifferentColor;

            generateData();
        }

        private void setCircles() {
            shape = ValueShape.CIRCLE;

            generateData();
        }

        private void setSquares() {
            shape = ValueShape.SQUARE;

            generateData();
        }

        private void setDiamonds() {
            shape = ValueShape.DIAMOND;

            generateData();
        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
            }

            generateData();
        }

        private void toggleAxes() {
            hasAxes = !hasAxes;
            generateData();
        }

        private void toggleAxesNames() {
            hasAxesNames = !hasAxesNames;
            generateData();
        }
        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
         * {@link LineChartView#setLineChartData(LineChartData)} again.
         */
        private void prepareDataAnimation() {
            for (Line line : data.getLines()) {
                for (PointValue value : line.getValues()) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.getX(), (float) Math.random() * 100);
                }
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
    					float Y_axisHeartRate = Double.valueOf(hbr.get(i).toString()).floatValue();
    					heartRateList.add(Y_axisHeartRate);
    				}

    				for(int i = 0; i<time.length(); i++){
    					float X_axisEventTime = Double.valueOf(time.get(i).toString()).floatValue();
    					eventTimeList.add(X_axisEventTime);
    					
    				}
    				Log.e("+++++", heartRateList.toString());
    				Log.e("+++++", eventTimeList.toString());
    				
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
    			params.put("yourId", "quki");
    			params.put("title", "차트샘플데이터");
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

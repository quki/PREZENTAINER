
package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puregodic.android.prezentainer.connecthelper.BluetoothHelper;
import com.puregodic.android.prezentainer.connecthelper.ConnecToPcHelper;
import com.puregodic.android.prezentainer.connecthelper.ConnectionActionPc;
import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.ConnectionActionGear;

public class SettingActivity extends AppCompatActivity implements BluetoothHelper{

    private AccessoryService mAccessoryService = null;
    private Boolean isBound = false;
    private Boolean isGearConnected = false;
    private Boolean isPcConnected = false;
    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_DETAIL = 3;
    private static final String TAG = "==SETTING ACTIVITY==";

    private Button connectToGearBtn,connectToPcBtn,startBtn;
    private CheckBox timerCheckBox;
    private RadioGroup timerRadioGroup;
    private EditText ptTitleEditText;
    private LinearLayout rootView;
    
    //private static final  int PDIALOG_TIMEOUT_ID = 444;

    private ProgressDialog pDialog;
    
   // private final IncomingHandler mHandler = new IncomingHandler(this);
    private String mDeviceName ;
    
    // 수정 - 타이머 설정값 저장하는 배열
    private ArrayList<String> timeInterval;
    // ArrayList To JSON
    private Gson gson = new Gson();
    private TextView txtsendJson;
    String gsonString;
    
    private String yourId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        Intent intent = getIntent();
        yourId = intent.getStringExtra("yourId");
        Toast.makeText(getApplicationContext(), yourId, Toast.LENGTH_SHORT).show();
       
        // Bind Service
        doBindService();
        
        startBtn = (Button)findViewById(R.id.startBtn);
        timerCheckBox = (CheckBox)findViewById(R.id.timerCheckBox);
        timerRadioGroup = (RadioGroup)findViewById(R.id.timerRadioGroup);
        connectToGearBtn = (Button)findViewById(R.id.connectToGearBtn);
        connectToPcBtn = (Button)findViewById(R.id.connectToPcBtn);
        txtsendJson = (TextView)findViewById(R.id.txtsendJson);
        connectToGearBtn = (Button)findViewById(R.id.connectToGearBtn);
        ptTitleEditText = (EditText)findViewById(R.id.ptTitleEditText);
        rootView = (LinearLayout)findViewById(R.id.settingActivityView);
        
        // 공백을 클릭시 EditText의 focus와 자판이 사라지게 하기
        rootView.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ptTitleEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        
        startBtn.setEnabled(false);
        
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("기다려주세요...");
        pDialog.setCancelable(true);
        //mHandler.sendEmptyMessageDelayed(PDIALOG_TIMEOUT_ID, 5000);
        

        // 수정 - 타이머설정 라디오박스 보이게 하기
        timerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timerRadioGroup.setVisibility(timerRadioGroup.VISIBLE);
                } else {
                    // 체크박스를 해제한 경우 timeInterval을 null로
                    timerRadioGroup.setVisibility(timerRadioGroup.INVISIBLE);
                    timerRadioGroup.clearCheck();
                    if(timeInterval != null)
                    timeInterval = null;
                    Toast.makeText(getApplicationContext(), ""+timeInterval, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 수정 - 라디오그룹에서 선택된 값 ArrayList(timeInterval)에 넣기
        timerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                
                // 5분마다
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio5) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("5");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 10분마다
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio10) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("10");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 15분마다
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio15) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("15");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 개인 설정
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadioDetail) {
                    //DetailSettingActivity( Sub Activity )로 부터 ArrayList( timeInterval ) 요청
                    Intent intent = new Intent (SettingActivity.this, DetailSettingActivity.class);
                    startActivityForResult(intent, REQUEST_DETAIL);
                }

            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        isEnabledAdapter();
        super.onPostCreate(savedInstanceState);
    }
    
    @Override
    protected void onRestart() {
        
        if(mDeviceName != null){
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    // 해당 Device(PC) 이름으로 연결
                    ConnecToPcHelper mConnecToPcHelper = new ConnecToPcHelper();
                    mConnecToPcHelper.registerConnectionAction(getConnectionActionPc());
                    mConnecToPcHelper.connectWithPc(mDeviceName);
                }
            }).start();
        }
        
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        if(mAccessoryService != null)
        mAccessoryService.closeConnection();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_ENABLE_BT) {

            // 블루투스 연결 허락을 사용자에게 물어본 이후 동작
            if (resultCode == RESULT_OK) {
                Toast.makeText(SettingActivity.this, "블루투스를 켰습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, "블루투스를 꼭 켜주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DEVICENAME) {
            mDeviceName = intent.getStringExtra("deviceName");
            Toast.makeText(getApplicationContext(), mDeviceName, Toast.LENGTH_SHORT).show();

        } else if (requestCode == REQUEST_DETAIL) {
            
            // DetailSettingActivity로 부터 가져온 ArrayList를 timeInterval에 저장
            timeInterval = intent.getStringArrayListExtra("timeInterval");
            
            // 삭제 요망
            gsonString = gson.toJson(timeInterval);
            txtsendJson.setText(gsonString);

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        
        switch (v.getId()) {
            
            // 기어와 연결
            case R.id.connectToGearBtn: {
                startConnection();
                break;
            }
            
            // PC와 연결 (device name 설정 화면으로 이동)
            case R.id.connectToPcBtn: {
                
                // device name 요청
                Intent requestDeviceNameIntent = new Intent(SettingActivity.this, SettingBluetoothActivity.class);
                startActivityForResult(requestDeviceNameIntent, REQUEST_DEVICENAME);
                break;
                
            }
            
            // 기어 어플리케이션에 설정값 전달(알람 시간 간격, 페어링된 PC이름) 및 시작
            case R.id.startBtn: {
               final String mPtTitle =   ptTitleEditText.getText().toString().trim();
                
                // 프레젠테이션 제목을 기입한 경우
                if(!TextUtils.isEmpty(mPtTitle)){
                    
                    // AlertDialog
                    AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                            SettingActivity.this);
                    mAlertBuilder.setTitle(mPtTitle)
                            .setMessage( "발표를 시작하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("시작하기", new DialogInterface.OnClickListener() {
                                // 시작하기 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if(mDeviceName != null && mAccessoryService != null){
                                        
                                        
                                        // Service에 device name, ppt tittle, 계정정보 넘기기
                                        mAccessoryService.mDeviceName = mDeviceName;
                                        mAccessoryService.mPtTitle = mPtTitle;
                                        mAccessoryService.yourId = yourId;
                                        
                                        if (timeInterval != null) {
                                            // timeInterval(ArrayList) -> JSONArray -> String ex) "["2","3","5"]"
                                            String timeJson = gson.toJson(timeInterval);
                                            sendDataToService(timeJson);
                                        }else{
                                            // 체크박스를 단 한번도 누르지 않은 경우, 눌렀다가 해제한 경우 "[]"을 전달
                                            sendDataToService("[]");
                                        }
                                        
                                        // Start Activity로 나의 id와 PT 제목을 넘겨준다.
                                        startActivity(new Intent(SettingActivity.this, FileTransferRequestedActivity.class)
                                        .putExtra("yourId", yourId)
                                        .putExtra("title", mPtTitle));
                                        
                                        mDeviceName = null;
                                        
                                    }else{
                                        
                                        finish();
                                        Toast.makeText(SettingActivity.this, "설정을 다시 진행해주세요",Toast.LENGTH_SHORT).show();
                                    }
                                    
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                // 취소 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = mAlertBuilder.create();

                    dialog.show();
                 
                 // 프레젠테이션 제목을 기입하지 않은 경우
                }else{
                        Toast.makeText(SettingActivity.this, "프레젠테이션 제목을 반드시 기입하세요", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    
    // Service와 Activity를 bind
    private void doBindService() {
        Intent intent = new Intent(SettingActivity.this, AccessoryService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    // AccessoryService으로 conncetion 시작
    private void startConnection() {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.findPeers();
        }
    }
    // AccessoryService으로 알람시간간격 전달
    private void sendDataToService(String mData) {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.sendDataToGear(mData);
        } else {
            Toast.makeText(getApplicationContext(), "기어와 연결을 확인하세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 블루투스 승인 요청
    @Override
    public void isEnabledAdapter() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    
    // AccessoryService와의 인터페이스 메소드 정의하기
    private ConnectionActionGear getConnectionActionGear(){
        return new ConnectionActionGear() {

            
            // PeerAgent 찾는 중일 때
            @Override
            public void onFindingPeerAgent() {
                 runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToGearBtn.setText("기어를 찾는 중 입니다");
                        setEnabledStartBtn();
                    }
                });
                
            }
            
            // PeerAgent 못 찾았을  때
            @Override
            public void onFindingPeerAgentError() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {

                        hidepDialog();
                        connectToGearBtn.setText("기어와의 블루투스 연결을 확인하세요");
                        setEnabledStartBtn();
                        
                    }
                });
                
            }
           
            // Service Connection 요청 할 때
            @Override
            public void onConnectionActionRequest() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToGearBtn.setText("기어에 연결을 요청하였습니다");
                        setEnabledStartBtn();
                    }
                });
            }
            // Service Connection 완료 됬을 때
            @Override
            public void onConnectionActionComplete() {
                
                isGearConnected = true;
                
                runOnUiThread( new Runnable() {
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("기어와 연결되었습니다");
                        setEnabledStartBtn();
                    }
                });
                
            }
            // Service Connection 에러 났을 때
            @Override
            public void onConnectionActionError() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("기어 측 어플이 실행되었는지 확인하세요");
                        setEnabledStartBtn();
                    }
                });
            }
        };
    }
    
    // ConnecToPcHelper와의 인터페이스 메소드 정의하기
    private ConnectionActionPc getConnectionActionPc(){
        return new ConnectionActionPc() {
            
            @Override
            public void onConnectionActionRequest() {
                
                isPcConnected=false;

                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToPcBtn.setText("PC에 연결을 요청하였습니다");
                        setEnabledStartBtn();
                        
                    }
                });
            }
            
            @Override
            public void onConnectionActionComplete() {
                
                isPcConnected = true;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        hidepDialog();
                        connectToPcBtn.setText("PC와 연결되었습니다");
                        setEnabledStartBtn();
                    }
                });
                
            }

            @Override
            public void onConnectionActionError() {
                
                isPcConnected = false;
                
                 runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        hidepDialog();
                        connectToPcBtn.setText("PC 측 프로그램이 실행되었는지 확인하세요");
                        setEnabledStartBtn();
                    }
                });
                
            }
        };
    }
    
    private void showpDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }
    
    private void hidepDialog(){
        if(pDialog != null)
        pDialog.dismiss();
    }
    
    // 시작 버튼 활성화
    private void setEnabledStartBtn(){
        if(isGearConnected && isPcConnected){
            startBtn.setEnabled(true);
        }else
            startBtn.setEnabled(false);
    }
    

    // ServiceConnection Interface
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAccessoryService = ((AccessoryService.MyBinder)service).getService();
            mAccessoryService.registerConnectionAction(getConnectionActionGear());
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAccessoryService = null;
            isBound = false;
        }
    };
    
/*    //sub class 만들어서 메모리 누수를 막아줌
static class IncomingHandler extends Handler{
    private final WeakReference<SettingActivity> mActivity;
    IncomingHandler(SettingActivity activity) {
        mActivity = new WeakReference<SettingActivity>(activity);
    }
    
    @Override
    public void handleMessage(Message msg) {
        if(msg.what == PDIALOG_TIMEOUT_ID){
            SettingActivity activity = mActivity.get();
            activity.pDialog.dismiss();
        }
        super.handleMessage(msg);
        }
    }*/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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


package com.puregodic.android.prezentainer;


import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.puregodic.android.prezentainer.bluetooth.BluetoothHelper;
import com.puregodic.android.prezentainer.bluetooth.ConnecToPcHelper;
import com.puregodic.android.prezentainer.bluetooth.ConnectionActionPc;
import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.ConnectionActionGear;

import java.util.ArrayList;

public class SettingFragment extends Fragment implements BluetoothHelper{

    private static final int BUTTON_STATE_ERROR = -1;
    private static final int BUTTON_STATE_COMPLETE = 100;
    private static final int BUTTON_STATE_PROGRESS = 50;
    private static final int BUTTON_STATE_IDLE = 0;

    private AccessoryService mAccessoryService = null;
    private Boolean isBound = false;
    private Boolean isGearConnected = false;
    private Boolean isPcConnected = false;
    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_DETAIL = 3;
    private static final String TAG = "==SETTING ACTIVITY==";

    private Button startBtn;
    private CheckBox timerCheckBox;
    private RadioGroup timerRadioGroup;
    private EditText ptTitleEditText;

    //private static final  int PDIALOG_TIMEOUT_ID = 444;

    private CircularProgressButton connectToGearBtn;
    private CircularProgressButton connectToPcBtn;
    private TextView errorMessage;
    // private final IncomingHandler mHandler = new IncomingHandler(this);
    private String mDeviceName ;

    // 수정 - 타이머 설정값 저장하는 배열
    private ArrayList<String> timeInterval;
    // ArrayList To JSON
    private Gson gson = new Gson();
    private TextView txtsendJson;
    String gsonString;

    private String yourId;

    public SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isEnabledAdapter();
    }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_setting, container, false);


        Intent intent = getActivity().getIntent();
        yourId = intent.getStringExtra("yourId");

        // Bind Service
        doBindService();

        startBtn = (Button)rootView.findViewById(R.id.startBtn);
        timerCheckBox = (CheckBox)rootView.findViewById(R.id.timerCheckBox);
        timerRadioGroup = (RadioGroup)rootView.findViewById(R.id.timerRadioGroup);
        txtsendJson = (TextView)rootView.findViewById(R.id.txtsendJson);
        ptTitleEditText = (EditText)rootView.findViewById(R.id.ptTitleEditText);
        connectToGearBtn = (CircularProgressButton)rootView.findViewById(R.id.connectToGearBtn);
        connectToPcBtn = (CircularProgressButton)rootView.findViewById(R.id.connectToPcBtn);
        connectToGearBtn.setIndeterminateProgressMode(true); // progress mode On !
        connectToPcBtn.setIndeterminateProgressMode(true); // progress mode On !
        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_IDLE);
        setCircularProgressBtn(connectToPcBtn,BUTTON_STATE_IDLE);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        errorMessage = (TextView) rootView.findViewById(R.id.errorMessage);

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
        startBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final String mPtTitle =   ptTitleEditText.getText().toString().trim();

                // 프레젠테이션 제목을 기입한 경우
                if(!TextUtils.isEmpty(mPtTitle)){

                    // AlertDialog
                    AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                            getActivity());
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
                                        startActivity(new Intent(getActivity(), FileTransferRequestedActivity.class)
                                                .putExtra("yourId", yourId)
                                                .putExtra("title", mPtTitle));

                                        mDeviceName = null;

                                    }else{

                                        getActivity().finish();
                                        Toast.makeText(getActivity(), "설정을 다시 진행해주세요",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "프레젠테이션 제목을 반드시 기입하세요", Toast.LENGTH_SHORT).show();
                    ptTitleEditText.requestFocus();
                }
            }

        });

        connectToGearBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                if (connectToGearBtn.getProgress() == BUTTON_STATE_IDLE) {
                    setCircularProgressBtn(connectToGearBtn,BUTTON_STATE_PROGRESS);
                    startConnection();
                } else if (connectToGearBtn.getProgress() == BUTTON_STATE_COMPLETE) {
                    setCircularProgressBtn(connectToGearBtn,BUTTON_STATE_IDLE);
                }else if(connectToGearBtn.getProgress() == BUTTON_STATE_ERROR){
                    setCircularProgressBtn(connectToGearBtn,BUTTON_STATE_IDLE);
                } else {
                    setCircularProgressBtn(connectToGearBtn,BUTTON_STATE_IDLE);
                }
            }

        });

        connectToPcBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (connectToPcBtn.getProgress() == BUTTON_STATE_IDLE) {

                    // device name 요청
                    Intent requestDeviceNameIntent = new Intent(getActivity(), SettingBluetoothActivity.class);
                    startActivityForResult(requestDeviceNameIntent, REQUEST_DEVICENAME);

                } else if (connectToPcBtn.getProgress() == BUTTON_STATE_COMPLETE) {
                    setCircularProgressBtn(connectToPcBtn,BUTTON_STATE_IDLE);
                }else if(connectToPcBtn.getProgress() == BUTTON_STATE_ERROR){
                    setCircularProgressBtn(connectToPcBtn,BUTTON_STATE_IDLE);
                } else {
                    setCircularProgressBtn(connectToPcBtn, BUTTON_STATE_IDLE);
                }
            }

        });


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
                    if (timeInterval != null)
                        timeInterval = null;
                    Toast.makeText(getActivity().getApplicationContext(), "" + timeInterval, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(),
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 10분마다
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio10) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("10");
                    Toast.makeText(getActivity(),
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 15분마다
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio15) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("15");
                    Toast.makeText(getActivity(),
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 개인 설정
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadioDetail) {
                    //DetailSettingActivity( Sub Activity )로 부터 ArrayList( timeInterval ) 요청
                    Intent intent = new Intent(getActivity(), DetailSettingActivity.class);
                    startActivityForResult(intent, REQUEST_DETAIL);
                }

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PairingActivity.class));
            }
        });

        return rootView;

    }
    @Override
    public void onDestroy() {
        if(mAccessoryService != null)
            mAccessoryService.closeConnection();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_ENABLE_BT) {

            // 블루투스 연결 허락을 사용자에게 물어본 이후 동작
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(), "블루투스를 켰습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "블루투스를 꼭 켜주세요", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DEVICENAME) {
            mDeviceName = intent.getStringExtra("deviceName");

        } else if (requestCode == REQUEST_DETAIL) {

            // DetailSettingActivity로 부터 가져온 ArrayList를 timeInterval에 저장
            timeInterval = intent.getStringArrayListExtra("timeInterval");

            // 삭제 요망
            gsonString = gson.toJson(timeInterval);
            txtsendJson.setText(gsonString);

        }

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
        super.onActivityResult(requestCode, resultCode, intent);
    }


    // Service와 Activity를 bind
    private void doBindService() {
        Intent intent = new Intent(getActivity(), AccessoryService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
            Toast.makeText(getActivity().getApplicationContext(), "기어와 연결을 확인하세요", Toast.LENGTH_SHORT).show();
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
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_PROGRESS);
                        setEnabledStartBtn();
                    }
                });

            }

            // PeerAgent 못 찾았을  때
            @Override
            public void onFindingPeerAgentError() {

                isGearConnected =false;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_ERROR);
                        errorMessage.append("기어와의 블루투스 연결을 확인하세요");
                        setEnabledStartBtn();

                    }
                });

            }

            // Service Connection 요청 할 때
            @Override
            public void onConnectionActionRequest() {

                isGearConnected =false;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_PROGRESS);
                        setEnabledStartBtn();
                    }
                });
            }
            // Service Connection 완료 됬을 때
            @Override
            public void onConnectionActionComplete() {

                isGearConnected = true;

                getActivity().runOnUiThread( new Runnable() {
                    public void run() {
                        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_COMPLETE);
                        errorMessage.setText(null);
                        setEnabledStartBtn();
                    }
                });

            }
            // Service Connection 에러 났을 때
            @Override
            public void onConnectionActionError() {

                isGearConnected =false;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToGearBtn, BUTTON_STATE_ERROR);
                        errorMessage.append("기어 측 어플이 실행되었는지 확인하세요");
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

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToPcBtn, BUTTON_STATE_PROGRESS);
                        setEnabledStartBtn();

                    }
                });
            }

            @Override
            public void onConnectionActionComplete() {

                isPcConnected = true;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToPcBtn, BUTTON_STATE_COMPLETE);
                        errorMessage.setText(null);
                        setEnabledStartBtn();
                    }
                });

            }

            @Override
            public void onConnectionActionError() {

                isPcConnected = false;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setCircularProgressBtn(connectToPcBtn, BUTTON_STATE_ERROR);
                        errorMessage.append("PC 측 프로그램이 실행되었는지 확인하세요");
                        setEnabledStartBtn();
                    }
                });

            }
        };
    }

private void setCircularProgressBtn(final CircularProgressButton circularBtn, final int id){


    Handler mHandler = new Handler();
    circularBtn.setProgress(id);

    int height = getImageResource(circularBtn).getIntrinsicHeight();
    int width = getImageResource(circularBtn).getIntrinsicWidth();
    getImageResource(circularBtn).setBounds(0, 0, width, height);
    int padding_dp = 60;  // 6 dps
    final float scale = getResources().getDisplayMetrics().density;
    final int padding_px = (int) (padding_dp * scale + 0.5f);


    mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {

            // (start,top,end,bottom)
            circularBtn.setPaddingRelative(padding_px, 0, padding_px, 0);
            circularBtn.setCompoundDrawablesWithIntrinsicBounds(getImageResource(circularBtn), null, null, null);

            switch (id){

                case BUTTON_STATE_IDLE:{
                    if(circularBtn.equals(connectToGearBtn)){
                        circularBtn.setText("기어와 연결하기");
                    }else if(circularBtn.equals(connectToPcBtn)){
                        circularBtn.setText("PC와 연결하기");
                    }
                    break;
                }
                case BUTTON_STATE_ERROR:{
                    if(circularBtn.equals(connectToGearBtn)){
                        circularBtn.setText("연결실패");
                    }else if(circularBtn.equals(connectToPcBtn)){
                        circularBtn.setText("연결실패");
                    }
                    break;
                }
                case BUTTON_STATE_PROGRESS :{
                    circularBtn.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                    break;
                }
                case BUTTON_STATE_COMPLETE :{
                    if(circularBtn.equals(connectToGearBtn)){
                        circularBtn.setText("연결완료");
                    }else if(circularBtn.equals(connectToPcBtn)){
                        circularBtn.setText("연결완료");
                    }
                    break;
                }

            }

        }
    }, 500);
}

    private Drawable getImageResource(CircularProgressButton c) {

        Drawable icon = null;

        if (c == connectToGearBtn) {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_btn_watch);
        } else if (c == connectToPcBtn) {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_btn_laptop);
        }
        return icon;
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



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.setting, menu);
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
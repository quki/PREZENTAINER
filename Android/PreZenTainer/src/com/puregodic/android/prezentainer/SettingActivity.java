
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
    
    // ���� - Ÿ�̸� ������ �����ϴ� �迭
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
        
        // ������ Ŭ���� EditText�� focus�� ������ ������� �ϱ�
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
        pDialog.setMessage("��ٷ��ּ���...");
        pDialog.setCancelable(true);
        //mHandler.sendEmptyMessageDelayed(PDIALOG_TIMEOUT_ID, 5000);
        

        // ���� - Ÿ�̸Ӽ��� �����ڽ� ���̰� �ϱ�
        timerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timerRadioGroup.setVisibility(timerRadioGroup.VISIBLE);
                } else {
                    // üũ�ڽ��� ������ ��� timeInterval�� null��
                    timerRadioGroup.setVisibility(timerRadioGroup.INVISIBLE);
                    timerRadioGroup.clearCheck();
                    if(timeInterval != null)
                    timeInterval = null;
                    Toast.makeText(getApplicationContext(), ""+timeInterval, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ���� - �����׷쿡�� ���õ� �� ArrayList(timeInterval)�� �ֱ�
        timerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                
                // 5�и���
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio5) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("5");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 10�и���
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio10) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("10");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // 15�и���
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio15) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("15");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_SHORT)
                            .show();
                }
                // ���� ����
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadioDetail) {
                    //DetailSettingActivity( Sub Activity )�� ���� ArrayList( timeInterval ) ��û
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
                    // �ش� Device(PC) �̸����� ����
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

            // ������� ���� ����� ����ڿ��� ��� ���� ����
            if (resultCode == RESULT_OK) {
                Toast.makeText(SettingActivity.this, "��������� �׽��ϴ�", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, "��������� �� ���ּ���", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DEVICENAME) {
            mDeviceName = intent.getStringExtra("deviceName");
            Toast.makeText(getApplicationContext(), mDeviceName, Toast.LENGTH_SHORT).show();

        } else if (requestCode == REQUEST_DETAIL) {
            
            // DetailSettingActivity�� ���� ������ ArrayList�� timeInterval�� ����
            timeInterval = intent.getStringArrayListExtra("timeInterval");
            
            // ���� ���
            gsonString = gson.toJson(timeInterval);
            txtsendJson.setText(gsonString);

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        
        switch (v.getId()) {
            
            // ���� ����
            case R.id.connectToGearBtn: {
                startConnection();
                break;
            }
            
            // PC�� ���� (device name ���� ȭ������ �̵�)
            case R.id.connectToPcBtn: {
                
                // device name ��û
                Intent requestDeviceNameIntent = new Intent(SettingActivity.this, SettingBluetoothActivity.class);
                startActivityForResult(requestDeviceNameIntent, REQUEST_DEVICENAME);
                break;
                
            }
            
            // ��� ���ø����̼ǿ� ������ ����(�˶� �ð� ����, ���� PC�̸�) �� ����
            case R.id.startBtn: {
               final String mPtTitle =   ptTitleEditText.getText().toString().trim();
                
                // ���������̼� ������ ������ ���
                if(!TextUtils.isEmpty(mPtTitle)){
                    
                    // AlertDialog
                    AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                            SettingActivity.this);
                    mAlertBuilder.setTitle(mPtTitle)
                            .setMessage( "��ǥ�� �����Ͻðڽ��ϱ�?")
                            .setCancelable(false)
                            .setPositiveButton("�����ϱ�", new DialogInterface.OnClickListener() {
                                // �����ϱ� ��ư Ŭ���� ����
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if(mDeviceName != null && mAccessoryService != null){
                                        
                                        
                                        // Service�� device name, ppt tittle, �������� �ѱ��
                                        mAccessoryService.mDeviceName = mDeviceName;
                                        mAccessoryService.mPtTitle = mPtTitle;
                                        mAccessoryService.yourId = yourId;
                                        
                                        if (timeInterval != null) {
                                            // timeInterval(ArrayList) -> JSONArray -> String ex) "["2","3","5"]"
                                            String timeJson = gson.toJson(timeInterval);
                                            sendDataToService(timeJson);
                                        }else{
                                            // üũ�ڽ��� �� �ѹ��� ������ ���� ���, �����ٰ� ������ ��� "[]"�� ����
                                            sendDataToService("[]");
                                        }
                                        
                                        // Start Activity�� ���� id�� PT ������ �Ѱ��ش�.
                                        startActivity(new Intent(SettingActivity.this, FileTransferRequestedActivity.class)
                                        .putExtra("yourId", yourId)
                                        .putExtra("title", mPtTitle));
                                        
                                        mDeviceName = null;
                                        
                                    }else{
                                        
                                        finish();
                                        Toast.makeText(SettingActivity.this, "������ �ٽ� �������ּ���",Toast.LENGTH_SHORT).show();
                                    }
                                    
                                }
                            }).setNegativeButton("���", new DialogInterface.OnClickListener() {
                                // ��� ��ư Ŭ���� ����
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = mAlertBuilder.create();

                    dialog.show();
                 
                 // ���������̼� ������ �������� ���� ���
                }else{
                        Toast.makeText(SettingActivity.this, "���������̼� ������ �ݵ�� �����ϼ���", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    
    // Service�� Activity�� bind
    private void doBindService() {
        Intent intent = new Intent(SettingActivity.this, AccessoryService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    // AccessoryService���� conncetion ����
    private void startConnection() {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.findPeers();
        }
    }
    // AccessoryService���� �˶��ð����� ����
    private void sendDataToService(String mData) {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.sendDataToGear(mData);
        } else {
            Toast.makeText(getApplicationContext(), "���� ������ Ȯ���ϼ���", Toast.LENGTH_SHORT).show();
        }
    }

    // ������� ���� ��û
    @Override
    public void isEnabledAdapter() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    
    // AccessoryService���� �������̽� �޼ҵ� �����ϱ�
    private ConnectionActionGear getConnectionActionGear(){
        return new ConnectionActionGear() {

            
            // PeerAgent ã�� ���� ��
            @Override
            public void onFindingPeerAgent() {
                 runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToGearBtn.setText("�� ã�� �� �Դϴ�");
                        setEnabledStartBtn();
                    }
                });
                
            }
            
            // PeerAgent �� ã����  ��
            @Override
            public void onFindingPeerAgentError() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {

                        hidepDialog();
                        connectToGearBtn.setText("������ ������� ������ Ȯ���ϼ���");
                        setEnabledStartBtn();
                        
                    }
                });
                
            }
           
            // Service Connection ��û �� ��
            @Override
            public void onConnectionActionRequest() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToGearBtn.setText("�� ������ ��û�Ͽ����ϴ�");
                        setEnabledStartBtn();
                    }
                });
            }
            // Service Connection �Ϸ� ���� ��
            @Override
            public void onConnectionActionComplete() {
                
                isGearConnected = true;
                
                runOnUiThread( new Runnable() {
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("���� ����Ǿ����ϴ�");
                        setEnabledStartBtn();
                    }
                });
                
            }
            // Service Connection ���� ���� ��
            @Override
            public void onConnectionActionError() {
                
                isGearConnected =false;
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("��� �� ������ ����Ǿ����� Ȯ���ϼ���");
                        setEnabledStartBtn();
                    }
                });
            }
        };
    }
    
    // ConnecToPcHelper���� �������̽� �޼ҵ� �����ϱ�
    private ConnectionActionPc getConnectionActionPc(){
        return new ConnectionActionPc() {
            
            @Override
            public void onConnectionActionRequest() {
                
                isPcConnected=false;

                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToPcBtn.setText("PC�� ������ ��û�Ͽ����ϴ�");
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
                        connectToPcBtn.setText("PC�� ����Ǿ����ϴ�");
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
                        connectToPcBtn.setText("PC �� ���α׷��� ����Ǿ����� Ȯ���ϼ���");
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
    
    // ���� ��ư Ȱ��ȭ
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
    
/*    //sub class ���� �޸� ������ ������
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

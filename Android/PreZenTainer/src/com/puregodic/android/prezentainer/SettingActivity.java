
package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.puregodic.android.prezentainer.connectpchelper.ConnecToPcHelper;
import com.puregodic.android.prezentainer.service.AccessoryService;

public class SettingActivity extends AppCompatActivity {

    private AccessoryService mAccessoryService = null;

    private Boolean isBound = false;

    ConnecToPcHelper mConnecToPcHelper;

    // 수정 - 타이머 설정값 저장하는 배열
    private ArrayList<String> timeInterval ;

    Button startBtn;

    CheckBox timerCheckBox;

    RadioGroup timerRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // Bind Service
        doBindService();
        
        startBtn = (Button)findViewById(R.id.startBtn);
        timerCheckBox = (CheckBox)findViewById(R.id.timerCheckBox);
        // 수정 - 라디오그룹추가
        timerRadioGroup = (RadioGroup)findViewById(R.id.timerRadioGroup);
        // startBtn.setEnabled(false);

        // 수정 - 타이머설정 라디오박스 보이게 하기
        timerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    timerRadioGroup.setVisibility(timerRadioGroup.VISIBLE);
                } else {
                    timerRadioGroup.setVisibility(timerRadioGroup.INVISIBLE);
                    timerRadioGroup.clearCheck();
                    timeInterval = new ArrayList<String>();
                    timeInterval.add(0, "0");
                }
            }
        });

        // 수정 - 라디오그룹에서 선택된 값 ArrayList(timeInterval)에 넣기
        timerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio5) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("5");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio10) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("10");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio15) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("15");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("시간간격 : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

    }

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

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        switch (v.getId()) {
        // 시작하기
            case R.id.connectToGearBtn: {
                startConnection();
                break;
            }
            case R.id.connectToPcBtn: {

                mConnecToPcHelper = new ConnecToPcHelper();
                mConnecToPcHelper.enabledBluetoothAdapter();
                break;
            }
            case R.id.startBtn: {
                
                if(timeInterval != null){
                    sendDataToService(timeInterval.get(0));
                }else{
                    sendDataToService("0");
                }
                break;
            }
        }

    }

    private void doBindService() {

        Intent intent = new Intent(SettingActivity.this, AccessoryService.class); // Action
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void startConnection() {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.findPeers();
        }
    }

    private void sendDataToService(String mData) {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.sendDataToGear(mData);
        } else {
            Toast.makeText(getApplicationContext(), "기어와 연결을 확인하세요", Toast.LENGTH_SHORT).show();
        }
    }

    // ServiceConnection Interface
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAccessoryService = ((AccessoryService.MyBinder)service).getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAccessoryService = null;
            isBound = false;
        }
    };
}

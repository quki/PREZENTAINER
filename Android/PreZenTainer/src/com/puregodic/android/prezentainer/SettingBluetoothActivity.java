
package com.puregodic.android.prezentainer;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.connecthelper.BluetoothHelper;

public class SettingBluetoothActivity extends AppCompatActivity implements BluetoothHelper {

    private BluetoothAdapter mBluetoothAdapter;

    private BroadcastReceiver mBroadcastReceiver;

    private ListView listViewPaired,listViewFound;
    
    private Button btnSearch;

    private String mDeviceName;

    Intent returnDeviceNameIntent;
    
    ArrayList<String> foundDeviceArrayList = new ArrayList<String>();
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bluetooth);
        
        returnDeviceNameIntent = new Intent();
        returnDeviceNameIntent.putExtra("deviceName", mDeviceName);
        setResult(REQUEST_DEVICENAME, returnDeviceNameIntent);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listViewPaired = (ListView)findViewById(R.id.listViewPaired);
        listViewFound = (ListView)findViewById(R.id.listViewFound);
        
        btnSearch = (Button)findViewById(R.id.btnSearch);

        

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        
        
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBluetoothAdapter.startDiscovery();
                btnSearch.setEnabled(false);
            }
        });

        isEnabledAdapter();
        listPairedDevices();

        

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    
                    // 새로운 기기를 찾았을 때...
                    BluetoothDevice devicesFound = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listFoundDevices(devicesFound);

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                    btnSearch.setEnabled(true);
                    foundDeviceArrayList.clear();
                }
            }
        };
        
        // IntentFilter 이벤트를 모니터링
        // 새로운기기를 찾았을 때, 탐색을 끝냈을 때, 상태 변화 감지
        IntentFilter deviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter deviveDiscoveryFinishedFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 탐색을 끝냈을때
        IntentFilter stateChanged = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 리시버와 intentfilter를 등록한다. 이는 해당 이벤트를 BroadcastReceiver로 통보하도록 요구!
        registerReceiver(mBroadcastReceiver, deviceFoundFilter);
        registerReceiver(mBroadcastReceiver, deviveDiscoveryFinishedFilter);
        registerReceiver(mBroadcastReceiver, stateChanged);
        
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void isEnabledAdapter() {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, SettingActivity.REQUEST_ENABLE_BT);
        }
    }

    // List Devices paired
    private void listPairedDevices() {
        // pair된 기기들을 Set화 시킴
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<String> pairedDeviceArrayList = new ArrayList<String>();

        // pair된 device를 list
        for (BluetoothDevice pairedDevice : pairedDevices) {
            pairedDeviceArrayList.add(pairedDevice.getName());
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                R.layout.list_items_paired, pairedDeviceArrayList);
        listViewPaired.setAdapter(mAdapter);
        listViewPaired.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDeviceName = (String)parent.getItemAtPosition(position);

                // AlertDialog
                AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                        SettingBluetoothActivity.this);
                mAlertBuilder.setTitle(mDeviceName)
                        .setMessage("파워포인트가 실행 될 PC가 " + mDeviceName + " 이(가) 맞습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네, 맞습니다", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                returnDeviceNameIntent.putExtra("deviceName", mDeviceName);
                                setResult(REQUEST_DEVICENAME, returnDeviceNameIntent);
                                finish();
                            }
                        }).setNegativeButton("아닙니다", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = mAlertBuilder.create();

                dialog.show();

            }

        });

    }

    // List Devices found
    private void listFoundDevices(BluetoothDevice device) {
        
        foundDeviceArrayList.add(device.getName().toString());
        ArrayAdapter<String> mAdpat = new ArrayAdapter<String>(this, R.layout.list_items_found,foundDeviceArrayList);
        
        listViewFound.setAdapter(mAdpat);
        listViewFound.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String mmfoundDeviceName = (String)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), mmfoundDeviceName, Toast.LENGTH_SHORT).show();

            }

        });

    

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_bluetooth, menu);
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

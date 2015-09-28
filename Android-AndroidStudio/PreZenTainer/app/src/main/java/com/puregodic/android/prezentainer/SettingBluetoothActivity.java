
package com.puregodic.android.prezentainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.puregodic.android.prezentainer.adapter.PairedDeviceAdapter;
import com.puregodic.android.prezentainer.adapter.PairedDeviceData;
import com.puregodic.android.prezentainer.bluetooth.BluetoothConfig;
import com.puregodic.android.prezentainer.bluetooth.BluetoothHelper;

import java.util.ArrayList;
import java.util.Set;

public class SettingBluetoothActivity extends AppCompatActivity implements BluetoothHelper {

    private BluetoothAdapter mBluetoothAdapter;

    private ListView listViewPaired;

    private String mDeviceName;

    private Intent returnDeviceNameIntent;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bluetooth);

        // Toolbar 설정
        mToolbar = (Toolbar)findViewById(R.id.toolbarsettingBtActivity);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left));
        getSupportActionBar().setTitle("연결된 PC");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 사용자가 반드시 Bluetooth연결을 허용해야함
        isEnabledAdapter();

        // SettingFragment에 반드시 Device Name(PC)를 전달해야함.
        returnDeviceNameIntent = new Intent();
        returnDeviceNameIntent.putExtra("deviceName", mDeviceName);
        setResult(BluetoothConfig.REQUEST_DEVICENAME, returnDeviceNameIntent);

        listViewPaired = (ListView)findViewById(R.id.listViewPaired);

    }

    @Override
    protected void onResume() {

        // 현재 페어링된 PC를 list화 함
        listPairedDevices();


        // FloatingActionButton
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_list);
         fab.attachToListView(listViewPaired);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingBluetoothActivity.this, PairingActivity.class));
            }
        });
        super.onResume();
    }

    @Override
    public void isEnabledAdapter() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothConfig.REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == BluetoothConfig.REQUEST_ENABLE_BT) {

            // OK 버튼을 눌렀을때
            if (resultCode == RESULT_OK) {
                Toast.makeText(SettingBluetoothActivity.this, "블루투스를 켰습니다", Toast.LENGTH_SHORT).show();
                listPairedDevices();
            } else {
                Toast.makeText(SettingBluetoothActivity.this, "블루투스를 꼭 켜주세요", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        
        super.onActivityResult(requestCode, resultCode, intent);
    }
    // List Devices paired
    private void listPairedDevices() {
        
        // 패어링된 기기들을 Set화 시킴
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<PairedDeviceData> pairedDeviceArrayList = new ArrayList<PairedDeviceData>();

        // 패어링된 device를 list
        for (BluetoothDevice pairedDevice : pairedDevices) {

            // 패어링된 기기의 type, name, adress를 data객체에 초기화
            int type = pairedDevice.getBluetoothClass().getMajorDeviceClass();
            if(type == BluetoothConfig.COMPUTER){
                PairedDeviceData data = new PairedDeviceData(type, pairedDevice.getName(),pairedDevice.getAddress());
                pairedDeviceArrayList.add(data);
            }


        }
        
        PairedDeviceAdapter adapter = new PairedDeviceAdapter(this,pairedDeviceArrayList);
        
        
        listViewPaired.setAdapter(adapter);
        listViewPaired.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               final String mDeviceName = ((TextView)view.findViewById(R.id.pairedDeviceName)).getText().toString();

                // AlertDialog
                AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                        SettingBluetoothActivity.this);
                mAlertBuilder.setTitle(mDeviceName)
                        .setMessage("파워포인트가 실행 될 PC가 " + mDeviceName + " 이(가) 맞습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네, 맞습니다", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                returnDeviceNameIntent.putExtra("deviceName", mDeviceName);
                                setResult(BluetoothConfig.REQUEST_DEVICENAME, returnDeviceNameIntent);
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

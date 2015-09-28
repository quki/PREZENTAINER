package com.puregodic.android.prezentainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.bluetooth.BluetoothConfig;
import com.puregodic.android.prezentainer.bluetooth.BluetoothHelper;
import com.skyfishjy.library.RippleBackground;

import java.lang.reflect.Method;
import java.util.Set;

public class PairingActivity extends AppCompatActivity implements BluetoothHelper{


    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_DISCOVERABLE = 3;
    private RippleBackground rippleBackground;
    BroadcastReceiver mBroadcastReceiver;

    private CheckedTextView checkedTextView;

    private Toolbar mToolbar;
    private PairingFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        // Toolbar 설정
        mToolbar = (Toolbar)findViewById(R.id.toolbarPairingActivity);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left));
        getSupportActionBar().setTitle("주변 기기검색");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        checkedTextView = (CheckedTextView) findViewById(R.id.checkedTextView);
        ImageView button = (ImageView) findViewById(R.id.centerImage);

        isEnabledAdapter();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothAdapter.isDiscovering()) {

                    mBluetoothAdapter.cancelDiscovery();
                    rippleBackground.stopRippleAnimation();
                    Toast.makeText(PairingActivity.this, "검색중지", Toast.LENGTH_SHORT).show();
                } else {

                    // Fragment 초기화
                    fragment = new PairingFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_body, fragment)
                            .commit();


                    // 애니매이션 효과
                    rippleBackground.startRippleAnimation();
                    Toast.makeText(PairingActivity.this, "검색을 시작합니다", Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });

        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    // 내 기기를 찾을 수 없게 하고 싶다.
                    checkedTextView.setChecked(false);
                    if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

                        Toast.makeText(PairingActivity.this, "'예' 버튼을 누르면 내 기기 검색 허용을 차단합니다", Toast.LENGTH_SHORT).show();
                        Intent cancelDiscoverableIntent = new
                                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        cancelDiscoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                        startActivity(cancelDiscoverableIntent);
                    }

                }


                // 내 기기 검색 허용
                else {
                    checkedTextView.setChecked(true);

                    if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_NONE) {
                        Toast.makeText(PairingActivity.this, "내 디바이스를 패어링된 기기 역시 검색불가", Toast.LENGTH_SHORT).show();

                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);

                    } else if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {


                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);   --> 초단위 300초 : 5분
                        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
                    }
                }

            }
        });

    }

    public void myOnclick(View v){

        final BluetoothDevice deviceFound =  (BluetoothDevice)v.getTag();
        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(PairingActivity.this);
        mAlertBuilder.setTitle(deviceFound.getName()+" ("+deviceFound.getAddress()+")")
                .setMessage("블루투스 페어링을 요청하시겠어요?\n상대 기기의 승인 이후 페어링이 완료 됩니다.")
                .setCancelable(false)
                .setPositiveButton("연결요청", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mBluetoothAdapter.cancelDiscovery();
                        rippleBackground.stopRippleAnimation();

                        try {

                            Method method = deviceFound.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(deviceFound, (Object[]) null);

                        } catch (Exception e) {
                            Toast.makeText(PairingActivity.this
                                    , "요청오류", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mDialog = mAlertBuilder.create();
        mDialog.show();


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();// retrieve : 검색하다.

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 새로운 기기를 찾았을 때...
                    BluetoothDevice devicesFound= intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // 패어링 안되있는 기기 만 찾아서 아이콘을 띄움
                    if(!isPaired(devicesFound)){
                        fragment.setDeviceIconFound(devicesFound);
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {

                    // 검색이 완료되었을 때...
                    Toast.makeText(PairingActivity.this,"주변 기기 검색이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    rippleBackground.stopRippleAnimation();

                } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                    //broadcast intent 에는 현재 스캔모드와 이전 스캔 모드가 엑스트라로 포함된다.
                    int preScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, -1);
                    int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){


                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Toast.makeText(PairingActivity.this, "연결 완료", Toast.LENGTH_SHORT).show();
                        finish();

                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                        Toast.makeText(PairingActivity.this, "UnPaired", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        };
        // BroadCastReceiver를 당연히 먼저 선언해주어야한다.
        // IntentFilter 이벤트를 모니터링 할 수 있다.
        IntentFilter deviceFoundFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND); // 새로운 기기를 찾았을 때
        IntentFilter deviveDiscoveryFinishedFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 탐색을 끝냈을때
        IntentFilter scanModechanged = new IntentFilter(
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        IntentFilter bondStateChanged = new IntentFilter(
                BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        // 리시버와 intentfilter를 등록한다. 이는 해당 이벤트를 BroadcastReceiver로 통보하도록 요구!
        registerReceiver(mBroadcastReceiver, deviceFoundFilter);
        registerReceiver(mBroadcastReceiver, deviveDiscoveryFinishedFilter);
        registerReceiver(mBroadcastReceiver, scanModechanged);
        registerReceiver(mBroadcastReceiver, bondStateChanged);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BluetoothConfig.REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(PairingActivity.this, "블루투스를 켰습니다\n작업을 시작하세요", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "블루투스를 반드시 켜야만 해요.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            // 내 장치 검색모드
        } else if (requestCode == REQUEST_DISCOVERABLE) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(PairingActivity.this, "검색을 허용합니다.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "검색을 허용하지 않습니다.",
                        Toast.LENGTH_SHORT).show();
                checkedTextView.setChecked(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onDestroy() {

        // unregister the BroadcastReceiver
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();

    }
    @Override
    public void isEnabledAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothConfig.REQUEST_ENABLE_BT);
        }
    }

    // 이미 기기가 Pairing되어있는지 확인하는 함수
    private boolean isPaired(BluetoothDevice deviceFound){

        boolean isPaired= false;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice pariedDevice : pairedDevices){

            if(pariedDevice.equals(deviceFound)){
                isPaired = true;
            }
        }
        return isPaired;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pairing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

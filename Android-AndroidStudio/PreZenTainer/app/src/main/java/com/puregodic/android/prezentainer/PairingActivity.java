package com.puregodic.android.prezentainer;

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
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.lang.reflect.Method;
import java.util.Set;

public class PairingActivity extends AppCompatActivity {


    BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1; // intent의 구분자 값으로써 데이터 전달의 1대1대응을 위함.
    private final int REQUEST_DISCOVERABLE = 2;
    private RippleBackground rippleBackground;
    BroadcastReceiver mBroadcastReceiver;

    CheckedTextView checkedTextView;

    private PairingFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        rippleBackground = (RippleBackground) findViewById(R.id.content);

        checkedTextView = (CheckedTextView) findViewById(R.id.checkedTextView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 만일 하드웨어에 블루투스가 없다면 null을 return 한다.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 단말입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }


        ImageView button = (ImageView) findViewById(R.id.centerImage);

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

                        Toast.makeText(PairingActivity.this, "끄고 싶은데", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PairingActivity.this, "내 디바이스를 패어링된놈이고 나발이고 검색불가 !", Toast.LENGTH_SHORT).show();

                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);

                    } else if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {

                        Toast.makeText(PairingActivity.this, "내 디바이스를 페어링 된적이 있는 장치들은 검색이 가능하지만 새로운 장치들은 검색할수 없는 상태", Toast.LENGTH_SHORT).show();

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

        if (mBluetoothAdapter.isEnabled()) {

            Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어있습니다.",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "블루투스를 켜주세요.",
                    Toast.LENGTH_SHORT).show();

            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            // 시스템에 블루투스 켤 수있는 Alert메시지 요청
        }


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
                    Toast.makeText(PairingActivity.this, "스캔모드 체인지/// 이전모드 : " + preScanMode + "\n///현재모드 : " + scanMode, Toast.LENGTH_LONG).show();
                } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){


                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Toast.makeText(PairingActivity.this, "연결완료", Toast.LENGTH_SHORT).show();
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

        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(PairingActivity.this, "블루투스를 켰습니다\n작업을 시작하세요", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "블루투스를 반드시 켜야만 합니다.",
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

    private boolean isPaired(BluetoothDevice deviceFound){

        boolean isPaired= false;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice pariedDevice : pairedDevices){

            if(pariedDevice.equals(deviceFound)){
                Toast.makeText(PairingActivity.this,"이미 패어링 되어있음", Toast.LENGTH_SHORT).show();
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

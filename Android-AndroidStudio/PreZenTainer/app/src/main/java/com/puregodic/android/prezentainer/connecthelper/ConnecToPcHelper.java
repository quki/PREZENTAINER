
package com.puregodic.android.prezentainer.connecthelper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnecToPcHelper {
    
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
    // UUID 설정 (SPP)
    private UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    private BluetoothDevice targetDevice = null;
    private BluetoothSocket mBluetoothSocket = null;

    private static final String TAG = "==EVENTOPC==";

    private ConnectionActionPc mConnectionActionPc;

    // Connection Action (PC) Interface Initailize
    public void registerConnectionAction(ConnectionActionPc mConnectionActionPc) {
        this.mConnectionActionPc = mConnectionActionPc;
    }

    /*
     * PC이름으로 패어링된 PC측에 프로그램이 잘 실행되었는지 확인한다.
     * PC측에 별다른 이벤트는 동작하지 않는다.
     * 연결 여부를 ConnectionActionPc Interface를 이용하여 Button UI를 갱신해준다.
     * 
     * */
    public void connectWithPc(String mDeviceName){
        
        mConnectionActionPc.onConnectionActionRequest();
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(mDeviceName)) {
                targetDevice = pairedDevice;
                Log.e(TAG, targetDevice.toString());
                break;
            }
        }
        // If the device was not found, toast an error and return
        if (targetDevice == null) {
            Log.e(TAG, "target decvice NULL");
            return;
        }
        
        // Create a connection to the device with the SPP UUID
        try {
            mBluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // Connect to the device
        try {
            mBluetoothSocket.connect();
            // 인터페이스를 이용해서 SettingActivity로 전달
            if(mConnectionActionPc != null){
                mConnectionActionPc.onConnectionActionComplete();
            }
        } catch (IOException e) {
            mConnectionActionPc.onConnectionActionError();
            Log.e(TAG, "Need PORT NUM");
            e.printStackTrace();
            return;
        }
        // Write the data by using OutputStreamWriter
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write("conne");
            outputStreamWriter.flush();
            Log.e(TAG, "===Connected, Hello PC side===");
        } catch (IOException e) {
            Log.e(TAG, "Unable to send message to the device");
            e.printStackTrace();
        }
        
        disconnect();
    }
    
    
    /*
     * PC이름으로 패어링된 PC에 Event를 전달한다.
     * 이때 두가지의 Event(왼쪽,오른쪽)이 있는데,
     * 이는 기어를 통해서 direction을 String을 전달 받고
     * 그대로 PC측 프로그램에 write하여,PC프로그램에서 Event를 구분한다
     * 
     * */
    public void transferToPc(String mDeviceName, String direction) {

        // 페어링 된 device를 target으로 저장        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(mDeviceName)) {
                targetDevice = pairedDevice;
                Log.e(TAG, targetDevice.toString());
                break;
            }
        }

        // If the device was not found, toast an error and return
        if (targetDevice == null) {
            Log.e(TAG, "target decvice is NULL");
            return;
        }

        // Create a connection to the device with the SPP UUID
        try {
            mBluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Connect to the PC and Android
        try {
            mBluetoothSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect with the device");
            e.printStackTrace();
            return;
        }

        // Write the data by using OutputStreamWriter
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write(direction);
            outputStreamWriter.flush();
            Log.e(TAG, "==="+direction+"===");
        } catch (IOException e) {
            Log.e(TAG, "Unable to send message to the device");
            e.printStackTrace();
        }
        
        disconnect();

    }
    
    // Close the Socket
    private void disconnect(){
        
        try {
            mBluetoothSocket.close();
            Log.e(TAG, "===Close===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

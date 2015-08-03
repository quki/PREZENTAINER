
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

    private static final String TAG_BLUETOOTH = "==EventToPc==";

    

    private ConnectionActionPc mConnectionActionPc;

    public void registerConnectionAction(ConnectionActionPc mConnectionActionPc) {
        this.mConnectionActionPc = mConnectionActionPc;
    }

   
    public void connectToPc(String mDeviceName){
        
        mConnectionActionPc.onConnectionActionRequest();
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(mDeviceName)) {
                targetDevice = pairedDevice;
                Log.e(TAG_BLUETOOTH, targetDevice.toString());
                break;
            }
        }
        // If the device was not found, toast an error and return
        if (targetDevice == null) {
            Log.e(TAG_BLUETOOTH, "target decvice NULL");
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
            Log.e(TAG_BLUETOOTH, "Need PORT NUM");
            e.printStackTrace();
            return;
        }
     // Write the data by using OutputStreamWriter
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write("conne");
            outputStreamWriter.flush();
            Log.e(TAG_BLUETOOTH, "===Connected, Hello PC side===");
        } catch (IOException e) {
            Log.e(TAG_BLUETOOTH, "--------------Unable to send message to the device--------------");
            e.printStackTrace();
        }
        
        disconnect();
    }
    
    public void transferToPc(String mDeviceName) {

        /*
         * 클릭해서 가져온 Device의 String 값과 페어링 된 것이 일치하는지 한번 더 확인 후 BluetoothDevice
         * 객체로 target저장
         */
        BluetoothDevice targetDevice = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(mDeviceName)) {
                targetDevice = pairedDevice;
                Log.e(TAG_BLUETOOTH, targetDevice.toString());
                break;
            }
        }

        // If the device was not found, toast an error and return
        if (targetDevice == null) {
            Log.e(TAG_BLUETOOTH, "target decvice NULL");
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
        } catch (IOException e) {
            Log.e(TAG_BLUETOOTH, "Unable to connect with the device");
            e.printStackTrace();
            return;
        }

        // Write the data by using OutputStreamWriter
        try {
            // 인터페이스를 이용해서 SettingActivity로 전달
           /* if(mConnectionActionPc != null){
                mConnectionActionPc.onConnectionActionComplete();
            }*/
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write("hello");
            outputStreamWriter.flush();
            Log.e(TAG_BLUETOOTH, "===Write===");
        } catch (IOException e) {
            Log.e(TAG_BLUETOOTH, "--------------Unable to send message to the device--------------");
            e.printStackTrace();
        }
        
        disconnect();

    }
    
    public void disconnect(){
        
        try {
            mBluetoothSocket.close();
            Log.e(TAG_BLUETOOTH, "===Close===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

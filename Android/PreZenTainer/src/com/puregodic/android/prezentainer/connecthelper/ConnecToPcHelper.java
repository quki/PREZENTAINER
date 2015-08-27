
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
    
    // UUID ���� (SPP)
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
     * PC�̸����� �о�� PC���� ���α׷��� �� ����Ǿ����� Ȯ���Ѵ�.
     * PC���� ���ٸ� �̺�Ʈ�� �������� �ʴ´�.
     * ���� ���θ� ConnectionActionPc Interface�� �̿��Ͽ� Button UI�� �������ش�.
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
            // �������̽��� �̿��ؼ� SettingActivity�� ����
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
     * PC�̸����� �о�� PC�� Event�� �����Ѵ�.
     * �̶� �ΰ����� Event(����,������)�� �ִµ�,
     * �̴� �� ���ؼ� direction�� String�� ���� �ް�
     * �״�� PC�� ���α׷��� write�Ͽ�,PC���α׷����� Event�� �����Ѵ�
     * 
     * */
    public void transferToPc(String mDeviceName, String direction) {

        // �� �� device�� target���� ����        
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
            outputStreamWriter.write("right");
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

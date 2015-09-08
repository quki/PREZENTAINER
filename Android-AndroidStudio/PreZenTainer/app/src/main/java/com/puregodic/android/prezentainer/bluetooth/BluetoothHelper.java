
package com.puregodic.android.prezentainer.bluetooth;

public interface BluetoothHelper {

    public static final String TAG_BT = "==BLUETOOTH HELPER==";
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_DEVICENAME = 2;

    void isEnabledAdapter();

}

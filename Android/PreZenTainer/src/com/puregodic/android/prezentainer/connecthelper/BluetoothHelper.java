
package com.puregodic.android.prezentainer.connecthelper;

public interface BluetoothHelper {

    public static final String TAG_BT = "==BluetoothHelper==";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_DEVICENAME = 2;

    void isEnabledAdapter();

}

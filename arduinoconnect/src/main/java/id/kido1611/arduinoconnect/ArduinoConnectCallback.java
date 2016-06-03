package id.kido1611.arduinoconnect;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kido1611 on 5/29/16.
 */
public interface ArduinoConnectCallback {

    void onArduinoConnected(BluetoothDevice device);
    void onArduinoConnectFailed();
    void onSerialTextReceived(String text);
    void onBluetoothDeviceNotFound();
    void onBluetoothFailedEnabled();
}

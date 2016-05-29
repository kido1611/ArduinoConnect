package id.kido1611.arduinoconnect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by kido1611 on 5/22/16.
 */
public class ConnectArduino extends Thread{

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ArduinoConnectDialog.BluetoothDeviceCallback callback;

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    public ConnectArduino(BluetoothDevice device, ArduinoConnectDialog.BluetoothDeviceCallback cb){
        this.callback = cb;
        try {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDevice = device;
    }

    @Override
    public void run() {
        try {
            mSocket.connect();

            if(callback!=null) callback.onConnected(mSocket);

        } catch (IOException e) {
            e.printStackTrace();
            try {
                mSocket.close();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
            if(callback!=null) callback.onFailed();
        }
    }
}

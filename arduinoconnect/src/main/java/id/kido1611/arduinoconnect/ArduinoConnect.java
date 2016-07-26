package id.kido1611.arduinoconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kido1611 on 5/29/16.
 */
public class ArduinoConnect {

    private BluetoothAdapter mBLAdapter;
    private ArduinoConnectCallback mCallback;
    private FragmentManager mFragmentManager;
    private Activity mContext;

    private BluetoothSocket mConnectedSocket = null;
    private BluetoothDevice mConnectedDevice = null;

    private ArduinoConnectDialog mDialog;

    int sleepTime = 1000;

    protected static final int ARDUINO_MSG_CONNECTED = 0;
    protected static final int ARDUINO_MSG_CONNECT_FAILED = 1;
    protected static final int ARDUINO_MSG_RECEIVE_SERIAL_TEXT = 2;
    protected static final int ARDUINO_MSG_BLUETOOTH_NOT_FOUND = 3;
    protected static final int ARDUINO_MSG_BLUETOOTH_FAILED = 4;

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ARDUINO_MSG_CONNECTED:
                    if(mCallback!=null) mCallback.onArduinoConnected(mConnectedDevice);
                    break;
                case ARDUINO_MSG_CONNECT_FAILED:
                    if(mCallback!=null) mCallback.onArduinoConnectFailed();
                    break;
                case ARDUINO_MSG_RECEIVE_SERIAL_TEXT:
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf);
                    if(mCallback!=null) mCallback.onSerialTextReceived(strIncom);
                    break;
                case ARDUINO_MSG_BLUETOOTH_NOT_FOUND:
                    if(mCallback!=null) mCallback.onBluetoothDeviceNotFound();
                    break;
                case ARDUINO_MSG_BLUETOOTH_FAILED:
                    if(mCallback!=null) mCallback.onBluetoothFailedEnabled();
                    break;
            }
        }
    };

    public void setSleepTime(int time){
        sleepTime = time;
    }

    public ArduinoConnect(Activity activity, FragmentManager fragmentManager){
        this.mContext = activity;
        this.mFragmentManager = fragmentManager;
        this.mCallback = null;
        init();
    }
    public ArduinoConnect(Activity activity, FragmentManager fragmentManager, ArduinoConnectCallback callback){
        this.mContext = activity;
        this.mFragmentManager = fragmentManager;
        this.mCallback = callback;
        init();
    }

    private void init(){
        mBLAdapter = BluetoothAdapter.getDefaultAdapter();
        mDialog = new ArduinoConnectDialog();
        mDialog.setCallback(new ArduinoConnectDialog.BluetoothDeviceCallback() {
            @Override
            public void onConnected(BluetoothDevice device, BluetoothSocket socket) {
                disconnected();
                mConnectedDevice = device;
                mConnectedSocket = socket;
                ManageBluetooth mManage = new ManageBluetooth(socket);
                mManage.start();

                sleep(2000);
                mHandler.obtainMessage(ARDUINO_MSG_CONNECTED);
                if(mCallback!=null){
                	mCallback.onArduinoConnected(mConnectedDevice);
                }
            }

            @Override
            public void onFailed() {
                mHandler.obtainMessage(ARDUINO_MSG_CONNECT_FAILED);
            }
        });

        if(mBLAdapter==null){
            mHandler.obtainMessage(ARDUINO_MSG_BLUETOOTH_NOT_FOUND);
        }

    }

    public void setCallback(ArduinoConnectCallback callback){
        this.mCallback = callback;
    }

    public void showDialog(){
        if(mBLAdapter.isEnabled()) {
            if (mDialog != null)
                mDialog.show(mFragmentManager, "ArduinoConnectDialog");
        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(enableBtIntent, 0);
        }
    }
    public void hideDialog(){
        if(mDialog!=null)
            mDialog.dismiss();
    }

    public void disconnected(){
        if(mConnectedSocket!=null)
            try {
                mConnectedSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public void sendMessage(String message){
        if(mConnectedSocket!=null){
            try {
                mConnectedSocket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==mContext.RESULT_OK){
            if (mDialog != null)
                mDialog.show(mFragmentManager, "ArduinoConnectDialog");
        }else if(requestCode==0 && resultCode==mContext.RESULT_CANCELED){
            mHandler.obtainMessage(ARDUINO_MSG_BLUETOOTH_FAILED);
        }
    }

    class ManageBluetooth extends Thread{

        BluetoothSocket mSocket;

        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ManageBluetooth(BluetoothSocket socket){
            mSocket = socket;
            try {
                mmOutStream = mSocket.getOutputStream();
                mmInStream = mSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            byte[] buffer;
            int bytes;

            while (true) {
                try {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);

                    mHandler.obtainMessage(ARDUINO_MSG_RECEIVE_SERIAL_TEXT, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

}

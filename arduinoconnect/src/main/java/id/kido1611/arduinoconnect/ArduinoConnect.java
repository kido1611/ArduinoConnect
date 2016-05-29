package id.kido1611.arduinoconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;

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

    private id.kido1611.arduinoconnect.ArduinoConnectDialog mDialog;

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
            public void onConnected(BluetoothSocket socket) {
                disconnected();
                mConnectedSocket = socket;
                if(mCallback!=null) mCallback.onArduinoConnected();

                ManageBluetooth mManage = new ManageBluetooth(socket);
                mManage.start();
            }

            @Override
            public void onFailed() {
                if(mCallback!=null) mCallback.onArduinoConnectFailed();
            }
        });

        if(mBLAdapter==null && mCallback!=null){
            mCallback.onBluetoothDeviceNotFound();
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
            if(mCallback!=null) mCallback.onBluetoothFailedEnabled();
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

            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            while (mmInStream==null) {
                try {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buffer = new byte[1024];

                    bytes = mmInStream.read(buffer);
                    String strIncom = new String(buffer);
                    if(mCallback!=null) mCallback.onSerialTextReceived(strIncom);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

}

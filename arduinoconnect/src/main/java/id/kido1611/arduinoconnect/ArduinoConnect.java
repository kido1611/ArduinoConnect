package id.kido1611.arduinoconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    private BluetoothDevice mConnectedDevice = null;

    private DialogConnect mDialog;

    int sleepTime = 100;

    /**
     * ARDUINO_MSG_CONNECTED = 0
     * Use for ArduinoConnect to send message if Android and Arduino is connected
     */
    protected static final int ARDUINO_MSG_CONNECTED = 0;
    /**
     * ARDUINO_MSG_CONNECT_FAILDE = 1
     * Use for ArduinoConnect to send message if Android failed to connect to Arduino
     */
    protected static final int ARDUINO_MSG_CONNECT_FAILED = 1;
    /**
     * ARDUINO_MSG_RECEIVE_SERIAL_TEXT = 2
     * Use for ArduinoConnect to send message when receive message or text from Arduino
     */
    protected static final int ARDUINO_MSG_RECEIVE_SERIAL_TEXT = 2;
    /**
     * ARDUINO_MSG_BLUETOOTH_NOT_FOUND = 3
     * Use for ArduinoConnect to send message when there is Bluetooth Device found on phone
     */
    protected static final int ARDUINO_MSG_BLUETOOTH_NOT_FOUND = 3;
    /**
     * ARDUINO_MSG_BLUETOOTH_FAILED = 4
     * Use for ArduinoConnect to send message when failed to enable Bluetooth
     */
    protected static final int ARDUINO_MSG_BLUETOOTH_FAILED = 4;
    /**
     * ARDUINO_MSG_DISCONNECTED = 5
     * Use for ArduinoConnect to send message when Android and Arduino disconnected
     */
    protected static final int ARDUINO_MSG_DISCONNECTED = 5;
    /**
     * ARDUINO_MSG_NOT_CONNECTED = 6
     * Use for ArduinoConnect to send message when Android and Arduino not connected
     */
    protected static final int ARDUINO_MSG_NOT_CONNECTED = 6;

    protected Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case ARDUINO_MSG_CONNECTED:
                    if(mCallback!=null){
                        mCallback.onArduinoConnected(mConnectedDevice);
                    }
                    break;
                case ARDUINO_MSG_CONNECT_FAILED:
                    if(mCallback!=null) mCallback.onArduinoConnectFailed();
                    break;
                case ARDUINO_MSG_RECEIVE_SERIAL_TEXT:
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf);
                    strIncom.trim();
                    if(mCallback!=null && !strIncom.isEmpty() && !strIncom.equals(" ") && !strIncom.equals("")) mCallback.onSerialTextReceived(strIncom);
                    break;
                case ARDUINO_MSG_BLUETOOTH_NOT_FOUND:
                    if(mCallback!=null) mCallback.onBluetoothDeviceNotFound();
                    break;
                case ARDUINO_MSG_BLUETOOTH_FAILED:
                    if(mCallback!=null) mCallback.onBluetoothFailedEnabled();
                    break;
                case ARDUINO_MSG_DISCONNECTED:
                    if(mCallback!=null) mCallback.onArduinoDisconnected();
                    break;
                case ARDUINO_MSG_NOT_CONNECTED:
                    if(mCallback!=null) mCallback.onArduinoNotConnected();
                    break;
            }
        }
    };

    /**
     * Set delay time to read message from arduino
     * @param milliseconds settime in millisecond
     */
    public void setSleepTime(int milliseconds){
        sleepTime = milliseconds;
    }
    /**
     * Set delay time to read message from arduino
     * @param milliseconds settime in millisecond
     */
    public void setDelayTime(int milliseconds){
        sleepTime = milliseconds;
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
        mDialog = new DialogConnect();
        mDialog.setCallback(new DialogConnect.BluetoothDeviceCallback() {
            @Override
            public void onConnected(BluetoothDevice device, BluetoothSocket socket) {
                disconnected();
                mConnectedDevice = device;
                mConnectedSocket = socket;
                ManageBluetooth mManage = new ManageBluetooth(socket);
                mManage.start();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.obtainMessage(ARDUINO_MSG_CONNECTED).sendToTarget();
            }

            @Override
            public void onFailed() {
                mHandler.obtainMessage(ARDUINO_MSG_CONNECT_FAILED).sendToTarget();
            }
        });

        if(mBLAdapter==null){
            mHandler.obtainMessage(ARDUINO_MSG_BLUETOOTH_NOT_FOUND).sendToTarget();
        }

    }

    public void setCallback(ArduinoConnectCallback callback){
        this.mCallback = callback;
    }

    public void showDialog(){
        if(mBLAdapter.isEnabled()) {
            if (mDialog != null)
                mDialog.show(mFragmentManager, "DialogConnect");
        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(enableBtIntent, 0);
        }
    }
    public void hideDialog(){
        if(mDialog!=null)
            mDialog.dismiss();
    }

    public boolean isConnected(){
        return mConnectedSocket!=null && mConnectedSocket.isConnected();
    }

    public void disconnected(){
        if(mConnectedSocket!=null && mConnectedSocket.isConnected()) {
            try {
                mConnectedSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally(){
                mHandler.obtainMessage(ARDUINO_MSG_DISCONNECTED).sendToTarget();
            }
        }
    }


    public void sendMessage(String message){
        if(mConnectedSocket!=null && mConnectedSocket.isConnected()){
            try {
                mConnectedSocket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }else{
            mHandler.obtainMessage(ARDUINO_MSG_NOT_CONNECTED).sendToTarget();
        }
    }

    public void sendCommand(String command, String data){
        StringBuilder builder = new StringBuilder();
        builder.append(data);
        builder.append(':');
        builder.append(command);
        sendMessage(builder.toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==mContext.RESULT_OK){
            if (mDialog != null)
                mDialog.show(mFragmentManager, "ArduinoConnectDialog");
        }else if(requestCode==0 && resultCode==mContext.RESULT_CANCELED){
            mHandler.obtainMessage(ARDUINO_MSG_BLUETOOTH_FAILED).sendToTarget();
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
                    disconnected();
                    break;
                }
            }
        }
    }

}

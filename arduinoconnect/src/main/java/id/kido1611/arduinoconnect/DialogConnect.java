package id.kido1611.arduinoconnect;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import id.kido1611.arduinoconnect.widget.CircularProgressView;

/**
 * Created by kido1611 on 5/29/16.
 */
public class DialogConnect extends DialogFragment {

    public DialogConnect(){

    }

    private ArrayList<BluetoothItem> mItems = new ArrayList<BluetoothItem>();

    private ListView mPairedList;
    private ListBluetoothAdapter mPairedAdapter;

    private Button mBtnRefresh;
    private Button mBtnSettings;

    private BluetoothAdapter mBLAdapter;

    private BluetoothDeviceCallback mCallback;

    private ProgressDialog mProgressDialog;
    private CircularProgressView mProgressView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppCompatAlertDialogFragment);

        mBLAdapter = BluetoothAdapter.getDefaultAdapter();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.dialog_title);
        mProgressDialog.setCancelable(false);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        getActivity().registerReceiver(mReceiver, filter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_list_bluetooth, container, false);

        mBtnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
        mBtnSettings = (Button) rootView.findViewById(R.id.btnSettings);
        mPairedList = (ListView) rootView.findViewById(R.id.list_paired);
        mProgressView = (CircularProgressView) rootView.findViewById(R.id.progressView);
        mProgressView.setVisibility(View.GONE);

        mPairedAdapter = new ListBluetoothAdapter(getActivity(), mItems);
        mPairedList.setAdapter(mPairedAdapter);

        refreshList();

        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        mPairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(mBLAdapter.isDiscovering())
                {
                    mBLAdapter.cancelDiscovery();
                }

                BluetoothItem item = mItems.get(i);
                BluetoothDevice mDevice = item.getDevice();
                if(item.isPaired()){
                    connectArduino(mDevice);
                }else{
                    pairDevice(mDevice);
                }
            }
        });

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        return rootView;
    }

    private void pairDevice(BluetoothDevice device) {
        mProgressDialog.setMessage(getString(R.string.pairing_title));
        mProgressDialog.show();
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectArduino(BluetoothDevice mDevice){
        mProgressDialog.setMessage("Connecting to "+mDevice.getName());
        mProgressDialog.show();
        ConnectArduino mConnArduino = new ConnectArduino(mDevice, new BluetoothDeviceCallback() {
            @Override
            public void onConnected(BluetoothDevice device, BluetoothSocket socket) {
                mProgressDialog.dismiss();
                if(mCallback!=null) mCallback.onConnected(device, socket);
                dismiss();
            }

            @Override
            public void onFailed() {
                if(mCallback!=null) mCallback.onFailed();
                mProgressDialog.dismiss();
            }
        });
        mConnArduino.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
        if (mBLAdapter != null) {
            if (mBLAdapter.isDiscovering()) {
                mBLAdapter.cancelDiscovery();
            }
        }
    }

    public void setCallback(BluetoothDeviceCallback callback){
        this.mCallback = callback;
    }

    private void refreshList(){
        mItems.clear();
//        BluetoothItem categoryPaired = new BluetoothItem();
//        categoryPaired.setCategory(true);
//        categoryPaired.setName(getString(R.string.paired_list_title));
//        mItems.add(categoryPaired);
        Set<BluetoothDevice> pairedDevices = mBLAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BluetoothItem item = new BluetoothItem();
                item.setDevice(device);
                item.setPaired(true);
                mItems.add(item);
            }
        }
//        BluetoothItem categoryUnpaired = new BluetoothItem();
//        categoryUnpaired.setCategory(true);
//        categoryUnpaired.setName(getString(R.string.unpaired_list_title));
//        mItems.add(categoryUnpaired);
        mPairedAdapter.notifyDataSetChanged();
        mBLAdapter.cancelDiscovery();
        if(!mBLAdapter.isDiscovering())
        {
            mBLAdapter.startDiscovery();
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mProgressView.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressView.setVisibility(View.GONE);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device==null) return;
                if(!mPairedAdapter.isAvailable(device)){
                    BluetoothItem item = new BluetoothItem();
                    item.setDevice(device);
                    item.setPaired(false);
                    item.setCategory(false);
                    mItems.add(item);
                    mPairedAdapter.notifyDataSetChanged();
                }
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                mProgressDialog.dismiss();
                refreshList();
            }
        }
    };

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    class ConnectArduino extends Thread{

        DialogConnect.BluetoothDeviceCallback callback;

        private BluetoothSocket mSocket;
        private BluetoothDevice mDevice;

        public ConnectArduino(BluetoothDevice device, DialogConnect.BluetoothDeviceCallback cb){
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
                if(mBLAdapter.isDiscovering()){
                    mBLAdapter.cancelDiscovery();
                }
                mSocket.connect();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(callback!=null) callback.onConnected(mDevice, mSocket);

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

    interface BluetoothDeviceCallback{
        void onConnected(BluetoothDevice device, BluetoothSocket socket);
        void onFailed();
    }
}

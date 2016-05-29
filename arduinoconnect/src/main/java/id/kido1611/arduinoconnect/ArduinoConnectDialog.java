package id.kido1611.arduinoconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by kido1611 on 5/29/16.
 */
public class ArduinoConnectDialog extends DialogFragment {

    public ArduinoConnectDialog(){

    }

    private ArrayList<BluetoothDevice> mItems = new ArrayList<BluetoothDevice>();

    private ListView mPairedList;
    private ListBluetoothAdapter mPairedAdapter;

    private Button mBtnRefresh;
    private Button mBtnSettings;

    private BluetoothAdapter mBLAdapter;

    private BluetoothDeviceCallback mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBLAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_list_bluetooth, container, false);

        mBtnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
        mBtnSettings = (Button) rootView.findViewById(R.id.btnSettings);
        mPairedList = (ListView) rootView.findViewById(R.id.list_paired);

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
                BluetoothDevice mDevice = mItems.get(i);
                ConnectArduino mConnArduino = new ConnectArduino(mDevice, new BluetoothDeviceCallback() {
                    @Override
                    public void onConnected(BluetoothSocket socket) {
                        if(mCallback!=null) mCallback.onConnected(socket);

                        dismiss();
                    }

                    @Override
                    public void onFailed() {
                        if(mCallback!=null) mCallback.onFailed();
                    }
                });
                mConnArduino.start();
            }
        });
        getDialog().setTitle(R.string.dialog_title);
        return rootView;
    }

    public void setCallback(BluetoothDeviceCallback callback){
        this.mCallback = callback;
    }

    private void refreshList(){
        mItems.clear();
        Set<BluetoothDevice> pairedDevices = mBLAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mItems.add(device);
            }
        }
        mPairedAdapter.notifyDataSetChanged();
    }

    public interface BluetoothDeviceCallback{
        void onConnected(BluetoothSocket socket);
        void onFailed();
    }
}

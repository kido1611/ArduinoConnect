package id.kido1611.arduinoconnectexample;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import id.kido1611.arduinoconnect.ArduinoConnect;
import id.kido1611.arduinoconnect.ArduinoConnectCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements ArduinoConnectCallback{

    public MainActivityFragment() {
    }

    private TextInputLayout mMessageLayout, mCommandLayout, mOutputLayoutText;
    private TextInputEditText mMessageText, mCommandText, mOutputText;
    private Button mBtnSubmit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).getArduinoConnect().setCallback(this);
        getArduinoConnect().setSleepTime(1000);
    }

    private ArduinoConnect getArduinoConnect(){
        if((MainActivity)getActivity()==null) return null;

        return ((MainActivity)getActivity()).getArduinoConnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        mMessageLayout = (TextInputLayout) rootView.findViewById(R.id.messageLayout);
        mCommandLayout = (TextInputLayout) rootView.findViewById(R.id.commandLayout);
        mOutputLayoutText = (TextInputLayout) rootView.findViewById(R.id.outputLayout);
        mMessageText = (TextInputEditText) rootView.findViewById(R.id.messageText);
        mCommandText = (TextInputEditText) rootView.findViewById(R.id.commandText);
        mOutputText = (TextInputEditText) rootView.findViewById(R.id.outputText);
        mBtnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCommandText.getText().toString().isEmpty()){
                    if(getArduinoConnect()!=null) getArduinoConnect().sendMessage(mMessageText.getText().toString());
                }else{
                    if(getArduinoConnect()!=null) getArduinoConnect().sendCommand(mCommandText.getText().toString(), mMessageText.getText().toString());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onArduinoConnected(BluetoothDevice device) {
        if(getArduinoConnect()!=null){
            getArduinoConnect().sendMessage("Connected..");
        }
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        ((MainActivity)getActivity()).hideFAB(View.GONE);
    }

    @Override
    public void onArduinoDisconnected() {
        if(getArduinoConnect()!=null){
            Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).hideFAB(View.VISIBLE);
        }
    }

    @Override
    public void onArduinoNotConnected() {
        Toast.makeText(getActivity(), "Not Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArduinoConnectFailed() {
        Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSerialTextReceived(String text) {
        mOutputText.setText(text);
    }

    @Override
    public void onBluetoothDeviceNotFound() {
        Toast.makeText(getActivity(), "Bluetooth device not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothFailedEnabled() {
        Toast.makeText(getActivity(), "Failed to turn on Bluetooth", Toast.LENGTH_SHORT).show();
    }
}

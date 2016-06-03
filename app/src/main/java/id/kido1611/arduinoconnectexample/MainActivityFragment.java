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

import id.kido1611.arduinoconnect.ArduinoConnectCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements ArduinoConnectCallback{

    public MainActivityFragment() {
    }

    private TextInputLayout mInputLayoutDelay, mOutputLayoutText;
    private TextInputEditText mInputDelay, mOutputText;
    private Button mBtnSubmit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).getArduinoConnect().setCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        mInputLayoutDelay = (TextInputLayout) rootView.findViewById(R.id.inputLayout);
        mOutputLayoutText = (TextInputLayout) rootView.findViewById(R.id.outputLayout);
        mInputDelay = (TextInputEditText) rootView.findViewById(R.id.inputText);
        mOutputText = (TextInputEditText) rootView.findViewById(R.id.outputText);
        mBtnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getArduinoConnect().sendMessage(mInputDelay.getText().toString());
            }
        });

        return rootView;
    }

    @Override
    public void onArduinoConnected(BluetoothDevice device) {
        Toast.makeText(getActivity(), "Connected to "+device.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onArduinoConnectFailed() {

    }

    @Override
    public void onSerialTextReceived(String text) {
        mOutputText.setText(mOutputText.getText().toString()+"\n"+text);
    }

    @Override
    public void onBluetoothDeviceNotFound() {

    }

    @Override
    public void onBluetoothFailedEnabled() {

    }
}

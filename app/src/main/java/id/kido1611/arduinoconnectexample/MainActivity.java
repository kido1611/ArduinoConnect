package id.kido1611.arduinoconnectexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import id.kido1611.arduinoconnect.ArduinoConnect;
import id.kido1611.arduinoconnect.ArduinoConnectCallback;

public class MainActivity extends AppCompatActivity
        implements ArduinoConnectCallback{

    private ArduinoConnect mArduinoConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArduinoConnect = new ArduinoConnect(this, getSupportFragmentManager());
        mArduinoConnect.setCallback(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArduinoConnect.showDialog();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Must added to code
         */
        if(mArduinoConnect!=null)
            mArduinoConnect.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * Add this to fix memory leak
         */
        if(mArduinoConnect!=null)
            mArduinoConnect.disconnected();
    }

    private void showLog(String message){
        Log.d("ArduinoConnect", message);
    }

    @Override
    public void onArduinoConnected() {
        showLog("Connected");
    }

    @Override
    public void onArduinoConnectFailed() {
        showLog("Failed to connected");
    }

    @Override
    public void onSerialTextReceived(String text) {
        showLog(text);
    }

    @Override
    public void onBluetoothDeviceNotFound() {
        showLog("Bluetooth device not found");
    }

    @Override
    public void onBluetoothFailedEnabled() {
        showLog("Bluetooth device not enabled");
    }
}

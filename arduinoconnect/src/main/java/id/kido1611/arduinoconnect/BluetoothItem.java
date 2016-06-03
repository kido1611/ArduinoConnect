package id.kido1611.arduinoconnect;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kido1611 on 6/3/16.
 */

public class BluetoothItem {

    BluetoothDevice device;
    boolean paired;
    boolean category;
    String name;

    public boolean isCategory() {
        return category;
    }

    public void setCategory(boolean category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BluetoothItem(){
        paired = false;
        device = null;
        category = false;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public BluetoothDevice getDevice() {

        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}

# ArduinoConnect 
[![Build Status](https://travis-ci.org/kido1611/ArduinoConnect.svg?branch=master)](https://travis-ci.org/kido1611/ArduinoConnect) [![](https://jitpack.io/v/kido1611/ArduinoConnect.svg)](https://jitpack.io/#kido1611/ArduinoConnect)

Connecting Android and Arduino using bluetooth with Serial communication

## Tested on :
  * Android 4.1.2 and Arduino with HC-05 module

## Permission :
  This application using bluetooth permission to connect to Arduino. This permission is added on library
  ```xml
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
  ```

## How to use :
1. Add this to your root build.gradle
```Gradle
    allprojects {
    	repositories {
    		...
    		maven { url "https://jitpack.io" }
    	}
    }
```
2. Add dependency
```Gradle
    compile 'com.github.kido1611:ArduinoConnect:-SNAPSHOT'
```
3. Add line to your activity

```java
    public class MainActivity extends AppCompatActivity
            implements ArduinoConnectCallback{
    
      private ArduinoConnect mArduinoConnect;
    
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
            ...
          mArduinoConnect = new ArduinoConnect(this, getSupportFragmentManager());
          mArduinoConnect.setCallback(this);
            ...
          mArduinoConnect.showDialog();                                         // Show dialog bluetooth list to connect
          mArduinoConnect.sendMessage("Hello");                                 // Send string to arduino
      }
      
      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
            ...
          if(mArduinoConnect!=null)                                             // Must added to activity
              mArduinoConnect.onActivityResult(requestCode, resultCode, data);
      }
      
      @Override
      protected void onDestroy() {
          super.onDestroy();
            ...
          if(mArduinoConnect!=null)                                             // Recommended to add, to reduce memory leak
              mArduinoConnect.disconnected();                                   // Disconnecting Android and Arduino
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
      public void onSerialTextReceived(String text) {                           // Function to receive Serial text from Arduino
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
  
```

## Todo :
1. Show section paired and unpaired
2. Still imagine

License
====
```
The MIT License (MIT)

Copyright (c) 2016 Muhammad Abdusy Syukur

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

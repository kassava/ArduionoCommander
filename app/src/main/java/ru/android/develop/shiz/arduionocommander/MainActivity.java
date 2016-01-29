package ru.android.develop.shiz.arduionocommander;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;

/**
 * Created by ultra on 29.01.2016.
 */
public class MainActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener{

    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;

    private TextView led1TextView, led2TextView, led3TextView;
    private Switch led1Switch, led2Switch;
    private Button sendButton;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                switch (data) {
                    case "1ON":
                        led1TextView.setText("Ok!");
                        break;
                    case "1OFF":
                        led1TextView.setText("Not Ok!");
                        break;
                    case "2ON":
                        led2TextView.setText("Ok!");
                        break;
                    case "2OFF":
                        led2TextView.setText("Not Ok!");
                        break;
                    case "3ON":
                        led3TextView.setText("Ok!");
                        break;
                    case "3OFF":
                        led3TextView.setText("Not Ok!");
                        break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            }
//            else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
//                onClickStart(startButton);
//            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
//                onClickStop(stopButton);
//
//            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        led1Switch = (Switch) findViewById(R.id.switch1);
        led2Switch = (Switch) findViewById(R.id.switch2);
        led1TextView = (TextView) findViewById(R.id.textView);
        led2TextView = (TextView) findViewById(R.id.textView2);
        led3TextView = (TextView) findViewById(R.id.textView4);
        led1Switch.setOnCheckedChangeListener(this);
        led2Switch.setOnCheckedChangeListener(this);

        setUiEnabled(false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Set switches enabled, when connection is opened.
     * @param bool
     */
    public void setUiEnabled(boolean bool) {
        led1Switch.setEnabled(!bool);
        led2Switch.setEnabled(!bool);
    }

    /**
     * 1ON - turn LED #1
     * 1OFF - turn off LED #1
     * 2ON - turn LED #2
     * 2OFF - turn off LED #2
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String sendStr = null;
        switch(buttonView.getId()) {
            case R.id.switch1:
                if (isChecked) {
                    led1TextView.setText("1ON"); // test
                    led3TextView.setText("wait1"); // test
                    sendStr = "1ON";
//                    serialPort.write(sendStr.getBytes());
                } else {
                    led1TextView.setText("1OFF"); // test
                    led3TextView.setText("wait11"); // test
                    sendStr = "1OFF";
//                    serialPort.write(sendStr.getBytes());
                }
                break;
            case R.id.switch2:
                if (isChecked) {
                    led2TextView.setText("2ON"); // test
                    led3TextView.setText("wait2"); // test
                    sendStr = "2ON";
//                    serialPort.write(sendStr.getBytes());
                } else {
                    led2TextView.setText("2OFF"); // test
                    led3TextView.setText("wait22"); // test
                    sendStr = "2OFF";
//                    serialPort.write(sendStr.getBytes());
                }
                break;
            default:
                break;
        }
    }
}
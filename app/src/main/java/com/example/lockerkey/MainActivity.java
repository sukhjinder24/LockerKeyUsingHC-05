package com.example.lockerkey;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter myBluetooth = null;
    Button btnConnect;
    ListView deviceList;
    private String deviceToken = null;
    private String OwnerName = null;
    private String LockerNumber = null;
    private TextView profileTxt,connectingMsg;
    private BluetoothDevice bluetoothDevice = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnect = findViewById(R.id.connect);
        deviceList = findViewById(R.id.devices);
        profileTxt = findViewById(R.id.profile);
        connectingMsg = findViewById(R.id.connecting);
        connectingMsg.setVisibility(View.INVISIBLE);
        getSharedPref();
        connect();
    }
    public void connect() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        } else {
            if (myBluetooth.isEnabled()) {
            } else {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            connectingMsg.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, Control.class);
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            intent.putExtra("EXTRA_ADDRESS", address);
            intent.putExtra("token", deviceToken);
            startActivity(intent);
        }
    };
    public void pairedList(View view) {
        Set pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if (pairedDevices.size() > 0) {
            for (Object bluetoothDevice1 : pairedDevices) {
                bluetoothDevice = (BluetoothDevice) bluetoothDevice1;
                list.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);
    }
    public void addProfile(View view) {
        Intent intent = new Intent(MainActivity.this,AddProfile.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSharedPref(){
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = Objects.requireNonNull(getSharedPreferences("MySharedPref", MODE_APPEND));
        deviceToken = (String) sharedPreferences.getAll().get("deviceToken");
        OwnerName = (String) sharedPreferences.getAll().get("OwnerName");
        LockerNumber = (String) sharedPreferences.getAll().get("OwnerLockerNumber");
        if(OwnerName!=null) {
            profileTxt.setText(LockerNumber);
        }
        else {
            profileTxt.setText(" ");
        }
    }
}
package com.example.lockerkey;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Control extends AppCompatActivity {
    String writeMessage = null;
    private TextView recDataView, OtpTime;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    boolean ConnectSuccess;
    String address = null;
    String deviceToken = null;
    private final boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected void onCreate(Bundle savedInstanceState)
    {
        LinearLayout connected;
        ConstraintLayout failed;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        connected = findViewById(R.id.connected);
        failed = findViewById(R.id.failed);
        recDataView = findViewById(R.id.recData);
        address = (String) getIntent().getSerializableExtra("EXTRA_ADDRESS");
        deviceToken = (String) getIntent().getSerializableExtra("token");
        OtpTime = findViewById(R.id.OtpTime);
        OtpTime.setVisibility(View.INVISIBLE);
        doInBackground();
        if(ConnectSuccess){
            connected.setVisibility(View.VISIBLE);
            failed.setVisibility(View.INVISIBLE);

        }else{
            connected.setVisibility(View.INVISIBLE);
            failed.setVisibility(View.VISIBLE);
            msg("Connection Failed");
        }
    }
    private void doInBackground() {
        ConnectSuccess = true;
        try
        {
            if (btSocket == null || !isBtConnected)
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();
            }
        }
        catch (IOException e)
        {
            ConnectSuccess = false;
            msg("Device Connection Failed");
        }
    }
    @SuppressLint("SetTextI18n")
    public void requestOTP(View view) {

        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(deviceToken.toString().getBytes());

                InputStream inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());
                writeMessage = "";
                for(int i = 0;i < 4; i++){

                    byte b = (byte) inputStream.read();
                    System.out.println((char) b );
                    writeMessage = writeMessage + (char) b;
                }


                if(writeMessage.matches("0000")){
                    recDataView.setText("Unauthorised Device Please contact Manager");
                }
                else {
                    recDataView.setText(writeMessage);
                    OtpTime.setVisibility(View.VISIBLE);
                }

            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }
    public void retryFn(View view) {
        Intent intent = new Intent(Control.this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Control.this,MainActivity.class);
        try {
            btSocket.close();
            startActivity(intent);
        } catch (IOException ignored) {
        }
    }
}
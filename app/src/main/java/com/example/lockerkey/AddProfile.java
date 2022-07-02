package com.example.lockerkey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.widget.Toast.LENGTH_LONG;

public class AddProfile extends AppCompatActivity {
    private EditText tokenEntered ,tokenReEntered ,lockerNumber;
    private String tokenEntStr,tokenReEntStr ,lockerNumberStr;
    private EditText ownerName;
    private String OwnerNameString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_profile);
        tokenEntered = findViewById(R.id.tokenEntered);
        tokenReEntered = findViewById(R.id.tokenReEntered);
        ownerName = findViewById(R.id.owner);
        lockerNumber = findViewById(R.id.lockerNumber);

    }

    public void submission(View view) {
        tokenEntStr = tokenEntered.getText().toString();
        tokenReEntStr = tokenReEntered.getText().toString();
        OwnerNameString = ownerName.getText().toString();
        lockerNumberStr = lockerNumber.getText().toString();
        if(tokenEntStr.matches(tokenReEntStr) ){
            if(tokenEntStr.isEmpty() || OwnerNameString.isEmpty() || lockerNumberStr.isEmpty()){
                Toast toast = Toast.makeText(AddProfile.this,"Invalid Input" , LENGTH_LONG);
                toast.show();
            }
            else {
                Intent intent = new Intent(AddProfile.this, MainActivity.class);
                updateSharedPref();
                startActivity(intent);
            }
        }

        else {
            Toast toast = Toast.makeText(AddProfile.this,"Input Mismatch" , LENGTH_LONG);
            toast.show();
        }
    }

    private void updateSharedPref() {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("deviceToken", tokenEntStr);
        editor.putString("OwnerName",OwnerNameString);
        editor.putString("OwnerLockerNumber",lockerNumberStr);
        editor.apply();
    }
}
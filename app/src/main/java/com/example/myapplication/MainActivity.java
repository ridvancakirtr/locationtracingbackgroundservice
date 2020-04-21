package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.UUID;

import static com.example.myapplication.R.id.activity_main_id;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "exampleServiceChannel";
    public static String IMEI, SERIAL, MODEL;
    public static String TAG = MainActivity.class.getSimpleName();
    public final int REQUESTCODE=1000;
    RelativeLayout relativeLayout;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        relativeLayout = (RelativeLayout) findViewById(activity_main_id);
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT>=28) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) +
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                ){
                    ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                    },REQUESTCODE);
                }else{
                    ActivityCompat.requestPermissions(this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                    },REQUESTCODE);
                }
            } else {
                Log.d("TAG", "İZİNLER VERİLİ");
                startService();
            }
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },REQUESTCODE);
            } else {
                Log.d("TAG", "İZİNLER VERİLİ");
                startService();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUESTCODE){
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.P) {
                if(grantResults.length>0 && (grantResults[0]+grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                    Log.d("TAG", "İZİN VERİLDİ");
                    message("İZİN VERİLDİ");
                    startService();
                }else{
                    message("İzinler Verilmedi");
                    Log.d("TAG", "İzinler Verilmedi");
                }
            }else{
                if(grantResults.length>0 && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    Log.d("TAG", "İZİN VERİLDİ");
                    message("İZİN VERİLDİ");
                    startService();
                }else{
                    message("İzinler Verilmedi");
                    Log.d("TAG", "İzinler Verilmedi");
                }
            }

        }
    }

    @SuppressLint("HardwareIds")
    public void getIMEISerial(){
        telephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        boolean aa=(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        MODEL=Build.BRAND+" "+Build.MODEL;
        if (aa) {
            if (Build.VERSION.SDK_INT >= 26 && Build.VERSION.SDK_INT <= 28) {
                Log.d(TAG, "11---------");
                IMEI=telephonyManager.getImei();
                SERIAL=Build.getSerial();

            }else if(Build.VERSION.SDK_INT < 26){
                Log.d(TAG, "22---------");
                SERIAL=Build.SERIAL;
                IMEI=telephonyManager.getDeviceId();
            }else{
                Log.d(TAG, "33---------");
                SERIAL= UUID.randomUUID().toString();
                IMEI=Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.d(TAG, "33--------SERIAL -"+SERIAL);
                Log.d(TAG, "33--------IMEI -"+IMEI);
            }
        }
    }

    public void startService() {
        getIMEISerial();
        Log.d("DENEME", "service girdi");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(manager).createNotificationChannel(serviceChannel);

            Intent intent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, intent);
        }else {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }
    }

    public void message(String message){
        Snackbar snackbar = Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}

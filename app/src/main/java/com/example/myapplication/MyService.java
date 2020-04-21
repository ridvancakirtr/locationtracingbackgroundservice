package com.example.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.API.JSONPlaceHolderAPI;
import com.example.myapplication.API.Post;
import com.example.myapplication.API.RetrofitSetting;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.MainActivity.CHANNEL_ID;
import static com.example.myapplication.MainActivity.IMEI;
import static com.example.myapplication.MainActivity.MODEL;
import static com.example.myapplication.MainActivity.SERIAL;

public class MyService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        createLocationRequest();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = notification("LOCATION TRACING SYSTEM", "Lokasyon Bilgisi Gönderiliyor.", pendingIntent);
        startForeground(1, notification);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "" + location.getLongitude());
                    Log.d(TAG, IMEI + "--" + SERIAL + "---" + MODEL);
                    postData(IMEI, SERIAL, MODEL, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));

                }
            }
        };
        startLocationUpdates();
        return START_NOT_STICKY;
    }

    private void postData(String imei, String serial, String model, String latitude, String longitude) {
        JSONPlaceHolderAPI jsonPlaceHolderAPI = RetrofitSetting.get().create(JSONPlaceHolderAPI.class);
        Call<Post> call = jsonPlaceHolderAPI.createPost(new Post(imei, serial, model, latitude, longitude));
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.d(TAG, "CALL : " + call.request());
                Log.d(TAG, "CALL : " + response.isSuccessful());
                if (response.isSuccessful()) {
                    Log.d(TAG, "CALL : KAYIT BAŞARILI");
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.d(TAG, "HATA" + call.request() + "----" + t.getMessage());
            }
        });
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private Notification notification(String title, String text, PendingIntent pendingIntent) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_location)
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }
}

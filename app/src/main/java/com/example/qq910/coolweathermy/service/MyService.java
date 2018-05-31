package com.example.qq910.coolweathermy.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.qq910.coolweathermy.gson.Weather;
import com.example.qq910.coolweathermy.util.HttpUtil;
import com.example.qq910.coolweathermy.util.Utillity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int time = 60 * 60 * 1000;
        long clockTime = SystemClock.elapsedRealtime() + time;
        Intent inten = new Intent(this, MyService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, inten, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, clockTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyService.this);
        String weatherStr = sharedPreferences.getString("weather", null);
        if (weatherStr != null) {
            Weather weather = Utillity.handleWeatherResponse(weatherStr);
            final String weatherId = weather.basic.weatherId;
            String address = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=f44246d7b5c24e8dbcec889e49eed3b8";
            HttpUtil.sendOkhttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String stringResponse = response.body().string();
                    Weather weather = Utillity.handleWeatherResponse(stringResponse);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(MyService.this).edit();
                        edit.putString("weather", stringResponse);
                        edit.apply();
                    }

                }
            });
        } else {
            Toast.makeText(this, "MyService: 数据空", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateBingPic() {
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(MyService.this).edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();
            }
        });

    }
}

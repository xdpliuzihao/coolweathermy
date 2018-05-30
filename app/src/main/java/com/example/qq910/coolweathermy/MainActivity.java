package com.example.qq910.coolweathermy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = sharedPreferences.getString("weather", null);
        if (weather != null) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("weather",weather);
            startActivity(intent);
            finish();
        }

    }
}

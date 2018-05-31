package com.example.qq910.coolweathermy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qq910.coolweathermy.gson.Forecast;
import com.example.qq910.coolweathermy.gson.Weather;
import com.example.qq910.coolweathermy.service.MyService;
import com.example.qq910.coolweathermy.util.HttpUtil;
import com.example.qq910.coolweathermy.util.Utillity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private List<Forecast> dataLists = new ArrayList<>();

    private ScrollView mWeatherLayout;
    private TextView mTitleText;
    private TextView mTitleTime;
    private TextView mTvTmp;
    private TextView mTvCondtext;
    private LinearLayout mForecastLayout;
    private TextView mTvAqi;
    private TextView mTvPm25;
    private TextView mTvComf;
    private TextView mTvCw;
    private TextView mTvSport;

    private TextView mTvItemtext1;
    private TextView mTvItemtext2;
    private TextView mTvItemtext3;
    private TextView mTvItemtext4;
    private ImageView mIvBackGround;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private String mWeather_id;
    public DrawerLayout mDrawerLayout;
    private Button mButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initData();

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }


    private void initView() {
        mWeatherLayout = findViewById(R.id.weather_layout);
        mTitleText = findViewById(R.id.title_text);
        mTitleTime = findViewById(R.id.title_time);
        mTvTmp = findViewById(R.id.tv_tmp);
        mTvCondtext = findViewById(R.id.tv_condtext);
        mForecastLayout = findViewById(R.id.forecast_layout);
        mTvAqi = findViewById(R.id.tv_aqi);
        mTvPm25 = findViewById(R.id.tv_pm25);
        mTvComf = findViewById(R.id.tv_comf);
        mTvCw = findViewById(R.id.tv_cw);
        mTvSport = findViewById(R.id.tv_sport);
        mIvBackGround = findViewById(R.id.iv_background);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);//////////////////
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);///////
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mButtonBack = findViewById(R.id.btn_back);
    }

    private void initData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherResponse = sharedPreferences.getString("weather", null);
        if (weatherResponse != null) {
            Weather weather = Utillity.handleWeatherResponse(weatherResponse);
            mWeather_id = weather.basic.weatherId;
            showResponseWeather(weather);
        } else {
            mWeather_id = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeather_id);
        }

        String bingPic = sharedPreferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mIvBackGround);
        } else {
            loadBingPic();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeather_id);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    private void loadBingPic() {
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                if (bingPic != null) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    edit.putString("bing_pic", bingPic);
                    edit.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(mIvBackGround);
                        }
                    });
                }
            }
        });

    }

    public void requestWeather(final String weatherId) {
        String address = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=f44246d7b5c24e8dbcec889e49eed3b8";
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                final Weather weather = Utillity.handleWeatherResponse(responseStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", responseStr);
                            edit.apply();
                            mWeather_id = weather.basic.weatherId;
                            showResponseWeather(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
    }

    private void showResponseWeather(Weather weather) {
        String cityName = weather.basic.cityName;
        String loc = weather.basic.mUpdate.loc.split(" ")[1];
        String tmp = weather.now.tmp;
        String info = weather.now.cond.info;
        mTitleText.setText(cityName);
        mTitleTime.setText(loc);
        mTvTmp.setText(tmp);
        mTvCondtext.setText(info);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.mForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            mTvItemtext1 = view.findViewById(R.id.tv_itemtext1);
            mTvItemtext2 = view.findViewById(R.id.tv_itemtext2);
            mTvItemtext3 = view.findViewById(R.id.tv_itemtext3);
            mTvItemtext4 = view.findViewById(R.id.tv_itemtext4);
            mTvItemtext1.setText(forecast.date);
            mTvItemtext2.setText(forecast.cond.info);
            mTvItemtext3.setText(forecast.tmp.max);
            mTvItemtext4.setText(forecast.tmp.min);
            mForecastLayout.addView(view);
        }
        mTvAqi.setText(weather.aqi.mCity.aqi);
        mTvPm25.setText(weather.aqi.mCity.pm25);
        String sugInfo1 = "舒适度: " + weather.suggestion.comf.info;
        String sugInfo2 = "洗车指数: " + weather.suggestion.cw.info;
        String sugInfo3 = "运动指数: " + weather.suggestion.sport.info;
        mTvComf.setText(sugInfo1);
        mTvCw.setText(sugInfo2);
        mTvSport.setText(sugInfo3);
        mWeatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        
    }
}

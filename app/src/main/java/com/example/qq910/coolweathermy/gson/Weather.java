package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by qq910 on 2018/5/28.
 */

public class Weather {
    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> mForecastList;
    public Now now;
    public Suggestion suggestion;
    public String status;
}

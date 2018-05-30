package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qq910 on 2018/5/28.
 */

public class AQI {
    @SerializedName("city")
    public City mCity;

    public class City {
        public String aqi;
        public String pm25;
    }
}

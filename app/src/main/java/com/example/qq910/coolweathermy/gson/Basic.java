package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qq910 on 2018/5/28.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update mUpdate;

    public class Update {
        public String loc;
    }
}

package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qq910 on 2018/5/28.
 */

public class Forecast {
    public String date;

    public Cond cond;

    public class Cond {
        @SerializedName("txt_d")
        public String info;
    }

    public Tmp tmp;
    public class Tmp {
        public String max;
        public String min;
    }
}

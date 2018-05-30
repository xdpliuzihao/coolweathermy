package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qq910 on 2018/5/28.
 */

public class Now {
    public Cond cond;
    public class Cond{
        @SerializedName("txt")
        public String info;
    }

    //当前温度
    public String tmp;
}

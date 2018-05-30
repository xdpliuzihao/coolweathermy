package com.example.qq910.coolweathermy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qq910 on 2018/5/28.
 */

public class Suggestion {

    public Cmf comf;

    public class Cmf {
        @SerializedName("txt")
        public String info;
    }

    public Cw cw;

    public class Cw {
        @SerializedName("txt")
        public String info;
    }

    public Sport sport;

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

}


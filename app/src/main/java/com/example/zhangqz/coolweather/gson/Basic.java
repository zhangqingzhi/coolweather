package com.example.zhangqz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangqz on 2017/7/28.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
    public Update update;
}

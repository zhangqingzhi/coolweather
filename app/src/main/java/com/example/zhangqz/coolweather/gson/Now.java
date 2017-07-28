package com.example.zhangqz.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangqz on 2017/7/28.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    public class More {
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("cond")
    public More more;
}

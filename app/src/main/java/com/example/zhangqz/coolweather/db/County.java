package com.example.zhangqz.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class County extends DataSupport {
    private int id; // 这个是主键id ，不能改名，如使用 mId 则相当于多了一个列 “mid”
    private String mName;
    private String mWeatherId;
    private int mCityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(String weatherId) {
        mWeatherId = weatherId;
    }

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }
}

package com.example.zhangqz.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class County extends DataSupport {
    private int mId;
    private int mName;
    private int mWeatherId;
    private int mCityId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int weatherId) {
        mWeatherId = weatherId;
    }

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }
}

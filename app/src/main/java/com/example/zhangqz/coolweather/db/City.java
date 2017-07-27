package com.example.zhangqz.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class City extends DataSupport {
    private int mId;
    private String mName;
    private int mCode;
    private int mProvinceId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getProvinceId() {
        return mProvinceId;
    }

    public void setProvinceId(int provinceId) {
        mProvinceId = provinceId;
    }
}

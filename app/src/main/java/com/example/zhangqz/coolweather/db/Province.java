package com.example.zhangqz.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class Province extends DataSupport {
    private int id; // 这个是主键id ，不能改名，如使用 mId 则相当于多了一个列 “mid”
    private String mName;
    private int mCode;

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

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }
}

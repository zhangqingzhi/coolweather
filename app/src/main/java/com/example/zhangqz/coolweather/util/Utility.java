package com.example.zhangqz.coolweather.util;

import android.text.TextUtils;

import com.example.zhangqz.coolweather.db.City;
import com.example.zhangqz.coolweather.db.County;
import com.example.zhangqz.coolweather.db.Province;
import com.example.zhangqz.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class Utility {

    // Handle province info from server response
    public static boolean handleProvinceResponse(String response) {
        return handleResponse(response, new OnJsonAnalysis() {
            @Override
            public void onAnalysis(JSONObject jsonObject) throws JSONException {
                Province province = new Province();
                province.setName(jsonObject.getString("name"));
                province.setCode(jsonObject.getInt("id"));
                province.save();
            }
        });
    }


    // Handle city info from server response
    public static boolean handleCityResponse(String response, final int provinceId) {
        return handleResponse(response, new OnJsonAnalysis() {
            @Override
            public void onAnalysis(JSONObject jsonObject) throws JSONException {
                City city = new City();
                city.setName(jsonObject.getString("name"));
                city.setCode(jsonObject.getInt("id"));
                city.setProvinceId(provinceId);
                city.save();
            }
        });
    }

    // Handle county info from server response
    public static boolean handleCountyResponse(String response, final int cityId) {
        return handleResponse(response, new OnJsonAnalysis() {
            @Override
            public void onAnalysis(JSONObject jsonObject) throws JSONException {
                County county = new County();
                county.setName(jsonObject.getString("name"));
                county.setWeatherId(jsonObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
        });
    }

    // Handle server response
    private static boolean handleResponse(String response, OnJsonAnalysis onJsonAnalysis) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allObjects = new JSONArray(response);
                for (int i = 0; i < allObjects.length(); i++) {
                    JSONObject jsonObject = allObjects.getJSONObject(i);
                    onJsonAnalysis.onAnalysis(jsonObject);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private interface OnJsonAnalysis{
        void onAnalysis(JSONObject jsonObject) throws JSONException;
    }

    public static Weather handleWeatherRespone(String respone) {
        try {
            JSONObject jsonObject = new JSONObject(respone);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  null;
    }
}

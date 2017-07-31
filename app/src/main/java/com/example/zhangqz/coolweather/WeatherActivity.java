package com.example.zhangqz.coolweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.example.zhangqz.coolweather.gson.Forecast;
import com.example.zhangqz.coolweather.gson.Weather;
import com.example.zhangqz.coolweather.util.HttpUtil;
import com.example.zhangqz.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class WeatherActivity extends AppCompatActivity {
    public static final String TAG = WeatherActivity.class.getSimpleName();
    private static final String EXTRA_KEY_WEATHER_ID = "weather_id";
    private static final String EXTRA_KEY_IMAGE_BACKGROUND = "image_background";
    private static final String REQUEST_WEATHER_KEY = "6d0e912f0c1e46dc98c131d48810ce2c";

    private DrawerLayout mDrawerLayout;
    private Button mBtnNav;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView mImgBackground;
    private ScrollView mWeatherLayout;
    private TextView mTvTitleCity;
    private TextView mTvTitleUpdateTime;
    private TextView mTvDegree;
    private TextView mTvWeatherInfo;

    private LinearLayout mForecastLayout;
    private TextView mTvAqi;
    private TextView mTvPm25;
    private TextView mTvComfort;
    private TextView mTvCarWash;
    private TextView mTvSport;

    private String mWeatherId;

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public static void actionStart(Activity currentAcivity, String weatherId) {
        Intent intent = new Intent(currentAcivity, WeatherActivity.class);
        intent.putExtra(EXTRA_KEY_WEATHER_ID, weatherId);
        currentAcivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();

        loadPrefsData();
    }

    private void initView() {

        mDrawerLayout   = (DrawerLayout) findViewById(R.id.drawer_layout);
        mBtnNav         = (Button) findViewById(R.id.btn_nav);
        mBtnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mImgBackground  = (ImageView) findViewById(R.id.img_weather_bg);

        mWeatherLayout  = (ScrollView) findViewById(R.id.weather_layout);
        mTvTitleCity    = (TextView) findViewById(R.id.tv_title);
        mTvTitleUpdateTime = (TextView) findViewById(R.id.tv_title_update_time);
        mTvDegree       = (TextView) findViewById(R.id.tv_degree);
        mTvWeatherInfo  = (TextView) findViewById(R.id.tv_weather_info);

        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mTvAqi          = (TextView) findViewById(R.id.tv_aqi);
        mTvPm25         = (TextView) findViewById(R.id.tv_pm25);
        mTvComfort      = (TextView) findViewById(R.id.tv_comfort);
        mTvCarWash      = (TextView) findViewById(R.id.tv_car_wash);
        mTvSport        = (TextView) findViewById(R.id.tv_sport);
    }

    private void loadPrefsData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时，直接解析天气数据
            Weather weather = Utility.handleWeatherRespone(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 没有缓存时去服务器获取天气信息
            mWeatherId = getIntent().getStringExtra(EXTRA_KEY_WEATHER_ID);
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String imagePicture = prefs.getString(EXTRA_KEY_IMAGE_BACKGROUND, null);
        if (imagePicture != null) {
            Glide.with(this).load(imagePicture).into(mImgBackground);
        } else {
            loadImageBg();
        }
    }

    // 从 必应 网站获取图片作为背景图片
    private void loadImageBg() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request Bing picture onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("image_picture", bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mImgBackground);
                    }
                });
            }
        });

    }

    // 根据 ID 来获取天气信息
    protected void requestWeather(final String weatherId) {
        mWeatherId = weatherId;

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + REQUEST_WEATHER_KEY;
        Log.e(TAG, "requestWeather: " + weatherUrl);

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_LONG).show();
                        mRefreshLayout.setRefreshing(false);
                        finish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherRespone(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);

                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();

                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        });

        loadImageBg();
    }

    private void showWeatherInfo(Weather weather) {
        mTvTitleCity.setText(weather.basic.cityName);
        mTvTitleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        mTvDegree.setText(weather.now.temperature + " ℃");
        mTvWeatherInfo.setText(weather.now.more.info);

        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
            TextView tvMax = (TextView) view.findViewById(R.id.tv_max);
            TextView tvMin = (TextView) view.findViewById(R.id.tv_min);
            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.more.info);
            tvMax.setText(forecast.temperature.max);
            tvMin.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            mTvAqi.setText(weather.aqi.city.aqi);
            mTvPm25.setText(weather.aqi.city.pm25);
        }

        mTvSport.setText("运动建议: " + weather.suggestion.sport.info);
        mTvCarWash.setText("洗车指数: " + weather.suggestion.carWash.info);
        mTvComfort.setText("舒适度: " + weather.suggestion.comfort.info);

        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}

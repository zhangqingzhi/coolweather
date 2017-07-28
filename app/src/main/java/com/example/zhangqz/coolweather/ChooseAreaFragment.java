package com.example.zhangqz.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangqz.coolweather.db.City;
import com.example.zhangqz.coolweather.db.County;
import com.example.zhangqz.coolweather.db.Province;
import com.example.zhangqz.coolweather.util.HttpUtil;
import com.example.zhangqz.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhangqz on 2017/7/27.
 */

public class ChooseAreaFragment extends Fragment {
    public static final String TAG = ChooseAreaFragment.class.getSimpleName();

    public static final int LEVE_PROVINCE = 0;
    public static final int LEVE_CITY = 1;
    public static final int LEVE_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTvTitle;
    private Button mBtnBack;
    private ListView mLvAddress;
    private ArrayAdapter<String> mAdapter;
    private List<String> mDataList = new ArrayList<>();

    private List<Province> mProvinces;
    private List<City> mCities;
    private List<County> mCounties;

    private Province mSelectedProvince;
    private City mSelectedCity;

    private int mCurrentLevel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);

        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mBtnBack = (Button) view.findViewById(R.id.btn_back);

        mLvAddress = (ListView) view.findViewById(R.id.lv_address);
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mDataList);
        mLvAddress.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVE_PROVINCE) {
                    mSelectedProvince = mProvinces.get(position);
                    queryCities();
                } else if (mCurrentLevel == LEVE_CITY) {
                    mSelectedCity = mCities.get(position);
                    queryCounties();
                } else if (mCurrentLevel == LEVE_COUNTY) {
                    String weatherId = mCounties.get(position).getWeatherId();
                    WeatherActivity.actionStart(getActivity(), weatherId);
                    //getActivity().finish();
                }

            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVE_COUNTY) {
                    queryCities();
                } else if (mCurrentLevel == LEVE_CITY) {
                    queryProvincies();
                }
            }
        });

        queryProvincies();
    }

    private void queryProvincies() {
        mTvTitle.setText("中国");
        mBtnBack.setVisibility(View.GONE);

        mProvinces = DataSupport.findAll(Province.class);
        if (mProvinces.size() > 0) {
            mDataList.clear();
            for (Province province : mProvinces) {
                mDataList.add(province.getName());
            }

            mAdapter.notifyDataSetChanged();
            mLvAddress.setSelection(0);
            mCurrentLevel = LEVE_PROVINCE;

        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        mTvTitle.setText(mSelectedProvince.getName());
        mBtnBack.setVisibility(View.VISIBLE);

        mCities = DataSupport.where("mProvinceId = ?", String.valueOf(mSelectedProvince.getId()))
                .find(City.class);
        if (mCities.size() > 0) {
            mDataList.clear();
            for(City city : mCities) {
                mDataList.add(city.getName());
            }

            mLvAddress.setSelection(0);
            mCurrentLevel = LEVE_CITY;
            mAdapter.notifyDataSetChanged();
        } else {
            String address = "http://guolin.tech/api/china/" + mSelectedProvince.getCode();
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        mTvTitle.setText(mSelectedCity.getName());
        mBtnBack.setVisibility(View.VISIBLE);

        mCounties = DataSupport.where("mCityId = ?", String.valueOf(mSelectedCity.getId()))
                .find(County.class);
        if (mCounties.size() > 0) {
            mDataList.clear();
            for (County county : mCounties) {
                mDataList.add(county.getName());
            }

            mLvAddress.setSelection(0);
            mCurrentLevel = LEVE_COUNTY;
            mAdapter.notifyDataSetChanged();
        } else {
            String address = "http://guolin.tech/api/china/"
                    + mSelectedProvince.getCode() + "/" + mSelectedCity.getCode();
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "sendOkHttpRequest fail! Exception: " + e.getMessage());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "Load fail!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result = false;
                String responseText = response.body().string();
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, mSelectedCity.getId());
                }

                if (!result) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            queryProvincies();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading ...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}

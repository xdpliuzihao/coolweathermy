package com.example.qq910.coolweathermy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq910.coolweathermy.db.City;
import com.example.qq910.coolweathermy.db.County;
import com.example.qq910.coolweathermy.db.Province;
import com.example.qq910.coolweathermy.util.HttpUtil;
import com.example.qq910.coolweathermy.util.Utillity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by qq910 on 2018/5/26.
 */

public class ChooseFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListView;
    private List<String> dataList = new ArrayList<>();
    private int currentLevel;//当前级别
    //    private List<Province> mProvinceList;
        private List<Province> mProvinceList = new ArrayList<>();     ///////////////////试试把上面的换成这里的
//    private List<City> mCityList;
    private List<City> mCityList = new ArrayList<>();///////////////////////////
    //    private List<County> mCountyList;
    private List<County> mCountyList = new ArrayList<>();///////////////////////////
    private Province mSelectedProvince;//选中的省份
    private City mSelectedCity;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog mProgressDialog;
    private County mSelectedCounty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTitleText = view.findViewById(R.id.title_text);
        mBackButton = view.findViewById(R.id.back_button);
        mListView = view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    mSelectedProvince = mProvinceList.get(position);
                    questCity();
                } else if (currentLevel == LEVEL_CITY) {
                    mSelectedCity = mCityList.get(position);
                    questCounty();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = mCountyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();

                }

            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    questCity();
                } else if (currentLevel == LEVEL_CITY) {
                    questProvince();
                }
            }
        });

        questProvince();
    }

    private void questProvince() {
        mTitleText.setText("China");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            questFormService(address, "province");
        }
    }


    private void questCity() {
        mTitleText.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceId = ?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0) {
            dataList.clear();
            for (City city : mCityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            questFormService(address, "city");
        }

    }

    private void questCounty() {
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityId = ?", String.valueOf(mSelectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0) {
            dataList.clear();
            for (County county : mCountyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        } else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            int cityCode = mSelectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            questFormService(address, "county");
        }
    }

    private void questFormService(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {

            private boolean mResult = false;

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsrText = response.body().string();
                if ("province".equals(type)) {
                    mResult = Utillity.handleProvinceResponse(responsrText);
                } else if ("city".equals(type)) {
                    mResult = Utillity.handleCityResponse(responsrText, mSelectedProvince.getId());
                } else if ("county".equals(type)) {
                    mResult = Utillity.handleCountyResponse(responsrText, mSelectedCity.getId());
                }

                if (mResult) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                questProvince();
                            } else if ("city".equals(type)) {
                                questCity();
                            } else if ("county".equals(type)) {
                                questCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载中");
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

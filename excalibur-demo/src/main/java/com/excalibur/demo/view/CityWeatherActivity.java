package com.excalibur.demo.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.excalibur.core.data.Restore;
import com.excalibur.core.view.adapter.SimpleListAdapter;
import com.excalibur.core.view.inject.InjectView;
import com.excalibur.core.view.inject.ViewInjectUtils;
import com.excalibur.frame.base.Request;
import com.excalibur.demo.R;
import com.excalibur.demo.biz.base.BundleKey;
import com.excalibur.demo.biz.model.City;
import com.excalibur.demo.biz.model.WeatherDetail;
import com.excalibur.demo.biz.operation.GetWeatherFutureOperation;
import com.excalibur.demo.view.base.BaseActivity;

import java.util.List;

/**
 * @version 14-5-22
 */
public class CityWeatherActivity extends BaseActivity {
    @InjectView(id = R.id.tv_weather_city)
    private TextView       mTvWeatherCity;
    @InjectView(id = R.id.lv_weather_detail)
    private ListView       mLvWeatherDetail;
    private WeatherAdapter mWeatherAdapter;
    @Restore(BundleKey.CITY)
    private City           city;

    @Override
    protected void onBaseCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weather);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mWeatherAdapter = new WeatherAdapter(this, null);
        mLvWeatherDetail.setAdapter(mWeatherAdapter);
        if (city != null) {
            mTvWeatherCity.setText(city.getCityName() + " 5天天气");
            sendRequest(GetWeatherFutureOperation.createRequest(city.getWeatherId()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRequestSuccess(Request request, Bundle resultData) {
        super.onRequestSuccess(request, resultData);
        Class clazz = request.getOperationClass();
        if (GetWeatherFutureOperation.class == clazz) {
            mWeatherAdapter.setData((List<WeatherDetail>) resultData.getSerializable(BundleKey.WEATHERS));
            mWeatherAdapter.notifyDataSetChanged();
        }
    }

    private static class WeatherAdapter extends SimpleListAdapter<WeatherDetail> {

        public WeatherAdapter(Context context, List<WeatherDetail> data) {
            super(context, data);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = getInflater().inflate(R.layout.list_item_weather_day, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.populateView(getItem(i));
            return view;
        }
    }

    private static class ViewHolder {
        @InjectView(id = R.id.tv_day_weather)
        private TextView weather;
        @InjectView(id = R.id.tv_day_temperature)
        private TextView temperature;
        @InjectView(id = R.id.tv_day_date)
        private TextView days;

        private ViewHolder(View view) {
            ViewInjectUtils.inject(view, this);
        }

        private void populateView(WeatherDetail detail) {
            weather.setText(detail.getWeather());
            temperature.setText(detail.getTemperature());
            days.setText(detail.getDays());
        }
    }

}

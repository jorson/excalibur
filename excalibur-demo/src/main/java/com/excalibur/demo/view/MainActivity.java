package com.excalibur.demo.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.excalibur.core.view.adapter.SimpleListAdapter;
import com.excalibur.core.view.inject.InjectView;
import com.excalibur.core.view.inject.ViewInjectUtils;
import com.excalibur.frame.base.Request;
import com.excalibur.demo.R;
import com.excalibur.demo.biz.IntentUtil;
import com.excalibur.demo.biz.base.BundleKey;
import com.excalibur.demo.biz.model.City;
import com.excalibur.demo.biz.operation.GetCityListOperation;
import com.excalibur.demo.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示从sendRequest到获取结果并展现City列表的过程
 *
 * @version 14-5-20
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @InjectView(id = R.id.lv_main_cities)
    private ListView mLvMainCities;

    private CityListAdapter mCityListAdapter;

    @Override
    protected void onBaseCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mCityListAdapter = new CityListAdapter(this, null);
        mLvMainCities.setAdapter(mCityListAdapter);
        mLvMainCities.setOnItemClickListener(this);
        sendRequest(new Request(GetCityListOperation.class));
    }

    @Override
    public void afterRequest(Request request) {
        super.afterRequest(request);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRequestSuccess(Request request, Bundle resultData) {
        super.onRequestSuccess(request, resultData);

        Class clazz = request.getOperationClass();
        if (GetCityListOperation.class == clazz) {
            if (resultData != null && resultData.containsKey(BundleKey.CITY_LIST)) {
                List<City> list = (ArrayList<City>) resultData.getSerializable(BundleKey.CITY_LIST);
                mCityListAdapter.setData(list);
                mCityListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int realIndex = position - mLvMainCities.getHeaderViewsCount();
        City city = mCityListAdapter.getItem(realIndex);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKey.CITY, city);
        IntentUtil.startActivity(this, CityWeatherActivity.class, bundle);
    }

    private static class CityListAdapter extends SimpleListAdapter<City> {

        public CityListAdapter(Context context, List<City> data) {
            super(context, data);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = getInflater().inflate(R.layout.list_item_city, null);
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

        @InjectView(id = R.id.tv_city_name)
        private TextView cityName;

        public ViewHolder(View view) {
            ViewInjectUtils.inject(view, this);
        }

        public void populateView(City data) {
            cityName.setText(data.getCityName());
        }

    }

}

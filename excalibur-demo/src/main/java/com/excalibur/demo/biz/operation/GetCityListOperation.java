package com.excalibur.demo.biz.operation;

import static com.nd.hy.android.hermes.demo.biz.base.Protocol.*;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.DATA_FORMAT;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.WEATHER_API_URL;

import android.content.Context;
import android.os.Bundle;
import com.nd.hy.android.core.base.HermesException;
import com.nd.hy.android.core.net.http.HttpRequest;
import com.nd.hy.android.core.net.http.base.ConnectionResult;
import com.nd.hy.android.frame.base.Operation;
import com.nd.hy.android.frame.base.Request;
import com.nd.hy.android.hermes.demo.biz.base.BundleKey;
import com.nd.hy.android.hermes.demo.biz.model.City;
import com.nd.hy.android.hermes.demo.biz.model.WeatherCityEntry;

import java.util.ArrayList;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class GetCityListOperation implements Operation {
    @Override
    public Bundle execute(Context context, Request request) throws HermesException {
        ConnectionResult result = HttpRequest.get(WEATHER_API_URL)
                .addParam(Field.APP, Command.WEATHER_CITY)
                .addParam(Field.FORMAT, DATA_FORMAT)
                .send(context);
        ArrayList<City> cityList = (ArrayList<City>) WeatherCityEntry.parse(result.body).getCities();
        if (cityList != null) {
            Bundle data = new Bundle();
            data.putSerializable(BundleKey.CITY_LIST, cityList);
            return data;
        }
        return null;
    }
}

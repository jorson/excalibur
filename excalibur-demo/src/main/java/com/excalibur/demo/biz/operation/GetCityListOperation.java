package com.excalibur.demo.biz.operation;

import android.content.Context;
import android.os.Bundle;
import com.excalibur.core.base.ExcaliburException;
import com.excalibur.core.net.http.HttpRequest;
import com.excalibur.core.net.http.base.ConnectionResult;
import com.excalibur.frame.base.Operation;
import com.excalibur.frame.base.Request;
import com.excalibur.demo.biz.base.BundleKey;
import com.excalibur.demo.biz.model.City;
import com.excalibur.demo.biz.model.WeatherCityEntry;

import java.util.ArrayList;

import static com.excalibur.demo.biz.base.Protocol.*;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class GetCityListOperation implements Operation {
    @Override
    public Bundle execute(Context context, Request request) throws ExcaliburException {
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

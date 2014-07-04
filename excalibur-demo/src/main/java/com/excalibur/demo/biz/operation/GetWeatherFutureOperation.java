package com.excalibur.demo.biz.operation;

import android.content.Context;
import android.os.Bundle;
import com.excalibur.core.base.ExcaliburException;
import com.excalibur.core.net.http.HttpRequest;
import com.excalibur.core.net.http.base.ConnectionResult;
import com.excalibur.core.util.ObjectUtils;
import com.excalibur.frame.base.Operation;
import com.excalibur.frame.base.Priority;
import com.excalibur.frame.base.Request;
import com.excalibur.demo.biz.base.BundleKey;
import com.excalibur.demo.biz.model.BaseArrayEntry;
import com.excalibur.demo.biz.model.WeatherDetail;

import java.io.IOException;
import java.util.ArrayList;

import static com.excalibur.demo.biz.base.Protocol.*;

/**
 * @author Yangz
 * @version 14-5-22
 */
public class GetWeatherFutureOperation implements Operation {

    public static Request createRequest(String weaId) {
        Request request = new Request(GetWeatherFutureOperation.class);
        request.put(BundleKey.WEA_ID, weaId);
        request.setPriority(Priority.HIGH);
        request.setMemoryCacheEnabled(true);
        return request;
    }


    @Override
    public Bundle execute(Context context, Request request) throws ExcaliburException {
        String weaId = request.getString(BundleKey.WEA_ID);
        ConnectionResult result = HttpRequest.get(WEATHER_API_URL)
                .addParam(Field.APP, Command.WEATHER_FUTURE)
                .addParam(Field.WEA_ID, weaId)
                .addParam(Field.APP_KEY, APP_KEY)
                .addParam(Field.SIGN, SIGN)
                .addParam(Field.FORMAT, DATA_FORMAT)
                .send(context);
        try {
            BaseArrayEntry<WeatherDetail> entry = ObjectUtils.getMapperInstance()
                    .readValue(result.body, ObjectUtils.constructParametricType(BaseArrayEntry.class, WeatherDetail.class));
            Bundle data = new Bundle();
            data.putSerializable(BundleKey.WEATHERS, (ArrayList) entry.getResult());
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

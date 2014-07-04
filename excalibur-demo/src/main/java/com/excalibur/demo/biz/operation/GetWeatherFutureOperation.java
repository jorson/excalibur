package com.excalibur.demo.biz.operation;

import static com.nd.hy.android.hermes.demo.biz.base.Protocol.*;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.APP_KEY;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.DATA_FORMAT;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.SIGN;
import static com.nd.hy.android.hermes.demo.biz.base.Protocol.WEATHER_API_URL;

import android.content.Context;
import android.os.Bundle;
import com.nd.hy.android.core.base.HermesException;
import com.nd.hy.android.core.net.http.HttpRequest;
import com.nd.hy.android.core.net.http.base.ConnectionResult;
import com.nd.hy.android.core.util.ObjectUtils;
import com.nd.hy.android.frame.base.Operation;
import com.nd.hy.android.frame.base.Priority;
import com.nd.hy.android.frame.base.Request;
import com.nd.hy.android.hermes.demo.biz.base.BundleKey;
import com.nd.hy.android.hermes.demo.biz.model.BaseArrayEntry;
import com.nd.hy.android.hermes.demo.biz.model.WeatherDetail;

import java.io.IOException;
import java.util.ArrayList;

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
    public Bundle execute(Context context, Request request) throws HermesException {
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

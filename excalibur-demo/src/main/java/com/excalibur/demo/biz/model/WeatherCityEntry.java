package com.excalibur.demo.biz.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.excalibur.core.util.ObjectUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 14-5-22
 */
public class WeatherCityEntry {

    private String success;

    private List<City> cities;

    /**
     * 返回的json结构有点丑
     *
     * @param data
     * @return
     */
    public static WeatherCityEntry parse(String data) {
        WeatherCityEntry entry = new WeatherCityEntry();
        ObjectMapper mapper = ObjectUtils.getMapperInstance();
        try {
            JSONObject obj = new JSONObject(data);
            entry.success = obj.getString("success");
            if (obj.has("result")) {
                entry.cities = new ArrayList<City>();
                JSONObject result = obj.getJSONObject("result");
                Iterator it = result.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    City city = mapper.readValue(result.getString(key), City.class);
                    entry.cities.add(city);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public String getSuccess() {
        return success;
    }

    public List<City> getCities() {
        return cities;
    }
}

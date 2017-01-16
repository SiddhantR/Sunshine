package com.example.sidraghu.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by SIDRAGHU on 13-01-2017.
 */

public class JsonParser {

    public  static  String[] getWeatherDataFromJSON(String jsonString,int days) throws JSONException{
        JSONObject ob1 = new JSONObject(jsonString);
        JSONArray array = ob1.getJSONArray("list");
        String[] weatherData = new String[days];
        StringBuilder stringBuilder=null;
        JSONObject object;
        String day;
        String main_weather,date_time;
        long max,min;
        for(int i=0;i<days;i++){
            object = array.getJSONObject(i);
            JSONArray array2 = object.getJSONArray("weather");
            JSONObject ob2 = array2.getJSONObject(0);
            JSONObject ob3 = object.getJSONObject("temp");
            day = JsonParser.getDate(object.getLong("dt"));
            main_weather = ob2.getString("main");
            max = Math.round(ob3.getLong("max"));
            min = Math.round(ob3.getLong("min"));
            stringBuilder.append(day).append(",").append(main_weather)
                    .append(",").append(max+"/").append(min);
            weatherData[i]=stringBuilder.toString();
        }
        return weatherData;
    }

    private static String getDate(long  time){
        Date date = new Date(time*1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E,MMM d");
        return dateFormat.format(date).toString();

    }
}

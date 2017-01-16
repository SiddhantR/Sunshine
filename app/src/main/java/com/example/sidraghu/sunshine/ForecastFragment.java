package com.example.sidraghu.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SIDRAGHU on 06-01-2017.
 */

public class ForecastFragment extends Fragment {

    ListView forecast_view;
    ArrayAdapter<String> forecast;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            BackgroundTask weatherTask = new BackgroundTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_menu, menu);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        BackgroundTask backgroundTask = new BackgroundTask();
        String data[] = {"Monday 30", "Tuesday 28", "Wednesday 30", "Thursday 31", "Friday 25", "Saturday 22", "Sunday 20"};
        List<String> list = new ArrayList<>(Arrays.asList(data));
        forecast = new ArrayAdapter<String>(getActivity(), R.layout.list_layout, R.id.textlist, list);
        forecast_view = (ListView) rootView.findViewById(R.id.list);
        forecast_view.setAdapter(forecast);
        return rootView;
    }



    class BackgroundTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = BackgroundTask.class.getSimpleName();


        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                for(String dummy : strings){
                    Log.v("PostExcecuteData",dummy);
                }
                forecast.clear();
                for (String data : strings) {
                    forecast.add(data);
                }
            }
            forecast.notifyDataSetChanged();
        }

        @Override
        protected String[] doInBackground(String... params) {

            if(params.length==0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJson = null;
            try {
                final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID = "appid";
                Uri uriBuilt = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNITS_PARAM, "metric")
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(7))
                        .appendQueryParameter(APPID, "f8a779d5a75eaa29dbadba5a1f72c2cd").build();
                URL url = new URL(uriBuilt.toString());
                Log.v(LOG_TAG, "Built URl " + uriBuilt.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("Get");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {

                    return null;
                }
                forecastJson = stringBuffer.toString();
                Log.v("Message", forecastJson);
            } catch (Exception e) {

                Log.e("Error", "Weather data not found");
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("LOG_TAG", "reader not closed");

                    }
                }
            }

            try {
                Toast.makeText(getContext(), "json parser worker", Toast.LENGTH_SHORT).show();
                return JsonParser.getWeatherDataFromJSON(forecastJson, 7);
            } catch (JSONException e) {

                Log.e("Error", "JSONException occurred");
            }
            return null;
        }
    }
}
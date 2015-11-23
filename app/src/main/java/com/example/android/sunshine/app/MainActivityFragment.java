package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * A placeholder fragment containing a simple view.
 */
public class  MainActivityFragment  extends Fragment {
    ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
    }
   public void onCreate (Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
   }
    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater)
    {
        inflater.inflate(R.menu.mainactivityfragment, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
     int id = item.getItemId();
        if(id== R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();

            return true ;
        } return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] forecastArray =
                {
                        "Today-sunny- 87/56",
                        "tomorrow-rainy-65/55",
                        "tuesday-cloudy-78/67",
                        "wednesday-muddy-98/89",
                        "thursday-greeny-99/98",
                        "friday_i need some help-99/88"
                };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecastArray);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        // These two need to be declared outside the try/catch


        return rootView; }
}
     class FetchWeatherTask extends AsyncTask<String,Void,Void > {
         private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

         protected Void doInBackground(String... prams) {
             // so that they can be closed in the finally block.
             HttpURLConnection urlConnection = null;
             BufferedReader reader = null;

             // Will contain the raw JSON response as a string.
             String forecastJsonStr = null;
           String format ="json";
             String units ="metric";
             int numDays=7;
             try {
                 // Construct the URL for the OpenWeatherMap query
                 // Possible parameters are avaiable at OWM's forecast API page, at
                 // http://openweathermap.org/API#forecast
                 final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                 final String QUERY_PARAM="q";
                 final String FORMAT_PARAM="mode";
                 final String UNITS_PARAM="units";
                 final String DAYS_PARAM="cnt";
                 Uri builturi = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAM, prams[0]).
                         appendQueryParameter(FORMAT_PARAM, format).appendQueryParameter(UNITS_PARAM,units).appendQueryParameter(DAYS_PARAM,Integer.toString(numDays)).build();
                      URL url = new URL(builturi.toString());
                 Log.v(LOG_TAG, "built uri"+ builturi.toString());
                 // Create the request to OpenWeatherMap, and open the connection
                 urlConnection = (HttpURLConnection) url.openConnection();
                 urlConnection.setRequestMethod("GET");
                 urlConnection.connect();

                 // Read the input stream into a String
                 InputStream inputStream = urlConnection.getInputStream();
                 StringBuffer buffer = new StringBuffer();
                 if (inputStream == null) {
                     // Nothing to do.
                     return null;
                 }
                 reader = new BufferedReader(new InputStreamReader(inputStream));

                 String line;
                 while ((line = reader.readLine()) != null) {
                     // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                     // But it does make debugging a *lot* easier if you print out the completed
                     // buffer for debugging.
                     buffer.append(line + "\n");
                 }

                 if (buffer.length() == 0) {
                     // Stream was empty.  No point in parsing.
                     return null;
                 }
                 forecastJsonStr = buffer.toString();
                 Log.v(LOG_TAG,"JSONSTR:"+ forecastJsonStr);
             } catch (IOException e) {
                 Log.e(LOG_TAG, "Error ", e);
                 // If the code didn't successfully get the weather data, there's no point in attemping
                 // to parse it.
                 return null;
             } finally {
                 if (urlConnection != null) {
                     urlConnection.disconnect();
                 }
                 if (reader != null) {
                     try {
                         reader.close();
                     } catch (final IOException e) {
                         Log.e(LOG_TAG, "Error closing stream", e);
                     }
                 }
             }

          return null;
         }


     }

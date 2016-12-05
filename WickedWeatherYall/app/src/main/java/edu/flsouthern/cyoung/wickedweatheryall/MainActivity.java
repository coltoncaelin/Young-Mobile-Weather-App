package edu.flsouthern.cyoung.wickedweatheryall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.flsouthern.cyoung.wickedweatheryall.data.WeatherContract;
import edu.flsouthern.cyoung.wickedweatheryall.data.weatherDBHelper;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE_TO_SEND_OVER = "Message";

    WeatherAdapter mWeatherAdapter; //For loading our ListView

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu, which adds the items to the action bar if present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handle item clicks for menu
        int id = item.getItemId();
        if (id == R.id.action_settings){
            //This is the setting menu item
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }


        if(id == R.id.action_map){
            //This is the show cats location menu item
            openCityInMap();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void openCityInMap(){
        // Retrieve cat city information
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        //Look for key, if not there, go with the default
        String userCity = sharedPrefs.getString(
                getString(R.string.pref_loc_key),
                getString(R.string.pref_loc_default)
        );

        Uri location = Uri.parse("geo:0,0?q=" + userCity);

        // Make intent
        Intent intent = new Intent(Intent.ACTION_VIEW, location);

        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String LOG_TAG2 = FetchWeatherTask.class.getSimpleName();
        Log.v(LOG_TAG2, "Did onCreate");


        //Getting db connection to look up weather data
        weatherDBHelper weatherDbHelper = new weatherDBHelper(this);
        Cursor c = weatherDbHelper.getAllWeather();
        mWeatherAdapter = new WeatherAdapter(this,c,0);

        //Get reference to list view and attach the adapter
        ListView listView = (ListView)findViewById(R.id.listview_forecast);
        listView.setAdapter(mWeatherAdapter);
        // updateWeather(); Removed for project 4

        //Connect Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){

                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                final String LOG_TAG2 = FetchWeatherTask.class.getSimpleName();

                if(c != null) {
                    //Find column numbers for the data in the cursor
                    Log.v(LOG_TAG2, "Got into if c != null territory");
                    int idx_weather_date = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
                    int idx_weather_id = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
                    int idx_weather_short_desc = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
                    int idx_weather_min_temp = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
                    int idx_weather_max_temp = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
                    int idx_weather_humidity = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
                    int idx_weather_pressure = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
                    int idx_weather_wind_speed = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
                    int idx_weather_degrees = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);


                    //Get data to pass to activity
                    String date = c.getString(idx_weather_date);
                    Log.v(LOG_TAG2, "Here is the idx_weather_date " + c.getString(idx_weather_date));
                    int id = c.getInt(idx_weather_id);
                    String shortDesc = c.getString(idx_weather_short_desc);
                    Log.v(LOG_TAG2, "Here is the idx_weather_short_desc " + c.getString(idx_weather_short_desc));
                    String minTemp = c.getString(idx_weather_min_temp);
                    String maxTemp = c.getString(idx_weather_max_temp);
                    String humidity = c.getString(idx_weather_humidity);
                    String pressure = c.getString(idx_weather_pressure);
                    String windSpeed = c.getString(idx_weather_wind_speed);
                    String degrees = c.getString(idx_weather_degrees);

                    //Make an intent and attach our data    ****** Here's where my problem is
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    Log.v(LOG_TAG2, "created intent");
                    intent.putExtra("WEATHER_DATE", date);
                    Log.v(LOG_TAG2, "attached date to intent and date is: " + date);
                    intent.putExtra("WEATHER_ID", id);
                    intent.putExtra("WEATHER_SHORT_DESC", shortDesc);
                    intent.putExtra("WEATHER_MIN_TEMP", minTemp);
                    intent.putExtra("WEATHER_MAX_TEMP", maxTemp);
                    intent.putExtra("WEATHER_HUMIDITY", humidity);
                    intent.putExtra("WEATHER_PRESSURE", pressure);
                    intent.putExtra("WEATHER_WIND_SPEED", windSpeed);
                    intent.putExtra("WEATHER_DEGREES", degrees);

                    startActivity(intent);
                }
            }
        });
    }

    //Update here, so that it updates every time we switch back to main view
    @Override
    public void onStart(){
        super.onStart();
        //When starting page, try to get cat data
        updateWeather();
    }

    private void updateWeather(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String units = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
        String userLocation = prefs.getString(getString(R.string.pref_loc_key), getString(R.string.pref_loc_default));

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(userLocation, units);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatTemperature(double temperature) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedTemperature = Math.round(temperature);
            String readableTemperature = "" + roundedTemperature;

            return readableTemperature;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */

        //Not sure if we need string units here or not
        private void getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            Log.v(LOG_TAG, "Started FetchWeatherTask");

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_WIND_SPEED = "speed";
            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_DATE = "dt";
            //For Project 5- Should these/can these both be "id"?
            final String OWM_WEATHER_ID = "id";
            final String OWM_LOC_ID = "id";
            final String OWM_DEGREES = "deg";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);


            Log.v(LOG_TAG, "Found forecastJSONObject");

            //Get Location ID
            String locationID = forecastJson.getJSONObject("city").getString(OWM_LOC_ID);
            Log.v(LOG_TAG, "GotLocationID");

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            Log.v(LOG_TAG, "Got weatherArray");


            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            //Create a new dbhelper
            weatherDBHelper weatherDBHelper = new weatherDBHelper(MainActivity.this);

            //Empty old weather data from DB
            weatherDBHelper.deleteAllWeather();

            for (int i = 0; i < weatherArray.length(); i++) {
                String description;
                String date;
                int weatherID;
                String pressure;
                String humidity;
                String windspeed;
                String degrees;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);; //TODO: Get JSON for current day
                Log.v(LOG_TAG, "Got dayForecast");

                pressure = dayForecast.getString(OWM_PRESSURE);
                humidity = dayForecast.getString(OWM_HUMIDITY);
                windspeed = dayForecast.getString(OWM_WIND_SPEED);
                degrees = dayForecast.getString(OWM_DEGREES);
                Log.v(LOG_TAG, "Got press, humid, wind, and degrees");

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                date = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONArray weatherObject = dayForecast.getJSONArray(OWM_WEATHER); //TODO: Get the child weather object
                Log.v(LOG_TAG, "Got child weather object");

                description = weatherObject.getJSONObject(0).getString(OWM_DESCRIPTION); //TODO: Extract the description
                Log.v(LOG_TAG, "Got description");

                weatherID = weatherObject.getJSONObject(0).getInt(OWM_WEATHER_ID);
                Log.v(LOG_TAG, "Got weatherID");

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE); // TODO: Get temp object
                Log.v(LOG_TAG, "Got temp object");
                String high = formatTemperature(temperatureObject.getLong(OWM_MAX));
                Log.v(LOG_TAG, "High is: " + high);
                String low = formatTemperature(temperatureObject.getLong(OWM_MIN));
                Log.v(LOG_TAG, "Low is " + low);


                //No longer needed for project 5
                //resultStrs[i] = day + " - " + description + " - " + highAndLow;


                weatherDBHelper.insertWeather(locationID,date,weatherID,description,low,high,humidity,pressure,windspeed,degrees);
                Log.v(LOG_TAG, "Inserted into database");
            }
        }


        @Override
        protected Void doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String userLocation = params[0];
            String units = params[1];
            int numDays = 7;

            try {

                final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + userLocation + ",US&mode=" +
                        format + "&units=" + units + "&cnt=" + numDays + "&appid=bcb4e6ed4039933216fe1e93bacbb11e";

                URL url = new URL(WEATHER_BASE_URL);

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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (Exception e) {
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
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            // This will only happen if there was an error getting or parsing the forecast.
            Log.v(LOG_TAG, "Returned null");
            return null;
        }

       /* Not needed anymore since we're pulling from database
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mWeatherAdapter.clear();
                Log.v(LOG_TAG, "Cleared previous mWeatherAdapter");


                for(String weatherInfo : result){
                    mWeatherAdapter.add(weatherInfo);
                }
                Log.v(LOG_TAG, "New mWeatherAdapter info added");
                // New data is back from the server.  Hooray!
            }
        }
        */
    }

}

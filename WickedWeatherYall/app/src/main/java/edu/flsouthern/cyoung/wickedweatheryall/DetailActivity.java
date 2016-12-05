package edu.flsouthern.cyoung.wickedweatheryall;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    //public final static String PERMANENT_WEATHER = "Just look outside";
    //public String blueColorCode = "#0000FF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final String LOG_TAG2 = MainActivity.FetchWeatherTask.class.getSimpleName();

        Intent intent = getIntent();


        String date = intent.getStringExtra("WEATHER_DATE");

        Log.v(LOG_TAG2, "intent for WEATHER_DATE received " + date);

        int id = intent.getIntExtra("WEATHER_ID", 0);
        String shortDesc = intent.getStringExtra("WEATHER_SHORT_DESC");
        String minTemp = intent.getStringExtra("WEATHER_MIN_TEMP");
        String maxTemp = intent.getStringExtra("WEATHER_MAX_TEMP");
        String humidity = intent.getStringExtra("WEATHER_HUMIDITY");
        String pressure = intent.getStringExtra("WEATHER_PRESSURE");
        String windSpeed = intent.getStringExtra("WEATHER_WIND_SPEED");
        float floatWindSpeed = Float.parseFloat(windSpeed);
        String degrees = intent.getStringExtra("WEATHER_DEGREES");
        float floatDegrees = Float.parseFloat(degrees);

        ((TextView)findViewById(R.id.detail_day_textview)).setText(date);
        ((TextView)findViewById(R.id.detail_high_textview)).setText((Math.round(Double.parseDouble(maxTemp))) + "");
        ((TextView)findViewById(R.id.detail_low_textview)).setText((Math.round(Double.parseDouble(minTemp))) + "");
        ((ImageView)findViewById(R.id.detail_icon)).setImageResource(Utility.getArtResourceForWeatherCondition(id));
        ((TextView)findViewById(R.id.detail_forecast_textview)).setText(shortDesc);
        ((TextView)findViewById(R.id.detail_humidity_textview)).setText("Humidity: " + ((Math.round(Double.parseDouble(humidity))) + "") + "%");
        ((TextView)findViewById(R.id.detail_pressure_textview)).setText("Pressure: " + ((Math.round(Double.parseDouble(pressure))) + "") + " hPa");
        ((TextView)findViewById(R.id.detail_wind_textview)).setText("Wind: " + Utility.getFormattedWind(this,floatWindSpeed,floatDegrees));

    }

}

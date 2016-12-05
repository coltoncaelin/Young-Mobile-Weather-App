package edu.flsouthern.cyoung.wickedweatheryall;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.flsouthern.cyoung.wickedweatheryall.data.WeatherContract;

public class WeatherAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView dateView;
        public final TextView highView;
        public final TextView lowView;
        public final ImageView imageView;
        public final TextView forecastView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            highView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowView= (TextView) view.findViewById(R.id.list_item_low_textview);
            imageView = (ImageView) view.findViewById(R.id.list_item_icon);
            forecastView= (TextView) view.findViewById(R.id.list_item_forecast_textview);

        }
    }

    public WeatherAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // Remember that these views are reused as needed.
    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        if(c.getPosition() == 0) {

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_forecast_today, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            return view;

        }else{

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_forecast, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            return view;
        }

    }

    // This is where we fill-in the views with the contents of the cursor.
    @Override
    public void bindView(View view, Context context, Cursor c) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //Get column indices for the cursor
        int idx_weather_date = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_short_desc = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
        int idx_min = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_max = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_weather_id = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);

        //Retrieve the data from the cursor
        String weatherDate = c.getString(idx_weather_date);
        String forecast = c.getString(idx_short_desc);
        String low = c.getString(idx_min);
        String high = c.getString(idx_max);
        int image = c.getInt(idx_weather_id);


        viewHolder.dateView.setText(weatherDate);
        viewHolder.highView.setText((Math.round(Double.parseDouble(high))) + "");
        viewHolder.lowView.setText((Math.round(Double.parseDouble(low))) + "");
        viewHolder.forecastView.setText(forecast);

        if(c.getPosition()==0)
            viewHolder.imageView.setImageResource(Utility.getArtResourceForWeatherCondition(image));
        else
            viewHolder.imageView.setImageResource(Utility.getIconResourceForWeatherCondition(image));

        //viewHolder.imageView.setText(image);  Will use this when we have pictures

    }
}
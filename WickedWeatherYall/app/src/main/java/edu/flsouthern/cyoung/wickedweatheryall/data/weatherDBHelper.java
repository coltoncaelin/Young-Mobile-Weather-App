package edu.flsouthern.cyoung.wickedweatheryall.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.flsouthern.cyoung.wickedweatheryall.data.WeatherContract.WeatherEntry;

public class weatherDBHelper extends SQLiteOpenHelper {
    //If you change the db schema in any way, you MUST increment this number
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public weatherDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY, " +
                WeatherEntry.COLUMN_LOC_KEY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MIN_TEMP + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertWeather(String locID, String date, int weatherID, String shortDesc, String minTemp, String maxTemp, String humidity, String pressure, String windSpeed, String degreees){

        SQLiteDatabase db = this.getWritableDatabase();

        // Make a map of columns and values
        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_LOC_KEY, locID);
        values.put(WeatherEntry.COLUMN_DATE, date);
        values.put(WeatherEntry.COLUMN_WEATHER_ID, weatherID);
        values.put(WeatherEntry.COLUMN_SHORT_DESC, shortDesc);
        values.put(WeatherEntry.COLUMN_MIN_TEMP, minTemp);
        values.put(WeatherEntry.COLUMN_MAX_TEMP, maxTemp);
        values.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
        values.put(WeatherEntry.COLUMN_PRESSURE, pressure);
        values.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
        values.put(WeatherEntry.COLUMN_DEGREES, degreees);

        //Insert into db
        db.insert(WeatherEntry.TABLE_NAME, null, values);  // null is a typically unneeded parameter here

        //close your db connection when not in use, that way, in a more used app, we don't have lots of ongoing connections
        db.close();
    }

    public Cursor getWeather(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        //Make our projection
        String[] projection = {
                WeatherEntry._ID, //Internal ID database is using
                WeatherEntry.COLUMN_LOC_KEY,
                WeatherEntry.COLUMN_DATE,
                WeatherEntry.COLUMN_WEATHER_ID,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES

        };

        //Project 5- Not sure if this is what's needed for search
        //Only get weather with matching Date
        String selection = WeatherEntry.COLUMN_DATE + " = ?"; //? is argument to be filled in
        String[] selectionArgs = {String.valueOf(id)}; //String.valueOf just turns id into string since its an int

        Cursor c = db.query(
                WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        return c;
    }

    public Cursor getAllWeather(){
        SQLiteDatabase db = this.getReadableDatabase();

        //Make our projection
        String [] projection = {
                WeatherEntry._ID, //Internal ID database is using
                WeatherEntry.COLUMN_LOC_KEY,
                WeatherEntry.COLUMN_DATE,
                WeatherEntry.COLUMN_WEATHER_ID,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES
        };

        Cursor c = db.query(
                WeatherEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );


        return c;
    }

    /*  Might not need an update because of the way we update DB through openWeatherMap

    public int updateCat(int id, String name, String color, int age, double weight){

        SQLiteDatabase db = this.getWritableDatabase();  //Use readable or writable

        // Make a map of columns and values
        ContentValues values = new ContentValues();
        values.put(CatEntry.COLUMN_CAT_ID, id);
        values.put(CatEntry.COLUMN_CAT_NAME, name);
        values.put(CatEntry.COLUMN_CAT_COLOR, color);
        values.put(CatEntry.COLUMN_CAT_AGE, age);
        values.put(CatEntry.COLUMN_CAT_WEIGHT, weight);

        String selection = CatEntry.COLUMN_CAT_ID + " LIKE = ?";
        String [] selectionArgs = { String.valueOf(id)};

        //Update the db
        int count = db.update(
                CatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        //close your db connection when not in use, that way, in a more used app, we don't have lots of ongoing connections
        db.close();

        return count;
    }
    */


    public void deleteAllWeather(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(WeatherEntry.TABLE_NAME, null, null);
        db.close();
    }

}


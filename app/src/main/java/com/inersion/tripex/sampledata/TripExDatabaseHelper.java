package com.inersion.tripex.sampledata;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class TripExDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TripEx.db";
    private static final int DATABASE_VERSION = 17;

    // constructor
    public TripExDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the trips table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the trips table
        final String CREATE_TRIPS_TABLE =
                "CREATE TABLE " + DatabaseDescription.Trip.TABLE_NAME + "(" +
                        DatabaseDescription.Trip._ID + " integer primary key, " +
                        DatabaseDescription.Trip.COLUMN_NAME + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_FROM + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_TO + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_DEPART + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_RETURN + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_AIRFARE + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_HOTEL + " TEXT, " +
                        DatabaseDescription.Trip.COLUMN_RENTAL + " TEXT);";
        db.execSQL(CREATE_TRIPS_TABLE); // create the trips table
    }

    // normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) { }
}

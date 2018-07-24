package com.inersion.tripex.sampledata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.inersion.tripex.R;
import com.inersion.tripex.sampledata.DatabaseDescription.Trip;

public class TripExContentProvider extends ContentProvider {
    // used to access the database
    private TripExDatabaseHelper dbHelper;

    // UriMatcher helps ContentProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    // constants used with UriMatcher to determine operation to perform
    private static final int ONE_TRIP = 1; // manipulate one trip
    private static final int TRIPS = 2; // manipulate trips table

    // static block to configure this ContentProvider's UriMatcher
    static {
        // Uri for Trip with the specified id (#)
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Trip.TABLE_NAME + "/#", ONE_TRIP);

        // Uri for Trips table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Trip.TABLE_NAME, TRIPS);
    }

    // called when the TripExContentProvider is created
    @Override
    public boolean onCreate() {
        // create the TripExDatabaseHelper
        dbHelper = new TripExDatabaseHelper(getContext());
        return true; // ContentProvider successfully created
    }

    // required method: Not used in this app, so we return null
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // query the database
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        // create SQLiteQueryBuilder for querying trips table
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Trip.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_TRIP: // trip with specified id will be selected
                queryBuilder.appendWhere(
                        Trip._ID + "=" + uri.getLastPathSegment());
                break;
            case TRIPS: // all trips will be selected
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // execute the query to select one or all trips
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // configure to watch for content changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // insert a new trip in the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newTripUri = null;

        switch (uriMatcher.match(uri)) {
            case TRIPS:
                // insert the new trip--success yields new trip's row id
                long rowId = dbHelper.getWritableDatabase().insert(
                        Trip.TABLE_NAME, null, values);

                // if the trip was inserted, create an appropriate Uri;
                // otherwise, throw an exception
                if (rowId > 0) { // SQLite row IDs start at 1
                    newTripUri = Trip.buildTripUri(rowId);

                    // notify observers that the database changed
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newTripUri;
    }

    // update an existing trip in the database
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int numberOfRowsUpdated; // 1 if update successful; 0 otherwise

        switch (uriMatcher.match(uri)) {
            case ONE_TRIP:
                // get from the uri the id of trip to update
                String id = uri.getLastPathSegment();

                // update the trip
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        DatabaseDescription.Trip.TABLE_NAME, values, Trip._ID + "=" + id,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // if changes were made, notify observers that the database changed
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    // delete an existing trip from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_TRIP:
                // get from the uri the id of trip to update
                String id = uri.getLastPathSegment();

                // delete the trip
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        DatabaseDescription.Trip.TABLE_NAME, Trip._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // notify observers that the database changed
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}

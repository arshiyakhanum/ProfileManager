package com.arshiya.mapsapi.storage.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by arshiya on 10/28/2015.
 */

public class LocationsDatabase extends SQLiteOpenHelper {

    /**
     * Database name
     */
    private static String DATABASE_NAME = "LOCATION_DATABASE";

    /**
     * Version number of the database
     */
    private static int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String LOCATION_NAME = "location_name";
    public static final String GEOFENCE_TAG = "geofence_tag";
    public static final String ADDRESS = "address";
    public static final String LATLNGS = "ltlngs";
    public static final String PROFILE_TYPE = "profile_type";
    public static final String LOCATION_TYPE = "location_type";


    /**
     * A constant, stores the the table name
     */
    private static final String LOCATION_TABLE_NAME = "LOCATION_TABLE";

    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + LOCATION_TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ADDRESS + " TEXT, " + LOCATION_NAME + " TEXT, " + GEOFENCE_TAG + " TEXT, " +
            LOCATION_TYPE + " INTEGER, " +
            LATLNGS + " TEXT, " + PROFILE_TYPE + " INTEGER " + ")";

    /**
     * An instance variable for SQLiteDatabase
     */
    private SQLiteDatabase mDB;

    /**
     * Constructor
     */
    public LocationsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mDB = getWritableDatabase();
    }

    /**
     * This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    /**
     * Inserts a new location to the table locations
     */
    public long insert(ContentValues contentValues) {
        return mDB.insert(LOCATION_TABLE_NAME, null, contentValues);
    }

    /**
     * deletes particular geofence from the table
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int del(Uri uri, String selection, String[] selectionArgs) {
        return mDB.delete(LOCATION_TABLE_NAME, selection, selectionArgs);

    }

    /**
     * Deletes all locations from the table
     */
    public int del() {
        return mDB.delete(LOCATION_TABLE_NAME, null, null);
    }

    /**
     * Returns all the locations from the table
     */
    public Cursor getAllLocations() {
        return mDB.query(LOCATION_TABLE_NAME, new String[]{_ID, ADDRESS, LOCATION_NAME, GEOFENCE_TAG, LATLNGS, LOCATION_TYPE, PROFILE_TYPE},
                null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        onCreate(db);
    }

    public int delete(String geofenceTag) {
        String selection = LocationsDatabase.GEOFENCE_TAG + " = ?";
        return mDB.delete(LOCATION_TABLE_NAME, selection, new String[]{geofenceTag});

    }

    public Cursor getGeofenceTag(String geofencetag) {
        String selection = LocationsDatabase.GEOFENCE_TAG + " = ? ";

        return mDB.query(LOCATION_TABLE_NAME, new String[]{GEOFENCE_TAG}, selection, new String[]{geofencetag}, null, null, null);
    }

    public Cursor getGeofenceTags(String address, String locationName) {
        String selection = LocationsDatabase.ADDRESS + " = ? and " + LocationsDatabase.LOCATION_NAME + " = ?";

        return mDB.query(LOCATION_TABLE_NAME, new String[]{GEOFENCE_TAG}, selection, new String[]{address, locationName}, null, null, null);
    }

    public Cursor getProfileType(String geotag) {
        String selection = LocationsDatabase.GEOFENCE_TAG + " = ? ";//and " + LocationsDatabase.LOCATION_NAME + " = ?";

        return mDB.query(LOCATION_TABLE_NAME, new String[]{PROFILE_TYPE}, selection, new String[]{geotag}, null, null, null);
    }

    public int getSize() {
        Cursor cursor = mDB.query(LOCATION_TABLE_NAME, new String[]{_ID, ADDRESS, LOCATION_NAME, GEOFENCE_TAG, LATLNGS, LOCATION_TYPE, PROFILE_TYPE},
                null, null, null, null, null);

        return cursor.getCount();
    }
}

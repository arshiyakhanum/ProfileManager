package com.arshiya.mapsapi.storage.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.sql.SQLException;

/**
 * Created by akhanumx on 10/28/2015.
 */

/**
 * A custom Content Provider to do the database operations
 */
public class LocationsContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.arshiya.mapsapi.provider.locations";

    /**
     * A uri to do operations on locations table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations");

    /**
     * Constant to identify the requested operation
     */
    private static final int LOCATIONS = 1;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
    }

    /**
     * This content provider does the database operations by this object
     */
    LocationsDatabase mLocationsDB;

    /**
     * A callback method which is invoked when the content provider is starting up
     */
    @Override
    public boolean onCreate() {
        mLocationsDB = new LocationsDatabase(getContext());
        return true;
    }

    /**
     * A callback method which is invoked when insert operation is requested on this content provider
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mLocationsDB.insert(values);
        Uri _uri = null;
        if (rowID > 0) {
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        } else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int deleteAll(Uri uri){
        return mLocationsDB.del();
    }
    /**
     * A callback method which is invoked when delete operation is requested on this content provider
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mLocationsDB.del(uri, selection, selectionArgs);
    }

    /**
     * A callback method which is invoked by default content uri
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == LOCATIONS) {
            return mLocationsDB.getAllLocations();
        }
        return null;
    }

    public Cursor getGeofenceTag(String tag){
        return mLocationsDB.getGeofenceTag(tag);
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }
}

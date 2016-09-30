package com.arshiya.mapsapi.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationEntity;
import java.util.HashMap;

import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.ADDRESS;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.GEOFENCE_TAG;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.LATLNGS;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.LOCATION_NAME;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.LOCATION_TYPE;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns.PROFILE_TYPE;
import static com.arshiya.mapsapi.storage.ProfileManagerDataContract.LocationColumns._ID;

/**
 * @author Umang Chamaria
 */

public class ProfileManagerProvider extends ContentProvider {

  private DatabaseHelper helper;
  private UriMatcher sUriMatcher;

  /**
   * Denotes URI matched Locations
   */
  private static final int LOCATION = 1;
  /**
   * Denotes URI matched single Location
   */
  private static final int LOCATION_ID = 2;

  @Override public boolean onCreate() {
    helper = new DatabaseHelper(getContext());
    initializeUriMatcher();
    return true;
  }

  private void initializeUriMatcher() {
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    Context context = getContext();
    sUriMatcher.addURI(ProfileManagerDataContract.getAuthority(context), "locationtable", LOCATION);
    sUriMatcher.addURI(ProfileManagerDataContract.getAuthority(context), "locationtable/#",
        LOCATION_ID);
  }

  private static HashMap<String, String> sLocationProjectionMap;

  static {
    sLocationProjectionMap = new HashMap<>();
    sLocationProjectionMap.put(LocationEntity._ID, LocationEntity._ID);
    sLocationProjectionMap.put(LocationEntity.LOCATION_NAME, LocationEntity.LOCATION_NAME);
    sLocationProjectionMap.put(LocationEntity.GEOFENCE_TAG, LocationEntity.GEOFENCE_TAG);
    sLocationProjectionMap.put(LocationEntity.ADDRESS, LocationEntity.ADDRESS);
    sLocationProjectionMap.put(LocationEntity.LATLNGS, LocationEntity.LATLNGS);
    sLocationProjectionMap.put(LocationEntity.PROFILE_TYPE, LocationEntity.PROFILE_TYPE);
    sLocationProjectionMap.put(LocationEntity.LOCATION_TYPE, LocationEntity.LOCATION_TYPE);
  }


  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    SQLiteDatabase db = helper.getReadableDatabase();
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    String limit = uri.getQueryParameter(ProfileManagerDataContract.QUERY_PARAMETER_LIMIT);
    switch (sUriMatcher.match(uri)){
      case LOCATION:
        qb.setProjectionMap(sLocationProjectionMap);
        qb.setTables(DatabaseHelper.TABLE_NAME_LOCATION);
        break;
      case LOCATION_ID:
        qb.appendWhere(LocationEntity._ID + "=" + uri.getPathSegments().get(1));
        break;
    }
    Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, limit);
    return cursor;
  }

  @Nullable @Override public String getType(Uri uri) {
    switch (sUriMatcher.match(uri)) {
      case LOCATION:
        return LocationEntity.CONTENT_TYPE;
      case LOCATION_ID:
        return LocationEntity.CONTENT_ITEM_TYPE;
      default:
        throw new IllegalArgumentException("No Matching URI found");
    }
  }

  @Nullable @Override public Uri insert(Uri uri, ContentValues values) {
    if (values == null) return null;
    Uri newUri = null;
    long rowId = 0;
    SQLiteDatabase db = helper.getReadableDatabase();
    switch (sUriMatcher.match(uri)) {
      case LOCATION:
        rowId = db.insert(DatabaseHelper.TABLE_NAME_LOCATION, null, values);
        if (rowId > 0) {
          newUri = ContentUris.withAppendedId(LocationEntity.getContentUri(getContext()), rowId);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
    return newUri;
  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = helper.getReadableDatabase();
    int count = 0;
    switch (sUriMatcher.match(uri)) {
      case LOCATION:
        count = db.delete(DatabaseHelper.TABLE_NAME_LOCATION, selection, selectionArgs);
        break;
      case LOCATION_ID:
        String locationId = uri.getPathSegments().get(1);
        count = db.delete(DatabaseHelper.TABLE_NAME_LOCATION,
            LocationEntity._ID + "=" + locationId + (!TextUtils.isEmpty(selection) ? " AND "
                + selection : ""), selectionArgs);
    }
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    SQLiteDatabase db = helper.getReadableDatabase();
    int count = 0;
    switch (sUriMatcher.match(uri)) {
      case LOCATION:
        count = db.update(DatabaseHelper.TABLE_NAME_LOCATION, values, selection, selectionArgs);
        break;
      case LOCATION_ID:
        String locationId = uri.getPathSegments().get(1);
        count = db.update(DatabaseHelper.TABLE_NAME_LOCATION, values,
            LocationEntity._ID + "=" + locationId + (!TextUtils.isEmpty(selection) ? " AND "
                + selection : ""), selectionArgs);
        break;
    }

    return count;
  }

  class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "LOCATION_DATABASE";

    public DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static final String TABLE_NAME_LOCATION = "LOCATION_TABLE";

    @Override public void onCreate(SQLiteDatabase db) {
      createLocationTable(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createLocationTable(SQLiteDatabase db) {
      String DDL_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS "
          + TABLE_NAME_LOCATION
          + "("
          + _ID
          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + ADDRESS
          + " TEXT, "
          + LOCATION_NAME
          + " TEXT, "
          + GEOFENCE_TAG
          + " TEXT, "
          + LOCATION_TYPE
          + " INTEGER, "
          + LATLNGS
          + " TEXT, "
          + PROFILE_TYPE
          + " INTEGER "
          + ")";
      db.execSQL(DDL_LOCATION_TABLE);
    }
  }
}

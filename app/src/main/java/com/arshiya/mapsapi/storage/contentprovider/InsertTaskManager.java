package com.arshiya.mapsapi.storage.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Messenger;
import android.util.Log;

import com.arshiya.mapsapi.common.Constants;

/**
 * Created by akhanumx on 10/28/2015.
 */
public class InsertTaskManager {
    private static final String TAG = InsertTaskManager.class.getSimpleName();
    private static Context mContext;
    private static InsertTaskManager mInsertTaskManager = new InsertTaskManager();
    private static Messenger mMessenger;

    private InsertTaskManager() {
    }

    public static InsertTaskManager getInsertTaskManagerInstance(Context context, Messenger messenger) {
        mContext = context;
        mMessenger = messenger;
        return mInsertTaskManager;
    }

    public void insert(ContentValues contentValues) {
        new InsertTask().execute(contentValues);
    }

    private class InsertTask extends AsyncTask<ContentValues, Void, Integer> {

        @Override
        protected Integer doInBackground(ContentValues... params) {
            Log.d(TAG, " do in background");

            LocationsContentProvider provider = new LocationsContentProvider();
            int size = provider.getSize();

            if (size == Constants.MAX_GEOFENCE_SIZE){
                //max geofence  limit is reached cannot add any more geofences
                return -1;
            }

            ContentResolver cr = mContext.getContentResolver();
            Uri uri = cr.insert(LocationsContentProvider.CONTENT_URI, params[0]);
            if (null == uri) {
                Log.e(TAG, " writing to table failed");
                return 0;
            } else {
                Log.d(TAG, " write to table done, uri : " + uri);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer status) {


            //todo delete - start
            //read
            Cursor cursor = mContext.getContentResolver().query(LocationsContentProvider.CONTENT_URI, new String[]{
                    LocationsDatabase.LATLNGS, LocationsDatabase.PROFILE_TYPE, LocationsDatabase.LOCATION_TYPE,
                    LocationsDatabase.LOCATION_NAME, LocationsDatabase._ID, LocationsDatabase.ADDRESS
            }, null, null, null);

            if (null == cursor) {
                Log.e(TAG, " cursor is null");
            } else {
                int count = cursor.getCount();
                cursor.moveToFirst();

                int id;
                String latlngcur;
                int pt;
                int lt;
                String name;
                String address;

                for (int i = 0; i < count; i++) {
                    id = cursor.getInt(cursor.getColumnIndex(LocationsDatabase._ID));
                    pt = cursor.getInt(cursor.getColumnIndex(LocationsDatabase.PROFILE_TYPE));
                    lt = cursor.getInt(cursor.getColumnIndex(LocationsDatabase.LOCATION_TYPE));
                    latlngcur = cursor.getString(cursor.getColumnIndex(LocationsDatabase.LATLNGS));
                    name = cursor.getString(cursor.getColumnIndex(LocationsDatabase.LOCATION_NAME));
                    address = cursor.getString(cursor.getColumnIndex(LocationsDatabase.ADDRESS));

                    Log.d(TAG, "ID : " + id);
                    Log.d(TAG, "profile type : " + pt);
                    Log.d(TAG, "location type : " + lt);
                    Log.d(TAG, "latlng : " + latlngcur);
                    Log.d(TAG, "name : " + name);
                    Log.d(TAG, "address : " + address);


                    cursor.moveToNext();

                }
            }
            //todo delete - end
        }
    }
}

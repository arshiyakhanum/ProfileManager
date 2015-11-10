package com.arshiya.mapsapi.geofence.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.geofence.GeofenceCustomAdapter;
import com.arshiya.mapsapi.geofence.GeofenceErrorMessage;
import com.arshiya.mapsapi.geofence.GeofenceTransitionIntentService;
import com.arshiya.mapsapi.storage.contentprovider.LocationsContentProvider;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.ArrayList;
import java.util.List;

public class RemoveGeofences extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, GeofenceCustomAdapter.AdapterCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = RemoveGeofences.class.getSimpleName();
    private ListView mGeofencesList;
    private Cursor mGeofenceCursor;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        setCustomActionBar();

        buildGoogleApiClient();
        mGeofencesList = (ListView) findViewById(R.id.geofences_list);

    }

    private void setCustomActionBar() {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            View custom_view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null);
            TextView title = (TextView) custom_view.findViewById(R.id.custom_actionbar_title);
            title.setText("Saved Locations");
            title.setTypeface(Fonts.ROBOTOMEDIUM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(custom_view);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.backgroundColor)));
            //to remove Application icon for this activity
            actionBar.setIcon(
                    new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geofence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            removeAllGeofences();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeAllGeofences() {
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent()).setResultCallback(this);
        getLoaderManager().restartLoader(Constants.LOADER_ID, null, this);
        LocationsDatabase db = new LocationsDatabase(RemoveGeofences.this);
        int rows = db.del();
        Log.d(TAG, "deleted : " + rows + " rows.");
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Constants.LOADER_ID, null, this);
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                LocationsDatabase db = new LocationsDatabase(RemoveGeofences.this);
                mGeofenceCursor = db.getAllLocations();

                if (null == mGeofenceCursor) {
                    Log.e(TAG, " cursor is null");
                } else {
                    int count = mGeofenceCursor.getCount();
                    mGeofenceCursor.moveToFirst();

                    int id;
                    String latlngcur;
                    int pt;
                    int lt;
                    String name;
                    String address;
                    String tag;

                    for (int i = 0; i < count; i++) {
                        id = mGeofenceCursor.getInt(mGeofenceCursor.getColumnIndex(LocationsDatabase._ID));
                        pt = mGeofenceCursor.getInt(mGeofenceCursor.getColumnIndex(LocationsDatabase.PROFILE_TYPE));
                        lt = mGeofenceCursor.getInt(mGeofenceCursor.getColumnIndex(LocationsDatabase.LOCATION_TYPE));
                        latlngcur = mGeofenceCursor.getString(mGeofenceCursor.getColumnIndex(LocationsDatabase.LATLNGS));
                        name = mGeofenceCursor.getString(mGeofenceCursor.getColumnIndex(LocationsDatabase.LOCATION_NAME));
                        address = mGeofenceCursor.getString(mGeofenceCursor.getColumnIndex(LocationsDatabase.ADDRESS));
                        tag = mGeofenceCursor.getString(mGeofenceCursor.getColumnIndex(LocationsDatabase.GEOFENCE_TAG));

                        Log.d(TAG, "ID : " + id);
                        Log.d(TAG, "profile type : " + pt);
                        Log.d(TAG, "location type : " + lt);
                        Log.d(TAG, "latlng : " + latlngcur);
                        Log.d(TAG, "name : " + name);
                        Log.d(TAG, "address : " + address);
                        Log.d(TAG, " Geofence tag : " + tag);


                        mGeofenceCursor.moveToNext();

                    }
                }
                Log.d(TAG, "cursor :" + mGeofenceCursor);
                return mGeofenceCursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        GeofenceCustomAdapter geofenceCustomAdapter = new GeofenceCustomAdapter(this, mGeofenceCursor, 0);
        mGeofencesList.setAdapter(geofenceCustomAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDeleteComplete(String tag) {
        getLoaderManager().restartLoader(Constants.LOADER_ID, null, this);

        removeGeofenceByTag(tag);
    }

    private void removeGeofenceByTag(String tag) {
        List<String> tags = new ArrayList<String>();
        tags.add(tag);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, tags).setResultCallback(this);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private PendingIntent getGeofencePendingIntent() {
        if (null != mGeofencePendingIntent) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(RemoveGeofences.this, GeofenceTransitionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, " googleapiclient : connected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Geofence  : success");
        } else {
            Log.d(TAG, "Geofence  : failed");

            String errorMsg = GeofenceErrorMessage.getErrorString(RemoveGeofences.this, status.getStatusCode());
            Log.d(TAG, "Geofence error : " + errorMsg);
        }

    }
}

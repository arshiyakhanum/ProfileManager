package com.arshiya.mapsapi.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by akhanumx on 11/11/2015.
 */
public class GeofenceCallbacks {

    private static final String TAG = GeofenceCallbacks.class.getSimpleName();

    public void createGeofence(GoogleApiClient googleApiClient, PendingIntent geofencePendingIntent,
                               LatLng latLng, ResultCallback resultCallback) {
        //create a geofence for the selected latlng
        if (null == googleApiClient || !googleApiClient.isConnected()) {
            //todo error dialog
            Log.e(TAG, "error in google api client");
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(latLng),
                    geofencePendingIntent
            ).setResultCallback(resultCallback);
        } catch (SecurityException securityexception) {
            securityexception.printStackTrace();
        }
    }

    public PendingIntent getGeofencePendingIntent(Context context,PendingIntent geofencePendingIntent) {
        if (null != geofencePendingIntent) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionIntentService.class);
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private Geofence getGeofenceToAdd(LatLng latLng) {
        Geofence geofence = new Geofence.Builder().setRequestId(latLng.toString())
                .setCircularRegion(latLng.latitude, latLng.longitude, Constants.GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setLoiteringDelay(Constants.GEOFENCE_LOITERING_DELAY)
                .build();

        return geofence;
    }

    private GeofencingRequest getGeofencingRequest(LatLng latLng) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(getGeofenceToAdd(latLng))
                .build();
    }
}

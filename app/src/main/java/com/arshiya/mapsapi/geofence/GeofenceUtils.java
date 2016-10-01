package com.arshiya.mapsapi.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import static com.arshiya.mapsapi.common.Constants.GEOFENCE_RADIUS;



/**
 * Created by arshiya on 11/11/2015.
 *
 */
public class GeofenceUtils {

    private static final String TAG = GeofenceUtils.class.getSimpleName();

    /**
     * Adds the geofence to the Location Services, which will set alerts to be notified when the device
     * enters/exits the specified geofence
     * @param googleApiClient - an existing connected google api client
     * @param geofencePendingIntent - a pending intent that will be used to generate an intent when
     *                              matched geofence transition is observed
     * @param latLng - Coordinates for creating a GeofenceRequest
     * @param resultCallback - Callback to handle the result - success/failure of adding geofence
     */
    public void addGeofence(GoogleApiClient googleApiClient, PendingIntent geofencePendingIntent,
                            LatLng latLng, ResultCallback resultCallback) {
        //create a geofence for the selected latlng
        if (null == googleApiClient || !googleApiClient.isConnected()) {
            //todo error dialog
            Log.e(TAG, "error in google api client");
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(latLng),
                    geofencePendingIntent).setResultCallback(resultCallback);
        } catch (SecurityException securityexception) {
            securityexception.printStackTrace();
        }
    }

    /**
     * Creates and returns a pending intent that will be used to generate
     * an intent when matched geofence transition is observed
     * @param context
     * @param geofencePendingIntent - a pending intent
     * @return - a pending intent
     */
    public PendingIntent getGeofencePendingIntent(Context context,
                                                  PendingIntent geofencePendingIntent) {
        if (null != geofencePendingIntent) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionIntentService.class);
        geofencePendingIntent =
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    /**
     * Builds a GeofencingRequest object with the initial triggers and geofences to monitor
     * @param latLng - Co-ordinates for creating a geofence
     * @return - GeofencingRequest Object
     */
    private GeofencingRequest getGeofencingRequest(LatLng latLng) {
        return new GeofencingRequest.Builder().setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(getGeofenceToAdd(latLng)).build();
    }

    /**
     * Builds a geofence for the specified coordinates
     * @param latLng - map coordinates
     * @return geofence object
     */
    private Geofence getGeofenceToAdd(LatLng latLng) {
        Geofence geofence = new Geofence.Builder().setRequestId(latLng.toString())
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        return geofence;
    }

}

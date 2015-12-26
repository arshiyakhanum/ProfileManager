package com.arshiya.mapsapi.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arshiya.mapsapi.mapactivities.MapsActivity;
import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.profilemanager.UpdateProfile;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akhanumx on 10/30/2015.
 */
public class GeofenceTransitionIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionIntentService.class.getSimpleName();
    private ProfileManagerSharedPref mProfileManagerSharedPref;

    public GeofenceTransitionIntentService() {
        super(null);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandle");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessage.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context             The app context.
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        String pt = "";
        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            pt = "" + checkIfInSelectedLocation(geofence.getRequestId(), geofenceTransition);
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return "Profile Type " + pt + "...  " + geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "entered");
                return getString(R.string.geofence_transition_entered);


            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "exited");
                return getString(R.string.geofence_transition_exited);

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "dwelling");
                return ("In the area");
            default:
                Log.e(TAG, "unknown transition");
                return getString(R.string.unknown_geofence_transition);
        }
    }


    private String checkIfInSelectedLocation(String geofenceid, int geofenceTransition) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        switch (geofenceTransition) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                LocationsDatabase db = new LocationsDatabase(this);
                Cursor cursor = db.getProfileType(geofenceid);

                int count = cursor.getCount();
                Log.d(TAG, "count : " + count);

                if (count == 0) {
                    //do nothing
                    Log.e(TAG, "geofence tag : " + geofenceid + " is not stored ");

                } else {
                    //save the current profile
                    int currentProfile = audioManager.getRingerMode();
                    Log.d(TAG, "current profile " + currentProfile);
                    mProfileManagerSharedPref.saveRingerMode(currentProfile);

                    //save the geofence id
                    mProfileManagerSharedPref.saveCurrentGeofenceId(geofenceid);

                    if (count > 1) {
                        cursor.moveToFirst();
                        Log.e(TAG, "duplicate geofence tags, setting first geotag's profile ");
                        int profile = cursor.getInt(0);
                        Log.d(TAG, " ProfileType : " + profile);
                        new UpdateProfile(profile, audioManager);
                        db.close();
                        cursor.close();
                        return (profile + " saved");
                    } else {
                        cursor.moveToFirst();
                        Log.e(TAG, "found one geotag");
                        int profile = cursor.getInt(0);
                        Log.d(TAG, "Profile : " + profile);
                        Log.d(TAG, " ProfileType : " + profile);
                        new UpdateProfile(profile, audioManager);
                        db.close();
                        cursor.close();
                        return (profile + " saved");

                    }
                }

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //change current geofence id to null
                mProfileManagerSharedPref.saveCurrentGeofenceId(null);
                new UpdateProfile(mProfileManagerSharedPref.getSavedRingerMode(), audioManager);
                return "exited , " + mProfileManagerSharedPref.getSavedRingerMode() + " profile";


        }

        return "";
    }


}
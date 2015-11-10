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
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arshiya.mapsapi.MapsActivity;
import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
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

    public GeofenceTransitionIntentService(){
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

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
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
             pt = ""+checkIfInSelectedLocation(geofence.getRequestId(), geofenceTransition);
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return "Profile Type " + pt + "...  " + geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_action)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_action))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(notificationDetails + "\n" + getString(R.string.geofence_transition_notification_text));
//                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
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
                Log.d(TAG,  "exited");
                return getString(R.string.geofence_transition_exited);

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "dwelling");
                return ("In the area");
            default:
                Log.e(TAG,  "unknown transition");
                return getString(R.string.unknown_geofence_transition);
        }
    }


    private String checkIfInSelectedLocation(String geofenceid, int geofenceTransition) {
        switch (geofenceTransition){

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                LocationsDatabase db = new LocationsDatabase(this);
                Cursor cursor = db.getProfileType(geofenceid);

                int count = cursor.getCount();
                Log.d(TAG, "count : " + count);

                if (count == 0){
                    Log.e(TAG, "geofence tag : " + geofenceid + " is not stored ");
                    changeProfile(Constants.PROFILE_NORMAL);
                    db.close();
                    return Constants.PROFILE_NORMAL + " default";
                }else {
                    if (count > 1){
                        cursor.moveToFirst();
                        Log.e(TAG, "duplicate geofence tags, setting first geotag's profile ");
                        int profile = cursor.getInt(0);
                        Log.d(TAG, " ProfileType : " + profile);
                        changeProfile(profile);
                        db.close();
                        return (profile + " saved");
                    }else {
                        cursor.moveToFirst();
                        Log.e(TAG, "found one geotag");
                        int profile = cursor.getInt(0);
                        Log.d(TAG, "Profile : " + profile);
                        Log.d(TAG, " ProfileType : " + profile);
                        changeProfile(profile);
                        db.close();
                        return (profile + " saved");

                    }
                }


            case Geofence.GEOFENCE_TRANSITION_EXIT:
                //todo save last user's profile settings
                changeProfile(Constants.PROFILE_NORMAL);
                return  "exited , default profile";



        }

        return "";
    }


    private void changeProfile(int profile) {
        switch (profile) {
            case Constants.PROFILE_NORMAL:
                setNormalVolumeSettings();
                break;

            case Constants.PROFILE_SILENT:
                setSilentVolumesettings();
                break;

            case Constants.PROFILE_VIBRATE:
                setVibrateVolumesettings();
                break;


        }
    }

    private void setNormalVolumeSettings() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
        Bundle settings = mProfileManagerSharedPref.getNormalSettings();

        int volume = settings.getInt("normal_volume_level", Constants.NORMAL_VOLUME_LEVEL);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    private void setSilentVolumesettings() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void setVibrateVolumesettings() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

}
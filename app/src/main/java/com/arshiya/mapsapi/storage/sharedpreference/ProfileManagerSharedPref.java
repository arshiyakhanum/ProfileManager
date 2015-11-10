package com.arshiya.mapsapi.storage.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.arshiya.mapsapi.common.Constants;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by akhanumx on 10/29/2015.
 */
public class ProfileManagerSharedPref {


    private final static String TAG = "GCSharedPreference ";
    private static SharedPreferences mPreferences;
    private static Context mContext;
    private static SharedPreferences.Editor mEditor;
    private static final ProfileManagerSharedPref PROFILE_MANAGER_SHARED_PREF= new ProfileManagerSharedPref();

    /**
     * returns single object of the shared preferences.
     */

    public static ProfileManagerSharedPref gcSharedPreferenceInstance(Context context) {
        ProfileManagerSharedPref.mContext = context;
        /* get the shared preference object */
        mPreferences = mContext.getSharedPreferences("greekcanyon_CloudApp", Context.MODE_PRIVATE);
        /*get the mEditor */
        mEditor = mPreferences.edit();
        return PROFILE_MANAGER_SHARED_PREF;
    }

    private ProfileManagerSharedPref() {

    }

    public void saveAddress(String address, LatLng latLng){
        mEditor.putString("saved_latng", latLng.toString());
        mEditor.putString("saved_address", address);
        mEditor.apply();

    }
    public String getSavedAddress(LatLng latLng){
        String saved_latlng = mPreferences.getString("saved_latlng", String.valueOf(0.0));

        if (saved_latlng.equalsIgnoreCase(latLng.toString())){
            return mPreferences.getString("saved_address", "");
        }
        return "";
    }
    public Bundle getNormalSettings(){
        Bundle normalSettings = new Bundle();
        int volumeLevel = mPreferences.getInt("normal_volume_level", Constants.NORMAL_VOLUME_LEVEL);
        normalSettings.putInt("level", volumeLevel);
        return normalSettings;
    }

    public boolean isNightModeEnabled(){
        return mPreferences.getBoolean("night_mode_state", false);
    }

    public void setNightMode(boolean state){
        mEditor.putBoolean("night_mode_state", state);
        mEditor.apply();
    }

    public String getStartTimeString(){
        int hour = mPreferences.getInt("start_hour", 22);
        int minute = mPreferences.getInt("start_minute", 0);

        return hour + " : " + minute;
    }

    public String getEndTimeString(){
        int hour = mPreferences.getInt("end_hour", 7);
        int minute = mPreferences.getInt("end_minute", 0);

        return hour + " : " + minute;
    }

    public Bundle getStartTime(){
        Bundle startTime = new Bundle();

        int hour = mPreferences.getInt("start_hour", 22);
        int minute = mPreferences.getInt("start_minute", 0);

        startTime.putInt("start_hour", hour);
        startTime.putInt("start_minute", minute);

        return startTime;

    }

    public void setStartTime(Bundle startTime){
        int hour = startTime.getInt("start_hour",22);
        int minute = startTime.getInt("start_minute",0);

        mEditor.putInt("start_hour",hour);
        mEditor.putInt("start_minute",minute);
        mEditor.apply();

    }

    public Bundle getEndTime(){
        Bundle endTime = new Bundle();

        int hour = mPreferences.getInt("end_hour", 7);
        int minute = mPreferences.getInt("end_minute", 0);

        endTime.putInt("end_hour", hour);
        endTime.putInt("end_minute", minute);

        return endTime;

    }

    public void setEndTime(Bundle endTime){
        int hour = endTime.getInt("end_hour",7);
        int minute = endTime.getInt("end_minute",0);

        mEditor.putInt("end_hour",hour);
        mEditor.putInt("end_minute",minute);
        mEditor.apply();
    }

    public boolean isAlarmSet(){
        return mPreferences.getBoolean("night_alarm", false);
    }

    public void setAlarm(Boolean status){
        mEditor.putBoolean("night_alarm", status);
        mEditor.apply();
    }
}

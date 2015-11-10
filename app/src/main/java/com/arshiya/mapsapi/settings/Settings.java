package com.arshiya.mapsapi.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;

import java.util.Calendar;

public class Settings extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = Settings.class.getSimpleName();
    private CheckBox mNightModeCB;
    private LinearLayout mNightModeHolder;
    private ProfileManagerSharedPref mProfileManagerSharedPref;
    private LinearLayout mFromLayout;
    private LinearLayout mToLayout;
    private LinearLayout mScheduleHolder;
    private TextView mFromTime;
    private TextView mToTime;
    private int mFromHour;
    private int mFromMinute;
    private int mToHour;
    private int mToMinute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);

        setCustomActionBar();
        initView();

    }

    private void initView() {
        mNightModeHolder = (LinearLayout) findViewById(R.id.night_mode_holder);
        mNightModeCB = (CheckBox) findViewById(R.id.night_mode_cb);
        mFromLayout = (LinearLayout) findViewById(R.id.from_time_holder);
        mToLayout = (LinearLayout) findViewById(R.id.to_time_holder);
        mScheduleHolder = (LinearLayout) findViewById(R.id.settings_schedule_holder);
        mFromTime = (TextView)findViewById(R.id.from_time);
        mToTime = (TextView)findViewById(R.id.to_time);



        mFromLayout.setOnClickListener(this);
        mToLayout.setOnClickListener(this);
        mNightModeCB.setOnCheckedChangeListener(this);
        mNightModeHolder.setOnClickListener(this);

        mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
        mNightModeCB.setChecked(mProfileManagerSharedPref.isNightModeEnabled());

        if (mNightModeCB.isChecked()){
            showScheduleHolder();

        }else {
            mScheduleHolder.setVisibility(View.GONE);
        }

    }

    private void showScheduleHolder() {
        mScheduleHolder.setVisibility(View.VISIBLE);
        mFromTime.setText(mProfileManagerSharedPref.getStartTimeString());
        mToTime.setText(mProfileManagerSharedPref.getEndTimeString());
    }

    private void setCustomActionBar() {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            View custom_view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null);
            TextView title = (TextView) custom_view.findViewById(R.id.custom_actionbar_title);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id) {
            case R.id.night_mode_cb:
                //check box state changed,save the state and handle the change
                mProfileManagerSharedPref.setNightMode(isChecked);
                handleNightModeStateChange(isChecked);
                break;
        }


    }

    /**
     * if night mode is disabled cancel the alarms and hide the night mode options
     * if night mode enabled set the alarms and show the options
     *
     * @param isChecked
     */
    private void handleNightModeStateChange(boolean isChecked) {
        if (isChecked) {
            //set the alarms and show options
            showScheduleHolder();
            if (!mProfileManagerSharedPref.isAlarmSet()) {
                setAlarm(mProfileManagerSharedPref.getStartTime(), mProfileManagerSharedPref.getEndTime());
                mProfileManagerSharedPref.setAlarm(true);

            }
        } else {
            //cancel the alarms and hide options
            mScheduleHolder.setVisibility(View.GONE);
            mProfileManagerSharedPref.setAlarm(false);
            cancelAlarm();
        }
    }

    private void cancelAlarm() {
        Log.d(TAG, " cancel alarm");
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        /**
         * cancel start alarm
         */
        Intent startIntent = new Intent(Settings.this, NightModeStartIntentService.class);
        PendingIntent startPI = PendingIntent.getBroadcast(this, Constants.FROM_TIME_REQUEST_CODE, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarm.cancel(startPI);

        /**
         * cancel end alarm
         */
        Intent endIntent = new Intent(Settings.this, NightModeEndIntentService.class);
        PendingIntent endPI = PendingIntent.getBroadcast(this, Constants.TO_TIME_REQUEST_CODE, endIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarm.cancel(endPI);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.night_mode_holder:
                boolean state = false;
                if (mNightModeCB.isChecked()) {
                    state = false;
                } else {
                    state = true;
                }

                mNightModeCB.setChecked(state);
                break;

            case R.id.from_time_holder:
                Intent timePickerIntent = new Intent(this, TimeSetActivity.class);
                timePickerIntent.putExtra("type", Constants.FROM_TIME);
                startActivityForResult(timePickerIntent, Constants.FROM_TIME);
                break;

            case R.id.to_time_holder:
                Intent timePickerIntent2 = new Intent(this, TimeSetActivity.class);
                timePickerIntent2.putExtra("type", Constants.TO_TIME);
                startActivityForResult(timePickerIntent2, Constants.TO_TIME);
                break;

            default:
                Log.e(TAG, "invalid option");


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.FROM_TIME || requestCode == Constants.TO_TIME){
            if (RESULT_OK == resultCode){
                showScheduleHolder();
                setAlarm(mProfileManagerSharedPref.getStartTime(), mProfileManagerSharedPref.getEndTime());

            }

        }
    }


    private void setAlarm(Bundle startTimeData, Bundle endTimeData){
        int startHour;
        int startMinute;
        int endHour;
        int endMinute;
        long startTime;
        long endTime;

        startHour = startTimeData.getInt("start_hour", 0);
        startMinute = startTimeData.getInt("start_minute", 0);


        endHour = endTimeData.getInt("end_hour", 0);
        endMinute = endTimeData.getInt("end_minute", 0);

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, startHour);
        startCal.set(Calendar.MINUTE, startMinute);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, endMinute);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        startTime = startCal.getTimeInMillis();
        endTime = endCal.getTimeInMillis();

        Log.d(TAG, "start time : " + startTime);
        Log.d(TAG, "end time : " + endTime);


        /**
         * check if current time is  greater than start time
         */

        Calendar currentCal = Calendar.getInstance();
        long curTimeInMillis = currentCal.getTimeInMillis();

        if (startTime < curTimeInMillis && curTimeInMillis < endTime){
            Log.d(TAG, " start time < current time ");
            //start the service
//            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Intent intent = new Intent(Settings.this, NightModeStartIntentService.class);
            startService(intent);
        }
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        /**
         * set start alarm
         */
        Intent startIntent = new Intent(Settings.this, NightModeStartIntentService.class);
        PendingIntent startPI = PendingIntent.getService(this, Constants.FROM_TIME_REQUEST_CODE, startIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime,
                AlarmManager.INTERVAL_DAY, startPI);

        /**
         * set end alarm
         */
        Intent endIntent = new Intent(Settings.this, NightModeEndIntentService.class);
        PendingIntent endPI = PendingIntent.getService(this, Constants.TO_TIME_REQUEST_CODE, endIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, endTime,
                AlarmManager.INTERVAL_DAY, endPI);



    }
}
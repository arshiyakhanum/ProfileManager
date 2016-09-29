package com.arshiya.mapsapi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.settings.CustomInputFilter;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;

import java.util.Calendar;

public class TimeSetActivity extends Activity implements View.OnClickListener {

  private EditText mTp_hour;
  private EditText mTp_minute;
  private TextView mTitle_time;
  private TextView mTitle_text;
  private Button mTp_hour_plus;
  private Button mTp_hour_minus;
  private Button mTp_minute_plus;
  private Button mTp_minute_minus;
  private Button mSave;
  private Button mCancel;
  private final int MAX_HOUR = 23;
  private final int MAX_MINUTE = 59;
  private final int MIN_HOUR = 0;
  private final int MIN_MINUTE = 0;
  private static String TAG = " TimePickerActivity";
  private int mHourCount;
  private int mMinuteCount;
  private int mType;
  private ProfileManagerSharedPref mProfileManagerSharedPref;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.time_picker);

    mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);

    mTp_hour = (EditText) findViewById(R.id.tp_hour);
    mTp_minute = (EditText) findViewById(R.id.tp_minute);
    mTp_hour_plus = (Button) findViewById(R.id.tp_hour_plus);
    mTp_hour_minus = (Button) findViewById(R.id.tp_hour_minus);
    mTp_minute_plus = (Button) findViewById(R.id.tp_minute_plus);
    mTp_minute_minus = (Button) findViewById(R.id.tp_minute_minus);
    mTitle_time = (TextView) findViewById(R.id.from_time_selection);
    mSave = (Button) findViewById(R.id.tp_save);
    mCancel = (Button) findViewById(R.id.tp_cancel);
    mTitle_text = (TextView) findViewById(R.id.from_header);

    mTp_hour.setTypeface(Fonts.ROBOTOREGULAR);
    mTp_minute.setTypeface(Fonts.ROBOTOREGULAR);
    mTitle_time.setTypeface(Fonts.ROBOTOMEDIUM);
    mTitle_text.setTypeface(Fonts.ROBOTOMEDIUM);
    mCancel.setTypeface(Fonts.ROBOTOMEDIUM);
    mSave.setTypeface(Fonts.ROBOTOMEDIUM);

    mTp_minute.setFilters(new InputFilter[] { new CustomInputFilter("0", "59") });
    mTp_hour.setFilters(new InputFilter[] { new CustomInputFilter("0", "23") });

    mType = getIntent().getIntExtra("type", 0);

    if (mType == Constants.FROM_TIME) {
      mTitle_time.setText("Start time");
    } else {
      mTitle_time.setText("End time");
    }

    Calendar now = Calendar.getInstance();

    mHourCount = now.get(Calendar.HOUR_OF_DAY);
    mMinuteCount = now.get(Calendar.MINUTE);
    mTp_hour.setText(String.valueOf(mHourCount));
    mTp_minute.setText(String.valueOf(mMinuteCount));

    mSave.setOnClickListener(this);

    mCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        finish();
      }
    });
  }

  public void OnClick(View view) {
    switch (view.getId()) {
      case R.id.tp_hour_plus:
        Log.i(TAG, "tp_hour_plus");
        if (mHourCount == MAX_HOUR) {
          mHourCount = MIN_HOUR;
        } else {
          mHourCount++;
        }
        Log.d(TAG, "count : " + mHourCount);
        mTp_hour.setText(String.valueOf(mHourCount));
        break;
      case R.id.tp_hour_minus:
        Log.i(TAG, "tp_hour_minus");
        if (mHourCount == MIN_HOUR) {
          mHourCount = MAX_HOUR;
        } else {
          mHourCount--;
        }
        Log.d(TAG, "count : " + mHourCount);
        mTp_hour.setText(String.valueOf(mHourCount));
        break;
      case R.id.tp_minute_plus:
        Log.i(TAG, "tp_minute_plus");
        if (mMinuteCount == MAX_MINUTE) {
          mMinuteCount = MIN_MINUTE;
        } else {
          mMinuteCount++;
        }
        mTp_minute.setText(String.valueOf(mMinuteCount));
        break;
      case R.id.tp_minute_minus:
        Log.i(TAG, "tp_minute_minus");
        if (mMinuteCount == MIN_MINUTE) {
          mMinuteCount = MAX_MINUTE;
        } else {
          mMinuteCount--;
        }
        mTp_minute.setText(String.valueOf(mMinuteCount));
        break;
    }
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.tp_save:
        Intent returnIntent = new Intent();
        returnIntent.putExtra("tp_hour", mHourCount);
        returnIntent.putExtra("tp_minute", mMinuteCount);
        setResult(Activity.RESULT_OK, returnIntent);
        saveTime();
        finish();
        break;

      case R.id.tp_cancel:
        finish();
        break;
    }
  }

  private void saveTime() {
    switch (mType) {
      case Constants.FROM_TIME:
        Bundle startBundle = new Bundle();
        startBundle.putInt("start_hour", Integer.parseInt(mTp_hour.getText().toString()));
        startBundle.putInt("start_minute", Integer.parseInt(mTp_minute.getText().toString()));
        mProfileManagerSharedPref.setStartTime(startBundle);
        break;

      case Constants.TO_TIME:
        Bundle endBundle = new Bundle();
        endBundle.putInt("end_hour", Integer.parseInt(mTp_hour.getText().toString()));
        endBundle.putInt("end_minute", Integer.parseInt(mTp_minute.getText().toString()));
        mProfileManagerSharedPref.setEndTime(endBundle);
        break;
    }
  }
}

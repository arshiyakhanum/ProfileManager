package com.arshiya.mapsapi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.common.Fonts;

public class LocationDetailsGetterActivity extends Activity implements View.OnClickListener {

  private static final String TAG = LocationDetailsGetterActivity.class.getSimpleName();
  private RadioButton mSilent;
  private RadioButton mNormal;
  private RadioButton mVibrate;
  private Button mOk;
  private Button mCancel;
  private int mCurrentSelection;
  private EditText mLocationName;
  private final int CANCEL = 0;
  private final int OK = 1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_details_getter);

    mCurrentSelection = Constants.PROFILE_SILENT;
    mSilent = (RadioButton) findViewById(R.id.rb_silent);
    mNormal = (RadioButton) findViewById(R.id.rb_normal);
    mVibrate = (RadioButton) findViewById(R.id.rb_vibrate);
    mLocationName = (EditText) findViewById(R.id.area_name);

    mOk = (Button) findViewById(R.id.profile_type_option_dialog_ok);
    mCancel = (Button) findViewById(R.id.profile_type_option_dialog_cancel);

    TextView profileTypeHeader = (TextView) findViewById(R.id.profile_type_header);

    mLocationName.setTypeface(Fonts.ROBOTOREGULAR);
    mSilent.setTypeface(Fonts.ROBOTOREGULAR);
    mNormal.setTypeface(Fonts.ROBOTOREGULAR);
    mVibrate.setTypeface(Fonts.ROBOTOREGULAR);
    profileTypeHeader.setTypeface(Fonts.ROBOTOMEDIUM);
    mOk.setTypeface(Fonts.ROBOTOMEDIUM);
    mCancel.setTypeface(Fonts.ROBOTOMEDIUM);

    mSilent.setChecked(true);
    mSilent.setOnClickListener(this);
    mNormal.setOnClickListener(this);
    mVibrate.setOnClickListener(this);
    mOk.setOnClickListener(this);
    mCancel.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    int id = v.getId();

    switch (id) {
      case R.id.rb_silent:
        mCurrentSelection = Constants.PROFILE_SILENT;
        break;

      case R.id.rb_normal:
        mCurrentSelection = Constants.PROFILE_NORMAL;
        break;

      case R.id.rb_vibrate:
        mCurrentSelection = Constants.PROFILE_VIBRATE;
        break;

      case R.id.profile_type_option_dialog_ok:
        setUpIntent(OK);
        finish();
        break;

      case R.id.profile_type_option_dialog_cancel:
        setUpIntent(CANCEL);
        finish();
        break;

      default:
        Log.e(TAG, "invalid selection");
    }
  }

  private void setUpIntent(int status) {
    Intent intent = new Intent();
    intent.putExtra("STATUS", status);
    intent.putExtra("PROFILE", mCurrentSelection);

    Log.d("Current selection ", "" + mCurrentSelection);
    String location_name = "";
    location_name = mLocationName.getText().toString();

    if (location_name.equalsIgnoreCase("")) {
      location_name = "Unknown location";
    }
    intent.putExtra("LOCATION_NAME", location_name);
    setResult(5, intent);
  }

  @Override public void onBackPressed() {
    setUpIntent(CANCEL);
    super.onBackPressed();
  }
}

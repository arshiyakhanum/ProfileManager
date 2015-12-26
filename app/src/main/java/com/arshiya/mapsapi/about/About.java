package com.arshiya.mapsapi.about;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Fonts;

public class About extends Activity {

    private static final String TAG = About.class.getSimpleName();
    private TextView mAppName;
    private TextView mVersionTitle;
    private TextView mVersionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAppName = (TextView) findViewById(R.id.about_app_name);
        mVersionNumber = (TextView) findViewById(R.id.about_version_number);
        mVersionTitle = (TextView) findViewById(R.id.about_version_number_title);

        mAppName.setTypeface(Fonts.ROBOTOMEDIUM);
        mVersionNumber.setTypeface(Fonts.ROBOTOMEDIUM);
        mVersionTitle.setTypeface(Fonts.ROBOTOMEDIUM);

    }

}

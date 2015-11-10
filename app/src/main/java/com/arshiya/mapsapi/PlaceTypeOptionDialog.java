package com.arshiya.mapsapi;

import android.app.Activity;
import android.app.usage.ConfigurationStats;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.arshiya.mapsapi.common.Constants;

public class PlaceTypeOptionDialog extends Activity implements View.OnClickListener {

    private static final String TAG = PlaceTypeOptionDialog.class.getSimpleName();
    private RadioButton mAreaButton;
    private RadioButton mPlacesButton;
    private Button mOk;
    private Button mCancel;
    private int mCurrentSelection;
    private final int CANCEL =0;
    private final int OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_type_option_dialog);

        mCurrentSelection = -1;
        mAreaButton = (RadioButton) findViewById(R.id.rb_area);
        mPlacesButton = (RadioButton) findViewById(R.id.rb_place);
        mOk = (Button) findViewById(R.id.place_type_option_dialog_ok);
        mCancel = (Button) findViewById(R.id.place_type_option_dialog_cancel);


        mAreaButton.setOnClickListener(this);
        mPlacesButton.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.rb_area:
                mCurrentSelection = Constants.LOCATION_AREA;
                break;

            case R.id.rb_place:
                mCurrentSelection = Constants.LOCATION_AREA;
                break;

            case R.id.place_type_option_dialog_ok:
                setUpIntent(OK);
                finish();
                break;

            case R.id.place_type_option_dialog_cancel:
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
        intent.putExtra("SELECTION", mCurrentSelection);
        setResult(4, intent);

    }
}

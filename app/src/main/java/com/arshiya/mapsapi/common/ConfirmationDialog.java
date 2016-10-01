package com.arshiya.mapsapi.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arshiya.mapsapi.R;

import static com.arshiya.mapsapi.common.Constants.ACTION_OK;
import static com.arshiya.mapsapi.common.Constants.ACTON_CANCEL;

/**
 * Created by arshiya on 11/11/2015.
 */

public class ConfirmationDialog {

  private static final String TAG = ConfirmationDialog.class.getSimpleName();
  private static AlertDialog.Builder mBuilder;
  private static AlertDialog mDialog;
  private static OnClickCallback mOnClickCallback;

  public static AlertDialog getDialog(Context context, String title, String description,
                                      String query) {
    LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View dialogView = inflater.inflate(R.layout.confirmation_pop_up, null);

    mOnClickCallback = (OnClickCallback) context;

    //build alert dialog and alert dialog builder
    mBuilder = new AlertDialog.Builder(context);
    mDialog = mBuilder.create();

    mDialog.setView(dialogView);

    TextView titleTV = (TextView) dialogView.findViewById(R.id.ca_title);
    TextView contentTV = (TextView) dialogView.findViewById(R.id.ca_description);
    TextView queryTV = (TextView) dialogView.findViewById(R.id.ca_assert);
    Button ok = (Button) dialogView.findViewById(R.id.ca_ok);
    Button cancel = (Button) dialogView.findViewById(R.id.ca_cancel);

    titleTV.setTypeface(Fonts.ROBOTOMEDIUM);
    contentTV.setTypeface(Fonts.ROBOTOREGULAR);
    ok.setTypeface(Fonts.ROBOTOMEDIUM);
    cancel.setTypeface(Fonts.ROBOTOMEDIUM);

    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnClickCallback.onClickCD(ACTION_OK);
      }
    });
    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOnClickCallback.onClickCD(ACTON_CANCEL);
      }
    });
    mDialog.setView(dialogView);

    titleTV.setText(title);
    contentTV.setText(description);
    queryTV.setText(query);
    return mDialog;
  }

  public interface OnClickCallback {
    public void onClickCD(int state);
  }
}

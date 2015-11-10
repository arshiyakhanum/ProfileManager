package com.arshiya.mapsapi.networkstatuschecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.arshiya.mapsapi.errordisplay.AlertDialogClass;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by akhanumx on 10/22/2015.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private String TAG = "NetworkChangeBroadcastReceiver";
    private static GregorianCalendar START_TIME;

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        boolean is_connected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());

        if (!is_connected) {
            Log.d(TAG, " : connected to network : " + is_connected);

            GregorianCalendar current_time = new GregorianCalendar();

            if (START_TIME == null ||
                    START_TIME.compareTo(current_time) == 0) {
                //reset the start time and show the alert
                START_TIME = new GregorianCalendar();
                START_TIME.add(Calendar.MINUTE, 1);
                Log.e(TAG, "Please check your internet connection");
//                AlertDialogClass.showAlert(ProfileManagerApplication.getContext(), "Please check your internet connection");
            }else {
                Log.v(TAG, "start : " + START_TIME.getTime() + " current : " + current_time.getTime());
            }
        } else

        {
            Log.d(TAG, " : connected to network : " + is_connected);
        }

    }

}

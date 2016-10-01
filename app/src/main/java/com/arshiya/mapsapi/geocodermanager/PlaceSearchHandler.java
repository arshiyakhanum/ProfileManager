package com.arshiya.mapsapi.geocodermanager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static com.arshiya.mapsapi.common.Constants.ACTION_SEARCH;


/**
 * Created by arshiya on 10/30/2015.
 * Process the search action - converts the input named location string to an array of addresses
 */
public class PlaceSearchHandler {

    private static final String TAG = PlaceSearchHandler.class.getSimpleName();
    private Context mContext;
    private static PlaceSearchHandler mPlaceSearchHandler = new PlaceSearchHandler();
    private Messenger mMessenger;

    private PlaceSearchHandler() {
    }

    public static PlaceSearchHandler getInstance() {
        return mPlaceSearchHandler;
    }

    public void search(Context context, String searchString, Messenger messenger) {
        mContext = context;
        mMessenger = messenger;
        new GeoCoderTask().execute(searchString);
    }

    /**
     * Asynchronously fetches an array of addresses that are known to describe named Location
     * and sends the result back to the result handler
     */
    private class GeoCoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Log.d("GeoCoderTask", "address : " + locationName);
            Geocoder geocoder = new Geocoder(mContext);
            List<Address> addresses = null;

            try {
                Log.d("GeoCoderTask", "address : " + locationName[0]);
                addresses = geocoder.getFromLocationName(locationName[0], 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (null == addresses || 0 == addresses.size()) {
                Toast.makeText(mContext, "No location found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "got addresses");
            }
            Message result = Message.obtain();
            result.obj = addresses;
            result.arg1 = ACTION_SEARCH;

            try {
                mMessenger.send(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}

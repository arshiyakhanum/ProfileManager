package com.arshiya.mapsapi.locationdatamanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Messenger;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by akhanumx on 11/9/2015.
 */
public class PlacesAutocomplete {

    private static final String TAG = PlacesAutocomplete.class.getSimpleName();
    private static PlacesAutocomplete mPlacesAutocomplete = new PlacesAutocomplete();
    private PlacesTask mPlacesTask;
    private static Context mContext;
    private AutocompleteResultParser mAutocompleteResultParser;
    private Messenger mMessenger;

    private PlacesAutocomplete() {
    }

    public static PlacesAutocomplete getInstance() {
        return mPlacesAutocomplete;
    }

    public void startTask(String place, Context context, Messenger messenger) {
        mContext = context;
        mMessenger = messenger;
        mPlacesTask = new PlacesTask();
        mPlacesTask.execute(place);
    }

    public void stopTask() {
        if (null != mPlacesTask) {
            AsyncTask.Status placesTaskStatus = mPlacesTask.getStatus();

            if (placesTaskStatus == AsyncTask.Status.RUNNING || placesTaskStatus == AsyncTask.Status.PENDING) {
                mPlacesTask.cancel(true);
            }
            if (null != mAutocompleteResultParser) {
                mAutocompleteResultParser.stopTask();
            }
        }
    }


    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            Log.d("PlacesTask", " place : " + place);
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyDwQPWM53Jffxg1hVelNV86mqeJu35yAIo";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from we service
                data = DownloadUrl.downloadUrl(url);
                Log.d("PlacesTask", "data :" + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            // Starting Parsing the JSON string returned by Web Service

            mAutocompleteResultParser = AutocompleteResultParser.getInstance();
            mAutocompleteResultParser.startTask(result, mContext, mMessenger);

        }
    }
}

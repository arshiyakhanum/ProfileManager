package com.arshiya.mapsapi;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.arshiya.mapsapi.common.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by akhanumx on 11/10/2015.
 */
public class AutocompleteResultParser {

    private static final String TAG = AutocompleteResultParser.class.getSimpleName();
    private static AutocompleteResultParser mAutocompleteResultParser = new AutocompleteResultParser();
    private static Context mContext;
    private ParserTask mParserTask;
    private Messenger mMessenger;

    private AutocompleteResultParser() {
    }

    public static AutocompleteResultParser getInstance() {
        return mAutocompleteResultParser;
    }

    public void startTask(String placesResult, Context context, Messenger messenger) {
        mContext = context;
        mParserTask = new ParserTask();
        mMessenger = messenger;
        mParserTask.execute(placesResult);
    }

    public void stopTask() {
        if (null != mParserTask) {
            AsyncTask.Status placesTaskStatus = mParserTask.getStatus();

            if (placesTaskStatus == AsyncTask.Status.RUNNING || placesTaskStatus == AsyncTask.Status.PENDING) {
                mParserTask.cancel(true);
            }
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jsonObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlacesResponseParser placeJsonParser = new PlacesResponseParser();

            try {
                jsonObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parsePlace(jsonObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(mContext, result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            Message message = new Message();
            message.arg1 = Constants.ACTION_AUTOCOMPLETE_RESULT;
            message.obj = adapter;
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}

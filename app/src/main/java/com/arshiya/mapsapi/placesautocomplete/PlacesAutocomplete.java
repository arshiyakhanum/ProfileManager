package com.arshiya.mapsapi.placesautocomplete;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.SimpleAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import static com.arshiya.mapsapi.common.Constants.ACTION_AUTOCOMPLETE_RESULT;
import static com.arshiya.mapsapi.common.Constants.BROWSER_API_KEY;
import static com.arshiya.mapsapi.common.Constants.GOOGLE_PLACES_AUTOCOMPLETE_URL;

/**
 * Created by arshiya on 11/9/2015.
 */
public class PlacesAutocomplete {

  private static final String TAG = PlacesAutocomplete.class.getSimpleName();
  private static PlacesAutocomplete mPlacesAutocomplete = new PlacesAutocomplete();
  private PlacesTask mPlacesTask;
  private static Context mContext;
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

      if (placesTaskStatus == AsyncTask.Status.RUNNING
              || placesTaskStatus == AsyncTask.Status.PENDING) {
        mPlacesTask.cancel(true);
      }
    }
  }

  /**
   * Fetches all places from GooglePlaces AutoComplete Web Service
   */

  private class PlacesTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

    @Override
    protected List<HashMap<String, String>> doInBackground(String... place) {
      Log.d("PlacesTask", " place : " + place);
      // For storing data from web service
      String data = "";
      List<HashMap<String, String>> result = null;
      try {
        // Fetching the data from we service
        String url = prepareUrl(place[0]);
        data = PlacesDataFromUrl.getData(url);

        PlacesAutocompleteResultParser parser = new PlacesAutocompleteResultParser();
        result = parser.parse(data);
        Log.d("PlacesTask", "data :" + data);
      } catch (Exception e) {
        Log.d("Background Task", e.toString());
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {
      super.onPostExecute(result);
      sendResult(result);
    }
  }

  private String prepareUrl(String place) {
    // Obtain browser key from https://code.google.com/apis/console
    String input = "";

    try {
      input = "input=" + URLEncoder.encode(place, "utf-8");
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    // place type to be searched
    String types = "types=geocode";

    // Sensor enabled
    String sensor = "sensor=false";

    // Building the parameters to the web service
    String parameters = input + "&" + types + "&" + sensor + "&" + BROWSER_API_KEY;

    // Output format
    String output = "json";

    // Building the url to the web service
    String url = GOOGLE_PLACES_AUTOCOMPLETE_URL + output + "?" + parameters;

    return url;
  }

  private void sendResult(List<HashMap<String, String>> result) {

    String[] from = new String[]{"description"};
    int[] to = new int[]{android.R.id.text1};

    // Creating a SimpleAdapter for the AutoCompleteTextView
    SimpleAdapter adapter =
            new SimpleAdapter(mContext, result, android.R.layout.simple_list_item_1, from, to);

    // Setting the adapter
    Message message = new Message();
    message.arg1 = ACTION_AUTOCOMPLETE_RESULT;
    message.obj = adapter;
    try {
      mMessenger.send(message);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
}

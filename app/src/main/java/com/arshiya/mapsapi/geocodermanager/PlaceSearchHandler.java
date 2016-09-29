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
import com.arshiya.mapsapi.common.Constants;
import java.io.IOException;
import java.util.List;

/**
 * Created by akhanumx on 10/30/2015.
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

  private class GeoCoderTask extends AsyncTask<String, Void, List<Address>> {

    @Override protected List<Address> doInBackground(String... locationName) {
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

    @Override protected void onPostExecute(List<Address> addresses) {
      if (null == addresses || 0 == addresses.size()) {
        Toast.makeText(mContext, "No location found", Toast.LENGTH_SHORT).show();
      } else {
        Log.d(TAG, "got addresses");
      }
      Message result = Message.obtain();
      result.obj = addresses;
      result.arg1 = Constants.ACTION_SEARCH;

      try {
        mMessenger.send(result);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}

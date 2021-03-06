package com.arshiya.mapsapi.geocodermanager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

import static com.arshiya.mapsapi.common.Constants.ACTION_FETCH_ADDRESS;
import static com.arshiya.mapsapi.common.Constants.FETCH_FAILED;
import static com.arshiya.mapsapi.common.Constants.FETCH_SUCCESS;


/**
 * Created by arshiya on 10/30/2015.
 * Converts the geographic co-ordinates to human readable address
 */
public class GeoCoderFetchAddress {

  private static final String TAG = GeoCoderFetchAddress.class.getCanonicalName();
  private Context mContext;
  private static GeoCoderFetchAddress mGeoCoderFetchAddress = new GeoCoderFetchAddress();
  private Messenger mMessenger;
  private LatLng mLatLng;

  private GeoCoderFetchAddress() {
  }

  public static GeoCoderFetchAddress getInstance() {
    return mGeoCoderFetchAddress;
  }

  public void init(Context context, LatLng latLng, Messenger messenger) {
    mContext = context;
    mMessenger = messenger;

    new GeoCoderFetchAddressTask().execute(latLng);
  }

  /**
   * Processes the LatLng and sends the result back to the Handler
   */
  private class GeoCoderFetchAddressTask extends AsyncTask<LatLng, Void, String> {

    @Override
    protected String doInBackground(LatLng... params) {
      mLatLng = params[0];
      Geocoder geocoder = new Geocoder(mContext);
      ArrayList<Address> addressList;
      Address address;
      String retAddress = "";
      try {
        addressList =
                (ArrayList<Address>) geocoder.getFromLocation(params[0].latitude, params[0].longitude,
                        1);

        if (0 != addressList.size()) {
          address = addressList.get(0);
          Log.d(TAG, "Address : " + address);

          if (null != address) {
            retAddress = address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " +
                    address.getAddressLine(2) + ", " + address.getAddressLine(3);
          }
          Log.d(TAG, "Address : " + address);
        } else {
          Log.e(TAG, "Address fetch failed ");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      return retAddress;
    }

    @Override
    protected void onPostExecute(String address) {
      Message message = Message.obtain();
      int status = FETCH_SUCCESS;

      if (address.equalsIgnoreCase("")) {
        address = "Address unknown : \nlatitude : "
                + mLatLng.latitude
                + "\nlongitude : "
                + mLatLng.longitude
                + "\n";
        status = FETCH_FAILED;
      }
      message.obj = address;
      message.arg1 = ACTION_FETCH_ADDRESS;
      message.arg2 = status;

      try {
        mMessenger.send(message);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
package com.arshiya.mapsapi.placesautocomplete;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arshiya on 10/27/2015.
 */
public class PlacesDataFromUrl {

  private static final String TAG = PlacesDataFromUrl.class.getSimpleName();

  public static String getData(String urlString) {
    Log.d(TAG, "url : " + urlString);
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try {
      URL url = new URL(urlString);

      // Creating an http connection to communicate with url
      urlConnection = (HttpURLConnection) url.openConnection();

      // Connecting to url
      urlConnection.connect();

      // Reading data from url
      iStream = urlConnection.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
      StringBuilder sb = new StringBuilder();

      String line = "";
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

      data = sb.toString();
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (iStream != null) {
        try {
          iStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
    Log.d(TAG, " url : " + urlString + "\n data : " + data);
    return data;
  }
}

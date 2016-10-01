package com.arshiya.mapsapi.common;

/**
 * Created by arshiya on 10/28/2015.
 */
public interface Constants {

  String GOOGLE_PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/";
  String BROWSER_API_KEY = "key=AIzaSyDwQPWM53Jffxg1hVelNV86mqeJu35yAIo";

  int RINGER_MODE_NORMAL = 2;
  int RINGER_MODE_VIBRATE = 1;
  int RINGER_MODE_SILENT = 0;

  int ACTION_SEARCH = 10;
  int ACTION_FETCH_ADDRESS = 11;
  int ACTION_AUTOCOMPLETE = 12;
  int ACTION_AUTOCOMPLETE_RESULT = 13;
  int ACTION_OK = 14;
  int ACTON_CANCEL = 15;

  int FETCH_FAILED = 30;
  int FETCH_SUCCESS = 31;
  int MAX_SIZE_ERROR = 32;
  int INSERT_ERROR = 33;
  int INSERT_SUCCESS = 34;


  int LOCATION_AREA = 0;
  int LOCATION_SINGLE = 1;

  int GEOFENCE_RADIUS = 100;
  int GEOFENCE_LOITERING_DELAY = 3 * 1000;


  int LOADER_ID = 20;
  int MAX_GEOFENCE_SIZE = 100;

  /**
   * time picker constants
   */
  int FROM_TIME = 40;
  int TO_TIME = 41;

  /**
   * night mode pending intent IDs
   */
  int FROM_TIME_REQUEST_CODE = 50;
  int TO_TIME_REQUEST_CODE = 51;

}

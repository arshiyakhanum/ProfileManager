package com.arshiya.mapsapi.common;

/**
 * Created by akhanumx on 10/28/2015.
 */
public class Constants {

  public static final int PROFILE_NORMAL = 2;
  public static final int PROFILE_VIBRATE = 1;
  public static final int PROFILE_SILENT = 0;

  public static final int LOCATION_AREA = 0;
  public static final int LOCATION_SINGLE = 1;

  public static int NORMAL_VOLUME_LEVEL = 5;

  public static final int GEOFENCE_RADIUS = 100;
  public static final int GEOFENCE_LOITERING_DELAY = 3 * 1000;

  public static final int ACTION_SEARCH = 10;
  public static final int ACTION_FETCH_ADDRESS = 11;
  public static final int ACTION_AUTOCOMPLETE = 12;
  public static final int ACTION_AUTOCOMPLETE_RESULT = 13;
  public static final int ACTION_OK = 14;
  public static final int ACTON_CANCEL = 15;

  public static final int LOADER_ID = 20;
  public static final int MAX_GEOFENCE_SIZE = 100;

  public static final int FETCH_FAILED = 30;
  public static final int FETCH_SUCCESS = 31;
  public static final int MAX_SIZE_ERROR = 32;
  public static final int INSERT_ERROR = 33;
  public static final int INSERT_SUCCESS = 34;

  /**
   * time picker constants
   */
  public static final int FROM_TIME = 40;
  public static final int TO_TIME = 41;

  /**
   * night mode pending intent IDs
   */
  public static final int FROM_TIME_REQUEST_CODE = 50;
  public static final int TO_TIME_REQUEST_CODE = 51;
}

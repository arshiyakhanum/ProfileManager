package com.arshiya.mapsapi.storage;

import android.content.Context;
import android.net.Uri;

/**
 * @author Umang Chamaria
 */

public final class ProfileManagerDataContract {

  interface LocationColumns {
    String _ID = "_id";
    String LOCATION_NAME = "location_name";
    String GEOFENCE_TAG = "geofence_tag";
    String ADDRESS = "address";
    String LATLNGS = "ltlngs";
    String PROFILE_TYPE = "profile_type";
    String LOCATION_TYPE = "location_type";

    // to be used only when using the default projection. Directly querying on the index fastens
    // the query.
    int COLUMN_INDEX_ID = 0;
    int COLUMN_INDEX_LOCATION_NAME = 1;
    int COLUMN_INDEX_GEOFENCE_TAG = 2;
    int COLUMN_INDEX_ADDRESS = 3;
    int COLUMN_INDEX_LATLNGS = 4;
    int COLUMN_INDEX_PROFILE_TYPE = 5;
    int COLUMN_INDEX_LOCATION_TYPE = 6;
  }

  public static final class LocationEntity implements LocationColumns{

    /**
     * Returns the content uri for the message table
     *
     * @param context application context
     * @return content uri of UserAttribute table
     */
    public static Uri getContentUri(Context context) {
      return Uri.parse("content://" + getAuthority(context) + "/locationtable");
    }

    /**
     * The MIME type of CONTENT_URI providing a directory of
     * messages.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pm.locationtable";
    /**
     * The MIME type of a CONTENT_URI sub-directory of a single
     * message.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pm.locationtable";

    public static final String[] PROJECTION = {
        _ID, LOCATION_NAME, GEOFENCE_TAG, ADDRESS, LATLNGS, PROFILE_TYPE, LOCATION_TYPE
    };
  }

  private static final String AUTHORITY_PART = ".pm.provider";

  public static final String QUERY_PARAMETER_LIMIT = "LIMIT";
  /**
   * This authority is used for writing to or querying from the
   * provider. Note: This is set at first run and cannot be changed without
   * breaking apps that access the provider.
   */
  private static String AUTHORITY = null;

  /**
   * @param context application context
   * @return Authority string pointing to the Provider
   */
  public static String getAuthority(Context context) {
    if (null == AUTHORITY) {
      String packageName = context.getPackageName();
      AUTHORITY = packageName + AUTHORITY_PART;
    }
    return AUTHORITY;
  }

}

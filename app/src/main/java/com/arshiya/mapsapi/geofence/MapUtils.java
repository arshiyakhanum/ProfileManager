package com.arshiya.mapsapi.geofence;

import android.content.Context;
import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by akhanumx on 11/11/2015.
 */
public class MapUtils {

  public static CircleOptions getCircle(Context context, LatLng latLng) {

    return new CircleOptions().center(latLng)
        .radius(Constants.GEOFENCE_RADIUS)
        .strokeColor(context.getResources().getColor(R.color.circle_ouline_blue))
        .fillColor(context.getResources().getColor(R.color.circle_stroke_blue));
  }
}

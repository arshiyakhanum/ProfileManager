package com.arshiya.mapsapi.geofence;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arshiya on 11/6/2015.
 */
public class MapDataProvider {

    private static final String TAG = MapDataProvider.class.getSimpleName();
    private static ArrayList<CircleOptions> mCircleOptionsArrayList;
    private static ArrayList<MarkerOptions> mMarkerOptionsArrayList;
    private Context mContext;

    public MapDataProvider(Context context) {
        mContext = context;
    }

    /**
     * Prepared the geofence marker data
     */
    public void prepareMapData() {
        mCircleOptionsArrayList = new ArrayList<CircleOptions>();
        mMarkerOptionsArrayList = new ArrayList<MarkerOptions>();

        LocationsDatabase db = new LocationsDatabase(mContext);
        Cursor cursor = db.getAllLocations();
        int count = 0;
        count = cursor.getCount();
        if (0 == count) {
            Log.i(TAG, "no geofences added yet");
            mCircleOptionsArrayList = null;
            mMarkerOptionsArrayList = null;
        } else {

            cursor.moveToFirst();

            String latlngcur;
            int pt;
            String name;

            int i;
            for (i = 0; i < count; i++) {
                pt = cursor.getInt(cursor.getColumnIndex(LocationsDatabase.PROFILE_TYPE));
                latlngcur = cursor.getString(cursor.getColumnIndex(LocationsDatabase.LATLNGS));
                name = cursor.getString(cursor.getColumnIndex(LocationsDatabase.LOCATION_NAME));

                JSONObject latlngJsonObject;
                double lat = 0;
                double lng = 0;
                try {
                    latlngJsonObject = new JSONObject(latlngcur);
                    lat = (Double) latlngJsonObject.get("lat");
                    lng = (Double) latlngJsonObject.get("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LatLng latLng = new LatLng(lat, lng);
                Log.d(TAG, "profile type : " + pt);
                Log.d(TAG, "latlng : " + latlngcur);
                Log.d(TAG, "name : " + name);

                //get the color
                //                int outline = getOutlineColor(pt);
                //                int stroke = getStrokeColor(pt);

                CircleOptions circleOptions = getCircle(pt, latLng);
                //                        new CircleOptions().center(latLng)
                //                        .radius(Constants.GEOFENCE_RADIUS)
                //                        .strokeColor(outline)
                //                        .fillColor(stroke);

                //Marker option
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                //add the marker optins a circle options
                mCircleOptionsArrayList.add(circleOptions);
                mMarkerOptionsArrayList.add(markerOptions);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public CircleOptions getCircle(int profileType, LatLng latLng) {
        int outline = getOutlineColor(profileType);
        int stroke = getStrokeColor(profileType);

        return new CircleOptions().center(latLng)
                .radius(Constants.GEOFENCE_RADIUS)
                .strokeColor(outline)
                .fillColor(stroke);
    }

    private int getOutlineColor(int pt) {
        int color = mContext.getResources().getColor(R.color.red_outer);
        switch (pt) {
            case Constants.PROFILE_NORMAL:
                color = mContext.getResources().getColor(R.color.green);
                break;

            case Constants.PROFILE_SILENT:
                color = mContext.getResources().getColor(R.color.red_outer);
                break;

            case Constants.PROFILE_VIBRATE:
                color = mContext.getResources().getColor(R.color.circle_ouline_blue);
                break;
        }
        return color;
    }

    private int getStrokeColor(int pt) {
        int color = mContext.getResources().getColor(R.color.redO30);
        switch (pt) {
            case Constants.PROFILE_NORMAL:
                color = mContext.getResources().getColor(R.color.greenO30);
                break;

            case Constants.PROFILE_SILENT:
                color = mContext.getResources().getColor(R.color.redO30);
                break;

            case Constants.PROFILE_VIBRATE:
                color = mContext.getResources().getColor(R.color.circle_stroke_blue);
                break;
        }
        return color;
    }

    public static CircleOptions getCircle(Context context, LatLng latLng) {

        return new CircleOptions().center(latLng)
                .radius(Constants.GEOFENCE_RADIUS)
                .strokeColor(context.getResources().getColor(R.color.circle_ouline_blue))
                .fillColor(context.getResources().getColor(R.color.circle_stroke_blue));
    }

    public ArrayList<CircleOptions> getCircleOptions() {
        return mCircleOptionsArrayList;
    }

    public ArrayList<MarkerOptions> getMarkerOptions() {
        return mMarkerOptionsArrayList;
    }
}

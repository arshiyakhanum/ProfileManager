package com.arshiya.mapsapi.geofence;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.profilemanager.UpdateProfile;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;

/**
 * Created by akhanumx on 11/2/2015.
 */
public class GeofenceCustomAdapter extends CursorAdapter {

  private static final String TAG = GeofenceCustomAdapter.class.getSimpleName();
  private AdapterCallback mAdapterCallback;
  private Context mContext;
  private ProfileManagerSharedPref mManagerSharedPref;

  public GeofenceCustomAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    try {
      this.mAdapterCallback = ((AdapterCallback) context);
      mContext = context;
      mManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(mContext);
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement AdapterCallback.");
    }
  }

  @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View retView = inflater.inflate(R.layout.geofence_list_item, parent, false);
    TextView locationName = (TextView) retView.findViewById(R.id.location_name);
    TextView address = (TextView) retView.findViewById(R.id.geofences_address);
    Button remove = (Button) retView.findViewById(R.id.remove_geofence_list_item);

    locationName.setTypeface(Fonts.ROBOTOMEDIUM);
    address.setTypeface(Fonts.ROBOTOREGULAR);
    remove.setTypeface(Fonts.ROBOTOMEDIUM);

    return retView;
  }

  @Override public void bindView(View view, final Context context, final Cursor cursor) {
    final TextView address = (TextView) view.findViewById(R.id.geofences_address);
    final TextView locationName = (TextView) view.findViewById(R.id.location_name);

    locationName.setText(cursor.getString(cursor.getColumnIndex(LocationsDatabase.LOCATION_NAME)));
    address.setText(cursor.getString(cursor.getColumnIndex(LocationsDatabase.ADDRESS)));

    Button delete = (Button) view.findViewById(R.id.remove_geofence_list_item);

    delete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //get the tag
        String addressString = address.getText().toString();
        String locationNameString = locationName.getText().toString();

        String tag = "";
        LocationsDatabase db = new LocationsDatabase(mContext);
        Cursor cursor1 = db.getGeofenceTags(addressString, locationNameString);
        int size = cursor1.getCount();

        Log.d(TAG, "in bind, count : " + size);

        if (0 != size) {
          cursor1.moveToFirst();
          Log.d(TAG, "cursor is not empty");
          tag = cursor1.getString(0);//cursor1.getColumnIndex(LocationsDatabase.GEOFENCE_TAG));
          Log.d(TAG, "Geofence tag : " + tag);
          //check if it is current geofence id
          if (tag.equals(mManagerSharedPref.getCurrentGeofenceId())) {
            //set the profile saved Profile
            Log.d(TAG, "Set saved profile and delete");
            new UpdateProfile(mManagerSharedPref.getSavedRingerMode(),
                (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE));
            mManagerSharedPref.saveCurrentGeofenceId(null);
          }
        } else {
          Log.e(TAG, "nothing returned");
        }

        int row = db.delete(tag);
        Log.d(TAG, "deleted : " + row);
        notifyDataSetChanged();

        mAdapterCallback.onDeleteComplete(tag);
        cursor1.close();
        db.close();
      }
    });
  }

  public interface AdapterCallback {
    public void onDeleteComplete(String tag);
  }
}

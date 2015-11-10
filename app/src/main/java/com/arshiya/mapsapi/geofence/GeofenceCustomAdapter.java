package com.arshiya.mapsapi.geofence;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.storage.contentprovider.LocationsContentProvider;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;

import org.w3c.dom.Text;

/**
 * Created by akhanumx on 11/2/2015.
 */
public class GeofenceCustomAdapter extends CursorAdapter {

    private static final String TAG = GeofenceCustomAdapter.class.getSimpleName();
    private AdapterCallback mAdapterCallback;
    private Context mContext;

    public GeofenceCustomAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
            mContext = context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.geofence_list_item, parent, false);
        return retView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView address = (TextView) view.findViewById(R.id.geofences_address);
        final TextView locationName = (TextView) view.findViewById(R.id.location_name);

        locationName.setText(cursor.getString(cursor.getColumnIndex(LocationsDatabase.LOCATION_NAME)));
        address.setText(cursor.getString(cursor.getColumnIndex(LocationsDatabase.ADDRESS)));

        Button delete = (Button) view.findViewById(R.id.remove_geofence_list_item);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the tag
                String addressString = address.getText().toString();
                String locationNameString  = locationName.getText().toString();

                String tag = "";
                LocationsDatabase db = new LocationsDatabase(mContext);
                Cursor cursor1 = db.getGeofenceTags(addressString, locationNameString);
                int size = cursor1.getCount();

                Log.d(TAG, "in bind, count : " + size);

                if (cursor1.moveToFirst()){
                    Log.d(TAG, "cursor is not empty");
                    tag = cursor1.getString(0);//cursor1.getColumnIndex(LocationsDatabase.GEOFENCE_TAG));
                    Log.d(TAG, "Geofence tag : " + tag);
                }else {
                    Log.e(TAG, "nothing returned");
                }




//                String selection = LocationsDatabase.GEOFENCE_TAG + " = ?";
//                int row = mContext.getContentResolver().delete(LocationsContentProvider.CONTENT_URI, selection,
//                        new String[]{address.getText().toString()});

                int row = db.delete(tag);
                Log.d(TAG, "deleted : " + row);

                mAdapterCallback.onDeleteComplete(tag);
                db.close();

            }
        });

    }

    public interface AdapterCallback {
        public void onDeleteComplete(String tag);
    }

}

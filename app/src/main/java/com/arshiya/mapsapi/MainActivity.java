package com.arshiya.mapsapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.geocodermanager.GeoCoderFetchAddress;
import com.arshiya.mapsapi.geofence.ui.RemoveGeofences;
import com.arshiya.mapsapi.settings.Settings;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<SliderListItem> mSliderListItems;
    private CustomSliderListAdapter mDListAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private GoogleMap mMap;
    private Button mAddLocation;
    private Button mRemoveLocation;
    private ImageButton mNavigationButton;
    private TextView mCurrentLocation;
    private Location mLocation;
    private ProfileManagerSharedPref mProfileManagerSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initLocationObserverService();
        initView();
        mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
        checkPlayServices();
        checkNetworkAvailability();
        checkGPSStatus();

        buildGoogleApiClient();

        setUpMapIfNeeded();


    }

    private void initLocationObserverService() {
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initView() {
        mCurrentLocation = (TextView) findViewById(R.id.home_current_location);

        mNavigationButton = (ImageButton) findViewById(R.id.navigator_button);
        mNavigationButton.setOnClickListener(this);

        mAddLocation = (Button) findViewById(R.id.home_add_location);
        mAddLocation.setOnClickListener(this);

        mRemoveLocation = (Button) findViewById(R.id.home_remove_location);
        mRemoveLocation.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        mSliderListItems = new ArrayList<SliderListItem>();
        String[] itemNameList = getResources().getStringArray(R.array.drawer_items);
//        int[] itemIconList = getResources().getIntArray(R.array.drawer_list_item_icons);

//        int size = itemIconList.length;

        mSliderListItems.add(new SliderListItem(itemNameList[0], R.drawable.home));
        mSliderListItems.add(new SliderListItem(itemNameList[1], R.drawable.settings));
        mSliderListItems.add(new SliderListItem(itemNameList[2], R.drawable.about));


        mDListAdapter = new CustomSliderListAdapter(this, mSliderListItems);

        mDrawerList.setAdapter(mDListAdapter);
        mDrawerList.setSelector(android.R.color.holo_blue_dark);

        mDrawerList.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        } else {

        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.home_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                mMap.clear();
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                Location location = mMap.getMyLocation();
                setUpMap(location);
            } else {
                Log.e(TAG, "setupmap : map is null");
            }

        }
    }

    private void setUpMap(Location location) {
        mLocation = location;
        LatLng latLng;
        if (null != location) {
            GeoCoderFetchAddress.getInstance().init(this, new LatLng(location.getLatitude(), location.getLongitude()),
                    new Messenger(new ResultHandler()));

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            Toast.makeText(this, "Could not fetch your location", Toast.LENGTH_SHORT).show();
            latLng = new LatLng(0, 0);
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.navigator_button:
                Log.d(TAG, "navigator clicked");
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.home_add_location:
                //launch maps activity
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.home_remove_location:
                Intent removeIntent = new Intent(this, RemoveGeofences.class);
                startActivity(removeIntent);
                break;

        }

    }



    private void checkGPSStatus() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
            Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsIntent);
        } else {
            Log.d(TAG, "GPS is enabled");
        }
    }

    private void checkNetworkAvailability() {
        final ConnectivityManager connMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        boolean is_connected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());

        if (!is_connected) {
            Log.d(TAG, " : connected to network : " + is_connected);
            Toast.makeText(this, " please check your internet connection ", Toast.LENGTH_SHORT).show();
            //TODO show error alert
        } else

        {
            Log.d(TAG, " : connected to network : " + is_connected);
        }

    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                int ACTION_PLAY_SERVICES_DIALOG = 1;
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "google api client : connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (null == location) {
            Log.e(TAG, "location is null");
        } else {
            setUpMap(location);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class ResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String address = msg.obj.toString();
            if (Constants.FETCH_FAILED == msg.arg2) {
                Log.e(TAG, " Fetch failed...");
                String savedAddress = mProfileManagerSharedPref.getSavedAddress(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                if (savedAddress.equalsIgnoreCase("")) {
                    Log.d(TAG, "Not a saved address");
                } else {
                    address = savedAddress;
                }
            } else {
                mProfileManagerSharedPref.saveAddress(address, new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            }

            mCurrentLocation.setText(address);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawers();
                break;

            case 1:
                Intent settingsIntent = new Intent(this, Settings.class);
                startActivity(settingsIntent);
                break;

            case 2:
                Toast.makeText(this, "in progress", Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.e(TAG, "invalid drawer list item");
        }

    }
}
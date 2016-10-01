package com.arshiya.mapsapi.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.arshiya.mapsapi.R;
import com.arshiya.mapsapi.common.ConfirmationDialog;
import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.errordisplay.AlertDialogClass;
import com.arshiya.mapsapi.geocodermanager.GeoCoderFetchAddress;
import com.arshiya.mapsapi.mapactivities.SliderListItem;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;
import com.arshiya.mapsapi.ui.adapters.SliderListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.arshiya.mapsapi.common.Constants.ACTION_OK;
import static com.arshiya.mapsapi.common.Constants.ACTON_CANCEL;
import static com.arshiya.mapsapi.common.Constants.FETCH_FAILED;

public class MainActivity extends Activity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener,
        ConfirmationDialog.OnClickCallback, OnMapReadyCallback {

  private static final String TAG = MainActivity.class.getSimpleName();
  public static DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ArrayList<SliderListItem> mSliderListItems;
  private SliderListAdapter mDListAdapter;
  private GoogleApiClient mGoogleApiClient;
  private LocationManager mLocationManager;
  private GoogleMap mMap;
  private Button mAddLocation;
  private Button mRemoveLocation;
  private ImageButton mNavigationButton;
  private TextView mCurrentLocation;
  private Location mLocation;
  private AlertDialog mDialog;
  private ProfileManagerSharedPref mProfileManagerSharedPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home_page);

    initView();
    checkPlayServices();
    checkNetworkAvailability();
    checkGPSStatus();

    buildGoogleApiClient();

    setUpMapIfNeeded();
  }

  private void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
  }

  private void initView() {
    mProfileManagerSharedPref = ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
    mCurrentLocation = (TextView) findViewById(R.id.home_current_location);
    mCurrentLocation.setTypeface(Fonts.ROBOTOREGULAR);

    TextView title = (TextView) findViewById(R.id.app_name);
    title.setTypeface(Fonts.ROBOTOMEDIUM);

    TextView youAreAt = (TextView) findViewById(R.id.location_header);
    youAreAt.setTypeface(Fonts.ROBOTOMEDIUM);

    mNavigationButton = (ImageButton) findViewById(R.id.navigator_button);
    mNavigationButton.setOnClickListener(this);

    mAddLocation = (Button) findViewById(R.id.home_add_location);
    mAddLocation.setOnClickListener(this);
    mAddLocation.setTypeface(Fonts.ROBOTOMEDIUM);

    mRemoveLocation = (Button) findViewById(R.id.home_remove_location);
    mRemoveLocation.setOnClickListener(this);
    mRemoveLocation.setTypeface(Fonts.ROBOTOMEDIUM);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.slider_list);

    mSliderListItems = new ArrayList<SliderListItem>();
    String[] itemNameList = getResources().getStringArray(R.array.drawer_items);

    mSliderListItems.add(new SliderListItem(itemNameList[0], R.drawable.home));
    mSliderListItems.add(new SliderListItem(itemNameList[1], R.drawable.settings));
    mSliderListItems.add(new SliderListItem(itemNameList[2], R.drawable.about));

    mDListAdapter = new SliderListAdapter(this, mSliderListItems);

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
      ((MapFragment) getFragmentManager().findFragmentById(R.id.home_map)).getMapAsync(this);
      // Check if we were successful in obtaining the map.
    }
  }

  private void setUpMap(Location location) {
    mLocation = location;
    LatLng latLng;
    if (null != location) {
      GeoCoderFetchAddress.getInstance()
              .init(this, new LatLng(location.getLatitude(), location.getLongitude()),
                      new Messenger(new ResultHandler()));

      latLng = new LatLng(location.getLatitude(), location.getLongitude());
    } else {
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
        Intent removeIntent = new Intent(this, ProfilesListActivity.class);
        startActivity(removeIntent);
        break;
    }
  }

  private void checkGPSStatus() {
    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    if (!gpsEnabled) {
      mDialog = ConfirmationDialog.getDialog(MainActivity.this, "Turn on GPS",
              "Please turn on GPS to get your location.", "");
      mDialog.show();
    } else {
      Log.d(TAG, "GPS is enabled");
    }
  }

  private void checkNetworkAvailability() {
    final ConnectivityManager connMgr =
            (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
    boolean is_connected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());

    if (!is_connected) {
      Log.d(TAG, " : connected to network : " + is_connected);
      AlertDialogClass.showAlert(this, "please check your internet connection ");
    } else

    {
      Log.d(TAG, " : connected to network : " + is_connected);
    }
  }

  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        int ACTION_PLAY_SERVICES_DIALOG = 1;
        GooglePlayServicesUtil.getErrorDialog(resultCode, this, ACTION_PLAY_SERVICES_DIALOG).show();
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

  @Override
  public void onClickCD(int state) {
    switch (state) {
      case ACTION_OK:
        if (null != mDialog && mDialog.isShowing()) {
          Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(gpsIntent);
          mDialog.dismiss();
        }
        break;

      case ACTON_CANCEL:
        if (null != mDialog && mDialog.isShowing()) {
          mDialog.dismiss();
        }
        break;
    }
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    if (mMap != null) {
      mMap.setMyLocationEnabled(true);
      mMap.getUiSettings().setMyLocationButtonEnabled(false);
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

  private class ResultHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      String address = msg.obj.toString();
      if (FETCH_FAILED == msg.arg2) {
        Log.e(TAG, " Fetch failed...");
        String savedAddress = mProfileManagerSharedPref.getSavedAddress(
                new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        if (savedAddress.equalsIgnoreCase("")) {
          Log.d(TAG, "Not a saved address");
        } else {
          address = savedAddress;
        }
      } else {
        mProfileManagerSharedPref.saveAddress(address,
                new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
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
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        break;

      case 2:
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
        break;

      default:
        Log.e(TAG, "invalid drawer list item");
    }
  }
}
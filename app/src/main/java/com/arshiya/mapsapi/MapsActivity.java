package com.arshiya.mapsapi;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arshiya.mapsapi.common.Fonts;
import com.arshiya.mapsapi.errordisplay.AlertDialogClass;
import com.arshiya.mapsapi.geofence.GeofenceCallbacks;
import com.arshiya.mapsapi.geofence.MapUtils;
import com.arshiya.mapsapi.storage.ConfirmationDialog;
import com.arshiya.mapsapi.storage.contentprovider.InsertTaskManager;
import com.arshiya.mapsapi.geofence.MapDataProvider;
import com.arshiya.mapsapi.common.Constants;
import com.arshiya.mapsapi.geocodermanager.GeoCoderFetchAddress;
import com.arshiya.mapsapi.geocodermanager.PlaceSearchHandler;
import com.arshiya.mapsapi.geofence.GeofenceErrorMessage;
import com.arshiya.mapsapi.geofence.GeofenceTransitionIntentService;
import com.arshiya.mapsapi.storage.contentprovider.LocationsDatabase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapLoadedCallback, ConfirmationDialog.OnClickCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 100;
    private static final int PLACE_TYPE_OPTION_DIALOG = 4;
    private static final int PROFILE_TYPE_OPTION_DIALOG = 5;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mLocation;
    private LocationManager mLocationManager;
    private Button mFind;
    private AutoCompleteTextView mUserInput;
    private LatLng mLatLng;
    private MarkerOptions mMarkerOptions;
    private PlacesAutocomplete mPlacesAutocomplete;
    private ArrayList<LatLng> mArrayLatLngs;
    private AlertDialog mDialog;
    private AlertDialog mNewLocationDialog;
    private InsertTaskManager mInsertTaskManager;
    private int mLocationType;
    private LatLng mSelectedLatLng;
    private ProgressBar mSearchProgress;

    //geofencing
    private PendingIntent mGeofencePendingIntent;
    private String mSelectedAddress;
    private ResultCallback<Status> mGeofenceResultCallback;
    private ArrayList<CircleOptions> mCircleOptionsArrayList;
    private ArrayList<MarkerOptions> mMarkerOptionsArrayList;
    private Marker mSelectedMarker;
    private Circle mSelectedCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();
        checkPlayServices();
        checkGPSStatus();
        checkNetworkAvailability();
        buildGoogleApiClient();

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void init() {
        geofenceResultCallback();
        mSearchProgress = (ProgressBar) findViewById(R.id.maps_progressBar);

        mInsertTaskManager = InsertTaskManager.getInsertTaskManagerInstance(this, new Messenger(new ResultHandler()));
        mFind = (Button) findViewById(R.id.btn_find);
        mUserInput = (AutoCompleteTextView) findViewById(R.id.et_location);
        mUserInput.setTypeface(Fonts.ROBOTOREGULAR);

        mFind.setOnClickListener(this);


        mArrayLatLngs = new ArrayList<LatLng>();

        mPlacesAutocomplete = PlacesAutocomplete.getInstance();

        mUserInput.setThreshold(3);
        mUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, " Text changed : " + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    //ignore
                } else {
                    mPlacesAutocomplete.startTask(s.toString(), MapsActivity.this, new Messenger(new ResultHandler()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void loadGeofences() {
        MapDataProvider mapDataProvider = new MapDataProvider(this);
        mCircleOptionsArrayList = new ArrayList<CircleOptions>();
        mMarkerOptionsArrayList = new ArrayList<MarkerOptions>();

        mCircleOptionsArrayList = mapDataProvider.getCircleOptions();
        mMarkerOptionsArrayList = mapDataProvider.getMarkerOptions();

        if (null != mCircleOptionsArrayList && null != mMarkerOptionsArrayList) {
            int count = mCircleOptionsArrayList.size();

            for (int i = 0; i < count; i++) {
                mMap.addCircle(mCircleOptionsArrayList.get(i));
                mMap.addMarker(mMarkerOptionsArrayList.get(i));
            }
        }
    }

    private void checkGPSStatus() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            mDialog = ConfirmationDialog.getDialog(MapsActivity.this, "Turn on GPS", "Please turn on GPS to get your location.", "");
            mDialog.show();
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
            AlertDialogClass.showAlert(this, "please check your internet connection ");
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
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        }
        loadGeofences();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that ist would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setOnMapClickListener(this);
                mMap.setOnMapLongClickListener(this);
                mMap.setOnMapLoadedCallback(this);
                mMap.setOnMarkerClickListener(this);
                mLocation = mMap.getMyLocation();
                setUpMap();
            } else {
                Log.e(TAG, "setupmap : map is null");
            }

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        if (null != mLocation) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                    .title("Marker"));
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "google api client : connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (null == location) {
            Log.e(TAG, "location is null");
        } else {
            setNewLocation(location);
        }
    }

    private void setNewLocation(Location location) {
        Log.d(TAG, "setNewLocation() :" + location);
        if (null == mMap) {
            Log.e(TAG, " map is still null");
            return;
        }
        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate currentLocation = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        mMap.moveCamera(currentLocation);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_find:
                //hide soft input keyboard
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                mSearchProgress.setVisibility(View.VISIBLE);
                if (null != mPlacesAutocomplete) {
                    mPlacesAutocomplete.stopTask();
                }

                startLocationSearch();
                break;

            case R.id.ca_ok:
                //show profile options
                Intent intent = new Intent(MapsActivity.this, LocationDetailsGetter.class);
                startActivityForResult(intent, PROFILE_TYPE_OPTION_DIALOG);
                mDialog.dismiss();
                break;

            case R.id.ca_cancel:
                mDialog.dismiss();
                break;
        }
    }

    private void startLocationSearch() {
        String userInput = mUserInput.getText().toString();

        Log.d(TAG, "user input : " + userInput);

        if (!userInput.equalsIgnoreCase("")) {
            //set up the data
            PlaceSearchHandler.getInstance().search(this, userInput, new Messenger(new ResultHandler()));

        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

        Log.d("onMapClick", " latlng : " + latLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        markerOptions.position(latLng);

        mLocationType = Constants.LOCATION_SINGLE;
        mSelectedLatLng = latLng;
        mSelectedMarker = mMap.addMarker(markerOptions);
        Log.d(TAG, "Marker added : " + mSelectedMarker);

        // get address from latlng
        GeoCoderFetchAddress.getInstance().init(this, latLng, new Messenger(new ResultHandler()));
       mSelectedCircle = mMap.addCircle(MapUtils.getCircle(MapsActivity.this, mSelectedLatLng));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewLocationDialog = ConfirmationDialog.getDialog(MapsActivity.this, "New Location", "Add location: \n" + mSelectedAddress, "");
                mNewLocationDialog.show();
            }
        }, 500);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, " LatLng :" + marker.getPosition());
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "map is loaded");
        loadGeofences();
    }

    private void geofenceResultCallback() {
        mGeofenceResultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "Geofence creation : success");
                } else {
                    Log.d(TAG, "Geofence creation : failed");

                    String errorMsg = GeofenceErrorMessage.getErrorString(MapsActivity.this, status.getStatusCode());
                    Log.d(TAG, "Geofence error : " + errorMsg);
                }

            }
        };
    }

    @Override
    public void onClickCD(int state) {
        switch (state) {
            case Constants.ACTION_OK:
                if (null != mDialog && mDialog.isShowing()) {
                    Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsIntent);
                    mDialog.dismiss();
                }
                if (null != mNewLocationDialog && mNewLocationDialog.isShowing()) {
                    Intent intent = new Intent(MapsActivity.this, LocationDetailsGetter.class);
                    startActivityForResult(intent, PROFILE_TYPE_OPTION_DIALOG);
                    loadGeofences();
                    mNewLocationDialog.dismiss();
                }
                break;

            case Constants.ACTON_CANCEL:
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != mNewLocationDialog && mNewLocationDialog.isShowing()) {
                    mSelectedMarker.remove();
                    mSelectedCircle.remove();
                    mNewLocationDialog.dismiss();
                }
                break;
        }
    }

//
//    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//
//            // params comes from the execute() call: params[0] is the url.
//            Log.d("testDownloadUrl", "url : " + urls[0]);
//            String data = "";
//            data = DownloadUrl.downloadUrl(urls[0]);
//
//            Log.d("DownloadWebpageTask", "  data : " + data);
//            return data;
//        }
//
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//        }
//    }


    /**
     * Message  handler to receive results from background task
     */
    private class ResultHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            int type = message.arg1;

            switch (type) {
                case Constants.ACTION_SEARCH:
                    ArrayList<Address> addresses = (ArrayList<Address>) message.obj;
                    showSearchResult(addresses);
                    break;

                case Constants.ACTION_FETCH_ADDRESS:
                    mSelectedAddress = (String) message.obj;
                    break;

                case Constants.ACTION_AUTOCOMPLETE:
                    break;

                case Constants.ACTION_AUTOCOMPLETE_RESULT:
                    mUserInput.setAdapter((SimpleAdapter) message.obj);
                    break;
            }

            Log.d(TAG, "ResultHandler , arg1: " + type);
        }

    }

    private void showSearchResult(ArrayList<Address> addresses) {
        int total = addresses.size();
        mSearchProgress.setVisibility(View.GONE);
        if (0 == addresses.size()) {
            Toast.makeText(this, "No location found", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "got addresses");

            Log.d("GeoCoderTask", "size : " + total);
            for (int i = 0; i < total; i += 1) {
                Address address = (Address) addresses.get(i);

                Log.d("GeoCoderTask", "address : " + address);
                mLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                mMarkerOptions = new MarkerOptions();
                mMarkerOptions.position(mLatLng);
                mMarkerOptions.title(addressText);
                mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap.addMarker(mMarkerOptions);
                // Locate the first location
                if (i == 0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLng));


            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int status;
        int selection;

        switch (requestCode) {
            case PLACE_TYPE_OPTION_DIALOG:
                status = data.getIntExtra("STATUS", -1);
                selection = data.getIntExtra("SELECTION", -1);

                handlePlaceTypeOptionResult(status, selection);
                break;

            case PROFILE_TYPE_OPTION_DIALOG:
                status = data.getIntExtra("STATUS", -1);

                handleProfileTypeOptionResult(status, data);
                break;
        }
    }

    private void handleProfileTypeOptionResult(int status, Intent data) {

        switch (status) {
            case 0:
                //cancel, clear the markers
                Log.d(TAG, "handleProfileTypeOptionResult : clear");
                clearMarkers();
                break;

            case 1:
                //ok
                loadGeofences();
                prepareData(data);
                createGeofence();
                break;

            default:
                Log.d(TAG, "invalid status received");

        }
    }

    private void createGeofence() {
        GeofenceCallbacks geofenceCallbacks = new GeofenceCallbacks();
        mGeofencePendingIntent = geofenceCallbacks.getGeofencePendingIntent(MapsActivity.this, mGeofencePendingIntent);
    }

//    private void createGeofence() {
//        //create a geofence for the selected latlng
//        if (null == mGoogleApiClient || !mGoogleApiClient.isConnected()) {
//            //todo error dialog
//            Toast.makeText(this, "google api client is not connected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    getGeofencingRequest(),
//                    getGeofencePendingIntent()
//            ).setResultCallback(mGeofenceResultCallback);
//        } catch (SecurityException securityexception) {
//            securityexception.printStackTrace();
//        }
//    }


    private void handlePlaceTypeOptionResult(int status, int selection) {

        switch (status) {
            case 0:
                //cancel, clear the markers
                Log.e(TAG, "handlePlaceTypeOptionResult : clear");
                clearMarkers();
                break;

            case 1:
                //ok
                switch (selection) {
                    case 0:
                        //area
                        getProfileDetails();
                        break;

                    default:
                        Log.d(TAG, "invalid selection received");
                }
                break;

            default:
                Log.d(TAG, "invalid status received");

        }
    }


    private void prepareData(Intent data) {
        JSONObject latlng = getLatLngObject();
        String locationName = data.getStringExtra("LOCATION_NAME");
        int profileType = data.getIntExtra("PROFILE", Constants.PROFILE_NORMAL);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        markerOptions.position(mLatLng);

        Log.d(TAG, " laction name : " + locationName);
        //todo add geofence
        writeToTable(latlng, locationName, profileType);

    }


    private JSONObject getLatLngObject() {
        JSONObject latLngJsonObject = new JSONObject();

        try {
            latLngJsonObject.put("lat", mSelectedLatLng.latitude);
            latLngJsonObject.put("lng", mSelectedLatLng.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLngJsonObject;

    }

    private void getProfileDetails() {
        Intent intent = new Intent(MapsActivity.this, LocationDetailsGetter.class);
        startActivityForResult(intent, PROFILE_TYPE_OPTION_DIALOG);
    }

    private void clearMarkers() {
        mSelectedMarker.remove();
    }


    private void writeToTable(JSONObject latlng, String locationName, int profileType) {
        ContentValues values = new ContentValues();

        values.put(LocationsDatabase.LATLNGS, latlng.toString());
        values.put(LocationsDatabase.LOCATION_TYPE, Constants.LOCATION_SINGLE);
        values.put(LocationsDatabase.ADDRESS, mSelectedAddress);
        values.put(LocationsDatabase.GEOFENCE_TAG, mSelectedLatLng.toString());
        values.put(LocationsDatabase.PROFILE_TYPE, profileType);
        values.put(LocationsDatabase.LOCATION_NAME, locationName);

        mInsertTaskManager.insert(values);

        mArrayLatLngs.clear();
    }

}
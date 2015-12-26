package com.arshiya.mapsapi.locationdatamanager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by akhanumx on 10/26/2015.
 */
public class PlacesResponseParser {

    private static final String TAG = "PlacesResponseParser";

    public List<HashMap<String, String>> parsePlace(JSONObject jsonObject){
        JSONArray placesArray = null;
        try {
            placesArray = jsonObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  getPredictedPlaces(placesArray);
    }

    private List<HashMap<String, String>> getPredictedPlaces(JSONArray placesArray) {
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> place = null;
        int count = placesArray.length();

        for (int i = 0; i < count; i++){
            try {
                place = getPlace((JSONObject) placesArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            placesList.add(place);
        }

        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject placeObject){
        HashMap<String, String> place = new HashMap<String, String>();

        Log.d(TAG, "JSONObject : " + placeObject);
        String place_id = "";
        String description = "";
        String reference = "";
        String types = "";

        try {
            description = placeObject.getString("description");
            place_id = placeObject.getString("place_id");
            reference = placeObject.getString("reference");
            types = placeObject.getString("types");

            place.put("description", description);
            place.put("id", place_id);
            place.put("reference", reference);
            place.put("types", types);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  place;
    }

}


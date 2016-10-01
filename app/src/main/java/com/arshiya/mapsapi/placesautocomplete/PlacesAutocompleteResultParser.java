package com.arshiya.mapsapi.placesautocomplete;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by arshiya on 9/30/2016.
 */
public class PlacesAutocompleteResultParser {

  private static final String TAG = PlacesAutocompleteResultParser.class.getSimpleName();

  public List<HashMap<String, String>> parse(String response) {
    JSONObject jsonResponseObj;
    JSONArray placesArray = null;

    try {
      jsonResponseObj = new JSONObject(response);
      placesArray = jsonResponseObj.getJSONArray("predictions");

    } catch (Exception e) {
      Log.d("Exception", e.toString());
    }
    return parsePredictedPlaces(placesArray);
  }

  private List<HashMap<String, String>> parsePredictedPlaces(JSONArray placesArray) {
    List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> place = null;
    int count = placesArray.length();

    for (int i = 0; i < count; i++) {
      try {
        place = getPlace((JSONObject) placesArray.get(i));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      placesList.add(place);
    }

    return placesList;
  }

  private HashMap<String, String> getPlace(JSONObject placeObject) {
    HashMap<String, String> place = new HashMap<>();

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
    return place;
  }
}
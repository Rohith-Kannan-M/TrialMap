package com.example.trialmap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String, String> getDuration(JSONArray darray) {
        Log.d("response",darray.toString());
        HashMap<String, String> googleDirectionsmap = new HashMap<>();
        String duration = "";
        String distance = "";
        try {
            duration = darray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = darray.getJSONObject(0).getJSONObject("distance").getString("text");
            googleDirectionsmap.put("duration",duration);
            googleDirectionsmap.put("distance",distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return googleDirectionsmap;
    }

    private HashMap<String, String> getPlace(JSONObject place)
    {
        HashMap<String,String> gplace = new HashMap<>();
        String placeName = "--NA--";
        String vicinity = "--NA--";
        String lat = "";
        String lang = "";
        String ref = "";
        try {
             if(!place.isNull("name")) {
                 placeName = place.getString("name");
             }
             if(!place.isNull("vicinity"))
             {
                 vicinity = place.getString("vicinity");
             }
             lat = place.getJSONObject("geometry").getJSONObject("location").getString("lat");
             lang = place.getJSONObject("geometry").getJSONObject("location").getString("lng");

             ref = place.getString("reference");

             gplace.put("place_name", placeName);
             gplace.put("vicinity",vicinity);
             gplace.put("lat",lat);
             gplace.put("lng",lang);
             gplace.put("reference",ref);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gplace;
    }

    private List<HashMap<String,String>> getplaces(JSONArray places)
    {
        int count = places.length();
        List<HashMap<String,String>> placelist = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for(int i=0 ; i < count; i++)
        {
            try {
                placeMap = getPlace((JSONObject) places.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    public List<HashMap<String,String>> parse(String data)
    {
        JSONArray array = null;
        JSONObject obj;
        try {
            obj = new JSONObject(data);
            array = obj.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getplaces(array);
    }

    public HashMap<String, String> parseDirections(String s) {
        JSONArray array = null;
        JSONObject obj;

        try {
            obj = new JSONObject(s);
            array = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
            Log.d("arrray","--"+array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getDuration(array);
    }


}

package com.example.trialmap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    private String googleDirectionData;
    private GoogleMap mMap;
    String url;
    String duration,distance;
    LatLng search_place;

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> directionsList=null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        duration = directionsList.get("Duration");
        distance = directionsList.get("Distance");

        final MarkerOptions markopr = new MarkerOptions();
        markopr.position(search_place);
        markopr.draggable(true);
        markopr.title("Distance : "+distance);
        markopr.title("\nDuration : "+duration);
        markopr.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markopr);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(search_place));




    }

    private void showdirectionsPlaces(List<HashMap<String, String>> nearbyPlaceList) {

    }

    @Override
    protected String doInBackground(Object[] objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        search_place = (LatLng)objects[2];

        Log.d("gd url","---"+url);
        Log.d("gd place ","---"+search_place);

        DownloadURL downurl = new DownloadURL();
        try {

            googleDirectionData = downurl.readUrl(url);

        }catch (IOException e)
        {
            e.printStackTrace();
        }


        return googleDirectionData;
    }
}

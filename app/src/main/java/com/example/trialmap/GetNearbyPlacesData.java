package com.example.trialmap;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String parsedDistance;
    String response;
    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    double latt,lang;
    //double origin;
    LatLng origin;
    String sep;
    LatLng latLng;




    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        origin = (LatLng)objects[2];
        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
            //Log.d("gogo",googlePlacesData);
            //sep = googlePlacesData.substring(googlePlacesData.length()-1,googlePlacesData.length()-22);
            //Log.d("gogo", ""+origin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method "+s);
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            //Log.d("dooo",""+googlePlace.size());
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            latLng = new LatLng( lat, lng);
            //Log.d("nearlatlng","lat ="+"lang ="+lng);

            origin = new LatLng(latt,lang);
            //getDistance(latt,lang,lat,lng);
            double distance = SphericalUtil.computeDistanceBetween(origin, latLng);
            //CalculationByDistance(origin,latLng);
            //double distance = origin.distanceTo(latLng);
            //double distance = CalculationByDistance(origin,latLng);
            //Log.d("dista","--  "+distance);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " :===== "+distance);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }








 /*   public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving&key=AIzaSyCGktY13nNN9RRzvGIziVd3g8XfHxMG2Wg");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("text");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }
*/
 public double CalculationByDistance(LatLng StartP, LatLng EndP) {
     int Radius=6371;//radius of earth in Km
     double lat1 = StartP.latitude;
     double lat2 = EndP.latitude;
     double lon1 = StartP.longitude;
     double lon2 = EndP.longitude;
     double dLat = Math.toRadians(lat2-lat1);
     double dLon = Math.toRadians(lon2-lon1);
     double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
             Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                     Math.sin(dLon/2) * Math.sin(dLon/2);
     double c = 2 * Math.asin(Math.sqrt(a));
     double valueResult= Radius*c;
     double km=valueResult/1;
     DecimalFormat newFormat = new DecimalFormat("####");
     int kmInDec =  Integer.valueOf(newFormat.format(km));
     double meter=valueResult%1000;
     int  meterInDec= Integer.valueOf(newFormat.format(meter));
     //Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);
     int k = Integer.valueOf(String.valueOf(kmInDec) + String.valueOf(meterInDec));
     Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec+"---"+k);
     return k;
 }
}

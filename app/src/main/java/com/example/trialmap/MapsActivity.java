package com.example.trialmap;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationrequest;
    private Location loc;
    private Marker mark;
    public static final int code = 99;
    Context context;
    public static double lat,lang;
    EditText search_place;
    String str_search_place;
    List<Address> add = null;
    Button search;
    LatLng latLng,latLng2;
    ImageButton bunk;
    int PROXIMITY_RADIUS=100000;
    double latitude,longitude;
    public static double dest1,dest2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        search_place = (EditText)findViewById(R.id.search_place);
        search = (Button)findViewById(R.id.search);
        bunk = (ImageButton)findViewById(R.id.bunk);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS must requirred:")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View locationButton =((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        rlp.setMargins(0,0,30,30);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(code)
        {
            case code:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleAPiClient();
                        }
                    mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission not Granted", Toast.LENGTH_LONG);
                }
        }
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleAPiClient();
            mMap.setMyLocationEnabled(true);
        }


    }


    protected synchronized void buildGoogleAPiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Currenct location Area
        lat = location.getLatitude();
        lang = location.getLongitude();

        loc = location;

        if (mark != null) {
            mark.remove();
        }
        Log.d("lat and lang ", "" + lat + lang);
        latLng = new LatLng(lat, lang);
        /*Intent  i = new Intent(MapsActivity.this,GetNearbyPlacesData.class);
        i.putExtra("ltt", lat);
        i.putExtra("lng", lang);
        i.putExtras(i);
        startActivity(i);*/

        Log.d("ronie",""+lat+""+lang);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }


Toast.makeText(MapsActivity.this,"Lat = "+lat+"--"+"Lang = "+lang,Toast.LENGTH_LONG).show();


    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"); //61
        googlePlaceUrl.append("location="+latitude+","+longitude); //9
        //googlePlaceUrl.append("&rankBy=distance&language=en");

        //googlePlaceUrl.append("location=11.0547379,78.0492949");

        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyCM7J_Pc0AfIUJiQyqOB4ZBG7jZJsbSN0I");

        //Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationrequest = new LocationRequest();
        locationrequest.setInterval(1000);
        locationrequest.setFastestInterval(1000);
        locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(client,locationrequest,this);
            }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
            } else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
            }
            return  false;
        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
            {
                final Geocoder geo = new Geocoder(this);

                str_search_place = search_place.getText().toString();

                try {
                    add = geo.getFromLocationName(str_search_place, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < add.size(); i++) {
                    Address address = add.get(i);
                    final MarkerOptions markopr = new MarkerOptions();
                    dest1 = address.getLatitude();
                    dest2 = address.getLongitude();
                    latLng2 = new LatLng(dest1, dest2);
                    markopr.position(latLng2);
                    markopr.draggable(true);
                    markopr.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        }
                    });

                    mMap.addMarker(markopr);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));

                }
            }
            break;
            case R.id.bunk:
            {
                Object dataTransfer[] = new Object[3];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

                mMap.clear();
                String bunk = "gas_station";
                String url = getUrl(lat, lang, bunk);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2]=latLng;
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Places ", Toast.LENGTH_SHORT).show();



            }
            break;
            case R.id.to:
            {
                Object dataTransfer[] = new Object[3];
                String urll = getdirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = urll;
                dataTransfer[2] = latLng2;
                Log.d("url","---"+urll);
                Log.d("latLng of dest ","---"+latLng2);
                getDirectionsData.execute(dataTransfer);
            }
        }
    }
//AIzaSyCGktY13nNN9RRzvGIziVd3g8XfHxMG2Wg ---- https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=YOUR_API_KEY
    private String getdirectionsUrl() {

        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+lat+","+lang);
        googleDirectionUrl.append("&destination="+dest1+","+dest2);
        googleDirectionUrl.append("&key="+"AIzaSyCM7J_Pc0AfIUJiQyqOB4ZBG7jZJsbSN0I");

        return googleDirectionUrl.toString();
    }
}

// Sample lat and long:
//11.0547379 78.0492974


// Adding marker and removing marker:
 /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                LatLng pointt = point;
                final MarkerOptions markopr = new MarkerOptions();
                markopr.position(pointt);
                markopr.title("Clicked Location");
                markopr.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mark = mMap.addMarker(markopr);
                mark.showInfoWindow();

            }
        });*/


        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                    marker.remove();

                return false;
            }
        });*/
package com.br.locationsearch.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.br.locationsearch.classes.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/*
* MapMarkerActivity is the activity responsible to show a map, get the content within an Intent, which is the clicked
* address data (address, latitude, longitude) on the ListView received with the method getStringExtra() and all the address
* items from the result ListView received with the method getSerializableExtra()
*
* */

public class MapMarkerActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        //Check if Google Play services is available on the device
        if(status != ConnectionResult.SUCCESS){
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        else{
            setUpMapIfNeeded();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        Intent intent = getIntent();
        //Get the values passed through the Intent
        String locationName = intent.getStringExtra(MainActivity.EXTRA_NAME);
        String lat = intent.getStringExtra(MainActivity.EXTRA_LAT);
        String lng = intent.getStringExtra(MainActivity.EXTRA_LNG);

        List<Location> locations;
        locations = (ArrayList<Location>)getIntent().getSerializableExtra(MainActivity.EXTRA_LIST);

        CameraPosition cameraPosition;
        CameraUpdate cameraUpdate = null;

        for(int i = 0; i < locations.size(); i++){
            if(locationName.equals(locations.get(i).getFormattedAddress()) && lat.equals(String.valueOf(locations.get(i).getLat()))
                                                    && lng.equals(String.valueOf(locations.get(i).getLng()))){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                        .anchor(0.5f, 0.5f)
                        .title("Location: " + locationName))
                        .setSnippet("Coordinates: " + lat + ", " + lng);
                //Gets the camera position from the given LatLng and a zoom is set
                cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).zoom(14.0f).build();
                /* CameraUpdate that references the update that must be done on the camera, passed as a parameter to
                   animateCamera function*/
                cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            }
            else{
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locations.get(i).getLat(), locations.get(i).getLng()))
                        .anchor(0.5f, 0.5f)
                        .title("Location: " + locations.get(i).getFormattedAddress()))
                        .setSnippet("Coordinates: " + locations.get(i).getLat() + ", " + locations.get(i).getLng());
            }
        }

        if(cameraUpdate != null){
            mMap.animateCamera(cameraUpdate);
        }
    }
}

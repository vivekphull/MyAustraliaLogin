package com.pavi_developing.myaustralialogin;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by VivekPhull on 9/29/2017.
 */

public class MappedReports extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener {

    GoogleMap mMap;
    private LocationManager locationManager;
    RequestQueue engine;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private boolean locationPermission;
    String url, TAG = "MapRep/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePermissions();
        initializeViews(getApplicationContext());
        initialCalls();

    }

    private void initialCalls() {
        moveToCurrentLocation();
        getData();
    }

    private void getData() {
        final Response.Listener<JSONArray> onSuccess = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG+"API", String.valueOf(response));
                try {
                    testFirstEntry(response.get(0));
                } catch (JSONException e) {
                    Log.e(TAG+"API/JSONExp", "Index out of bound");
                    e.printStackTrace();
                }
            }
        };
        final Response.ErrorListener onFailure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
                Log.e(TAG+"API", String.valueOf(response));
            }
        };

        engine.add(new JsonArrayRequest(Request.Method.GET, url, null, onSuccess, onFailure));
    }

    private void testFirstEntry(Object data) {
        Log.i(TAG+"test", data.toString());
        JSONObject obj = (JSONObject) data;
        try {
            Log.i(TAG+"test", "Geometry: "+obj.get("geometry"));
            obj = (JSONObject) obj.get("geometry");
            Log.i(TAG+"test", "Geometry/Coordinates: "+obj.get("coordinates"));
        } catch (JSONException e) {
            Log.i(TAG+"test/Exp", "Invalid Index");
            e.printStackTrace();
        }

    }

    private void moveToCurrentLocation() {

    }

    private void initializePermissions() {
        // ACCESS FINE LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);

    }

    private void initializeViews(Context context) {
        setContentView(R.layout.activity_mapped_reports);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeVariables();
    }

    private void initializeVariables() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationPermission = true;

        engine = Volley.newRequestQueue(this);
        url = "http://13.229.108.76:1000/api/status";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
    }

    private MarkerOptions getMarker(LatLng latLng, String title, int resource) {
        MarkerOptions mark =
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource( getResources(), resource ) ));
        return mark;
    }

    private void moveCamera(Location location) {
        LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, (android.location.LocationListener) this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.i(TAG+"onLocCh","Location: "+location);
        moveCamera(location);
    }
}
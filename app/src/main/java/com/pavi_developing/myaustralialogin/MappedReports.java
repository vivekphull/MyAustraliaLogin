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
    RequestQueue engine;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private boolean
            locationPermission,
            currentLocationZoomed;
    String url, TAG = "MapRep/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePermissions();
        initializeViews();
        initialCalls();

    }

    private void initialCalls() {
        Log.e(TAG+"initCalls", "Function Call");
        if(!allPermissionsAreGranted())
            return;

        getData();
    }

    private void getData() {
        Log.e(TAG+"getData", "Function Call");
        final Response.Listener<JSONArray> onSuccess = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG+"API", String.valueOf(response));
                mapReports(response);
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

    private void mapReports(JSONArray data) {
        Log.i(TAG+"test", ""+data.length());
        if(!(data.length()>0))
            return;
        try {
            JSONObject obj = (JSONObject) data.remove(0);
            mapReports(data);
            JSONArray arr = obj.getJSONObject("geometry").getJSONArray("coordinates");
            if(obj.getBoolean("active"))
                mMap.addMarker(getMarker(new LatLng(arr.getDouble(1), arr.getDouble(0)), obj.getString("description"), R.mipmap.ic_launcher));
        } catch (JSONException e) {
            Log.i(TAG+"test/Exp", "Invalid Index");
            e.printStackTrace();
        }

    }

    private void initializePermissions() {
        locationPermission = false;

        // ACCESS FINE LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        } else
            locationPermission = true;
    }

    private void initializeViews() {
        if(!allPermissionsAreGranted())
            return;

        setContentView(R.layout.activity_mapped_reports);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeVariables();
    }

    private boolean allPermissionsAreGranted() {
        boolean granted = true;
        granted = (locationPermission)?granted:locationPermission;

        Log.d(TAG+"AllPerGra", "\n\nLocation: "+locationPermission+"\nfinal bool: "+granted);
        return granted;
    }

    private void initializeVariables() {
        currentLocationZoomed =
                false;

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 10000));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (permissions[requestCode]) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                locationPermission = (grantResults[requestCode]==0);
                if(!locationPermission)
                    finish();
                break;
        }

        initializeViews();
        initialCalls();
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.i(TAG+"onLocCh","Location: "+location);
        if(!currentLocationZoomed) {
            currentLocationZoomed = true;
            moveCamera(location);
            mMap.setOnMyLocationChangeListener(null);
        }
    }
}
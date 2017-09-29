package com.pavi_developing.myaustralialogin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by VivekPhull on 9/29/2017.
 */

public class MappedReports extends FragmentActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews(getApplicationContext());
    }

    private void initializeViews(Context context) {
        setContentView(R.layout.activity_mapped_reports);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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

}
package com.example.dailymart;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dailymart.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

public class AboutUsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Button btnNormal, btnSatellite,btnTerrain,btnHybrid, btnNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnNormal=findViewById(R.id.btnNormal);
        btnSatellite=findViewById(R.id.btnSatellite);
        btnTerrain=findViewById(R.id.btnTerrain);
        btnHybrid=findViewById(R.id.btnHybrid);
        btnNull=findViewById(R.id.btnNull);


        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        btnTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        btnNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(21.097681080441408, 76.78763737698029);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Thakur Kirana Shop"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
        mMap.addCircle(new CircleOptions()
                .center(myLocation)
                .fillColor(Color.parseColor("#588C8C8C"))
                .strokeColor(Color.parseColor("#494949")).radius(20));

        LatLng myLocation1 = new LatLng(20.94360501766759, 77.7649271003036);
        mMap.addMarker(new MarkerOptions().position(myLocation1).title("Amravati"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation1));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation1,16));
        mMap.addPolyline(new PolylineOptions().add(myLocation,myLocation1).color(Color.RED)
                .width(5));
        mMap.addCircle(new CircleOptions()
                .center(myLocation1)
                .fillColor(Color.parseColor("#588C8C8C"))
                .strokeColor(Color.parseColor("#494949")).radius(20));

    }


}
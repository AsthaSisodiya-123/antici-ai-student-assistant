package com.example.dailymart;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dailymart.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Button btnNormal, btnSatellite,btnTerrain,btnHybrid, btnNull,btn3D;
    LocationManager locationManager;  //manage all services/ operations related to location
    public static final int RESOURCES_LOCATION_PERMISSION=1;
    double latitude,longitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},RESOURCES_LOCATION_PERMISSION);
        }
        else if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},RESOURCES_LOCATION_PERMISSION);
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10,
                    new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    latitude=location.getLatitude();
                    longitude=location.getLongitude();

                    Geocoder geocoder=new Geocoder(MapsActivity.this);
                     try {
                         List<Address> addressList= geocoder.getFromLocation(latitude, longitude, 1);

                         address = addressList.get(0).getAddressLine(0) + "," +
                                 addressList.get(0).getLocality() + "," +
                                 addressList.get(0).getCountryName();

                         LatLng myCurrentLocation=new LatLng(latitude,longitude);

                         mMap.clear();
                         mMap.addMarker(new MarkerOptions().position(myCurrentLocation).title(address));
                         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation,16),
                                 2000,null);

                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }

                }
            });
        }

        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    2000,
                    10,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {

                            latitude=location.getLatitude();
                            longitude=location.getLongitude();

                            Geocoder geocoder=new Geocoder(MapsActivity.this);
                            try {
                                List<Address> addressList= geocoder.getFromLocation(latitude, longitude, 1);

                                address = addressList.get(0).getAddressLine(0) + "," +
                                        addressList.get(0).getLocality() + "," +
                                        addressList.get(0).getCountryName();

                                LatLng myCurrentLocation1=new LatLng(latitude,longitude);

                                mMap.addMarker(new MarkerOptions().position(myCurrentLocation1).title(address));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation1,16),
                                        2000,null);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
        }

        btnNormal=findViewById(R.id.btnNormal);
        btnSatellite=findViewById(R.id.btnSatellite);
        btnTerrain=findViewById(R.id.btnTerrain);
        btnHybrid=findViewById(R.id.btnHybrid);
        btnNull=findViewById(R.id.btnNull);
        btn3D=findViewById(R.id.btn3D);


        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btnNormal.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        btnSatellite.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnTerrain.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btnHybrid.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnNull.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NONE));

        btn3D.setOnClickListener(v -> {
            if (mMap != null && latitude != 0 && longitude != 0) {
                LatLng currentLocation = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(18)
                        .tilt(60)
                        .bearing(90)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
    }


}
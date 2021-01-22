package com.monresto.acidlabs.monresto.UI.Maps;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.monresto.acidlabs.monresto.GPSTracker;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Homepage.HomepageActivity;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GPSTracker gpsTracker;
    Geocoder geocoder;
    double lat;
    double lng;
    String title;
    LatLng initPosition;
    @BindView(R.id.text_address_position)
    TextView text_address_position;
    List<Address> addresses;
    LatLng[] latlng = new LatLng[2];
    private HuaweiMap mMap;
    private boolean updatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        gpsTracker = new GPSTracker(this);
        geocoder = new Geocoder(getBaseContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button button = findViewById(R.id.buttonPickPosition);
        ImageView finishBtn = findViewById(R.id.buttonFinish);

        button.setOnClickListener(e -> closeWithResults());
        finishBtn.setOnClickListener(e -> closeWithResults());

        Intent intent = getIntent();
        updatePosition = intent.getBooleanExtra("update_position", false);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        mMap = huaweiMap;
        Intent intent = getIntent();
        boolean update = intent.getBooleanExtra("update", false);
        if (update) {
            this.lat = intent.getDoubleExtra("lat", 0);
            this.lng = intent.getDoubleExtra("lng", 0);
            LatLng position = new LatLng(this.lat, this.lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        } else if (checkLocationPermission() && gpsTracker.canGetLocation()) {
            LatLng position = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            this.lat = position.latitude;
            this.lng = position.longitude;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        } else {
            LatLng position = new LatLng(36.8624, 10.1955);
            this.lat = position.latitude;
            this.lng = position.longitude;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        }

        initPosition = new LatLng(this.lat, this.lng);

        mMap.setOnCameraIdleListener(() -> {
            try {
                addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                title = "Votre adresse";
                if (!addresses.isEmpty()) {
                    title = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (text_address_position != null) {
                text_address_position.setText(title);
            }
        });
    }

    public void closeWithResults() {
        lat = mMap.getCameraPosition().target.latitude;
        lng = mMap.getCameraPosition().target.longitude;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("lat", lat);
        resultIntent.putExtra("lon", lng);
        setResult(Activity.RESULT_OK, resultIntent);

        if (updatePosition) {
            Semsem.setLat(lat);
            Semsem.setLon(lng);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Demande d'autorisation")
                        .setMessage("Monresto a besoin de savoir votre position")
                        .setPositiveButton("Accepter", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create();
                alertDialog.show();

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    void resetMap() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 12));
    }
}
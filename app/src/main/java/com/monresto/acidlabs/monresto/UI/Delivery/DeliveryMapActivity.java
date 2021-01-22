package com.monresto.acidlabs.monresto.UI.Delivery;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;


import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Maps.MapsActivity;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnAddress1)
    Button btnAddress1;
    @BindView(R.id.btnAddress2)
    Button btnAddress2;
    LatLng[] latlng = new LatLng[2];
    private HuaweiMap mMap;
    private Geocoder geocoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_delivery);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        btnAddress1.setOnClickListener(e -> {
            Intent intent = new Intent(this, MapsActivity.class);
            Bundle options = new Bundle();
            options.putInt("for", 1);
            startActivityForResult(intent, 1, options);
        });
        btnAddress1.setOnClickListener(e -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, Config.REQUEST_CODE_ADDRESS_PICK_1);
        });

        btnAddress2.setOnClickListener(e -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, Config.REQUEST_CODE_ADDRESS_PICK_2);
        });

        geocoder = new Geocoder(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(HuaweiMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_action_view, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_CODE_ADDRESS_PICK_1:
                if (resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 36.849109);
                    double lon = data.getDoubleExtra("lon", 10.166124);
                    latlng[0] = new LatLng(lat, lon);

                    android.location.Address address;
                    try {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        if (addressList != null && !addressList.isEmpty()) {
                            address = addressList.get(0);
                            btnAddress1.setText(address.getFeatureName());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Config.REQUEST_CODE_ADDRESS_PICK_2:
                if (resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 36.849109);
                    double lon = data.getDoubleExtra("lon", 10.166124);
                    latlng[1] = new LatLng(lat, lon);

                    android.location.Address address;
                    try {
                        List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        if (addressList != null && !addressList.isEmpty()) {
                            address = addressList.get(0);
                            btnAddress2.setText(address.getAddressLine(address.getMaxAddressLineIndex()));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}

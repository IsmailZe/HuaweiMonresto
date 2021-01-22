package com.monresto.acidlabs.monresto.UI;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.Menu;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.PlacePickerDialog;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements RestaurantAsyncResponse {
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        final RestaurantService service = new RestaurantService(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            service.getAll(location.getLatitude(), location.getLongitude());
                        }
                    }
                });


    }

    @Override
    public void onListReceived(ArrayList<Restaurant> restaurantList, int status) {

    }


    @Override
    public void onMenusReceived(ArrayList<Menu> menus) {

    }


    @Override
    public void onDishesReceived(ArrayList<Dish> dishes, Menu menu) {

    }

    @Override
    public void onComposedDishReceived(Dish dish) {

    }

    @Override
    public void onSpecialitiesReceived(ArrayList<Speciality> specialities) {

    }

    @Override
    public void onServerDown() {

    }

    @Override
    public void onError(VolleyError error) {

    }
}

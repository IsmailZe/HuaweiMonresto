package com.monresto.acidlabs.monresto.UI.Profile.Address;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Address;

import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.monresto.acidlabs.monresto.Model.City;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.OnActionPerformed;
import com.monresto.acidlabs.monresto.PlacePickerDialog;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.City.CityAsyncResponse;
import com.monresto.acidlabs.monresto.Service.City.CityService;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Main.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.monresto.acidlabs.monresto.Config.REQUEST_CODE_MAP_INFO;

public class NewAddressActivity extends AppCompatActivity implements CityAsyncResponse, UserAsyncResponse {
    @BindView(R.id.location_spinner)
    Spinner locationSpinner;
    @BindView(R.id.textAddress)
    EditText textAddress;
    @BindView(R.id.textStreet)
    EditText textStreet;
    @BindView(R.id.textAppart)
    EditText textAppart;
    @BindView(R.id.textComment)
    EditText textComment;
    @BindView(R.id.city_spinner)
    Spinner citySpinner;
    @BindView(R.id.buttonSubmitAddress)
    Button buttonSubmitAddress;
    @BindView(R.id.imageProfileBack)
    ImageView imageProfileBack;
    @BindView(R.id.progressBarAddAddress)
    ProgressBar progressBarAddAddress;

    ArrayList<City> cities;
    Address address;
    Geocoder geocoder;
    UserService userService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_new);
        ButterKnife.bind(this);

        address = new Address();
        geocoder = new Geocoder(this);


        PlacePickerDialog placePickerDialog = new PlacePickerDialog();
        placePickerDialog.setOnLocationSelected(
                latLng -> {
                    address.setLat(latLng.latitude);
                    address.setLon(latLng.longitude);
                    placePickerDialog.setOnAddressFetched(object -> {
                        address.setAdresse(object);
                        runOnUiThread(() -> textAddress.setText(object));
                    });
                });
        placePickerDialog.show(getSupportFragmentManager(), "");
        /*PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), Config.REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e1) {
            e1.printStackTrace();
        }*/

        buttonSubmitAddress.setOnClickListener(e -> {
            address.setAdresse(textAddress.getText().toString());
            address.setEmplacement(locationSpinner.getSelectedItem().toString());
            address.setRue(textStreet.getText().toString());
            address.setAppartement(textAppart.getText().toString());
            address.setCityID(0);

            userService = new UserService(this);
            userService.addAddress(address);

            progressBarAddAddress.setVisibility(View.VISIBLE);
            buttonSubmitAddress.setVisibility(View.GONE);
        });
        imageProfileBack.setOnClickListener(e -> {
            finish();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter_city = ArrayAdapter.createFromResource(this,
                R.array.default_city_array, android.R.layout.simple_spinner_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter_city);

        cities = new ArrayList<>();
        CityService cityService = new CityService(this);
        cityService.getCities();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_MAP_INFO): {
                if (resultCode == Activity.RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 36.849109);
                    double lon = data.getDoubleExtra("lon", 10.166124);

                    android.location.Address currentAddress;
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                        if (addressList != null && !addressList.isEmpty()) {
                            currentAddress = addressList.get(0);
                            address.setPostalCode(currentAddress.getPostalCode());
                            textAddress.setText(currentAddress.getAddressLine(0));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    address.setLat(lat);
                    address.setLon(lon);

                } else if (resultCode == Activity.RESULT_CANCELED)
                    finish();
            }
            break;
            case (Config.REQUEST_PLACE_PICKER): {
                if (data == null)
                    finish();
                else {
                    /*Place place = PlacePicker.getPlace(data, this);
                    textAddress.setText(place.getAddress());
                    address.setAdresse(place.getAddress().toString());
                    address.setLat(place.getLatLng().latitude);
                    address.setLon(place.getLatLng().longitude);*/
                }
            }
            break;
        }
    }

    @Override
    public void onCitiesReceived(ArrayList<City> cities) {
        ArrayList<CharSequence> list = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++)
            list.add(cities.get(i).getName());

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    @Override
    public void onAddressAddResponse(boolean success) {
        if (success && User.getInstance() != null) {
            userService.getAddress(User.getInstance().getId());
            finish();
        } else
            Toast.makeText(this, "Une erreur est survenue lors de l'ajout de votre adresse", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        User user = User.getInstance();
        if (user != null) {
            user.setAddresses(addresses);
        }
    }


}

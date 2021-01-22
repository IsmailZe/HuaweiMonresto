package com.monresto.acidlabs.monresto.UI.User.RegisterFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.huawei.hms.maps.HuaweiMap;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.City;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.City.CityService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRegisterAddress extends Fragment {
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
    ArrayList<City> cities;
    private HuaweiMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root;
        root = (ViewGroup) inflater.inflate(R.layout.fragment_register_address, container, false);
        ButterKnife.bind(this, root);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter_city = ArrayAdapter.createFromResource(getContext(),
                R.array.default_city_array, android.R.layout.simple_spinner_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter_city);

        cities = new ArrayList<>();
        CityService cityService = new CityService(getContext());
        cityService.getCities();

        return root;
    }

    public boolean validate() {
        return !textAddress.getText().toString().equals("");
    }

    public Address fill(Address address) {
        address.setAdresse(textAddress.getText().toString());
        address.setRue(textStreet.getText().toString());
        address.setAppartement(textAppart.getText().toString());
        int cityID = 1;
        if (!cities.isEmpty())
            for (int i = 0; i < cities.size(); i++) {
                if (citySpinner.getSelectedItem().toString().equals(cities.get(i).getName())) {
                    cityID = cities.get(i).getId();
                    break;
                }
            }
        address.setCityID(cityID);
        address.setEmplacement(locationSpinner.getSelectedItem().toString());
        return address;
    }

    public User addComment(User user) {
        user.setComment(textComment.getText().toString());
        return user;
    }

    public void fillCities(ArrayList<City> cities) {
        try {
            ArrayList<CharSequence> list = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++)
                list.add(cities.get(i).getName());

            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

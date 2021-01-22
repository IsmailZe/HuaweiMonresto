package com.monresto.acidlabs.monresto.Service.City;

import com.monresto.acidlabs.monresto.Model.City;

import java.util.ArrayList;

public interface CityAsyncResponse {
    void onCitiesReceived(ArrayList<City> cities);
}

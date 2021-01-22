package com.monresto.acidlabs.monresto.Service.City;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.City;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityService {
    private Context context;
    private ArrayList<City> cities;

    public CityService(Context context) {
        this.context = context;
    }

    public void getCities() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "City/cityZone.php";
        cities = new ArrayList<>();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objResponse = new JSONObject(response);
                            JSONArray cityArray = objResponse.getJSONArray("City");
                            JSONObject obj;
                            for (int i = 0; i < cityArray.length(); i++) {
                                obj = cityArray.getJSONObject(i);
                                cities.add(new City(obj.getInt("cityID"), obj.getString("cityName"), null));
                            }
                            ((CityAsyncResponse) context).onCitiesReceived(cities);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5(Config.sharedKey);
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }
}

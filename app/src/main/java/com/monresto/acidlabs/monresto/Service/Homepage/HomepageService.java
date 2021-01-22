package com.monresto.acidlabs.monresto.Service.Homepage;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.HomepageConfig;
import com.monresto.acidlabs.monresto.Model.HomepageDish;
import com.monresto.acidlabs.monresto.Model.HomepageEvent;
import com.monresto.acidlabs.monresto.Model.Setting;
import com.monresto.acidlabs.monresto.UI.Homepage.HomeItem;
import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomepageService {
    private Context context;

    public HomepageService(Context context) {
        this.context = context;
    }

    public void getAll() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.acidlabsServer + "homepage";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        JSONObject config = response.getJSONObject("Config");
                        HomepageConfig homepageConfig = new HomepageConfig(config.optInt("id"), config.optString("cover_image"), config.optString("busket_image"), config.optString("snack"), config.optString("delivery"), config.optString("created_at"), config.optString("updated_at"));
                        ((HomepageAsyncResponse) context).onHomepageConfigReceived(homepageConfig);

                        JSONArray dishesJSON = response.getJSONArray("Dishes");
                        ArrayList<HomepageDish> dishes = new ArrayList<>();
                        for (int i = 0; i < dishesJSON.length(); i++) {
                            JSONObject dish = dishesJSON.getJSONObject(i);
                            dishes.add(new HomepageDish(dish.optInt("id"), dish.optString("label"), dish.optString("title"), dish.getString("image"), dish.optInt("restoID"), dish.optInt("dishID"), dish.optString("display_date"), dish.getString("created_at"), dish.optString("updated_at")));
                        }
                        ((HomepageAsyncResponse) context).onHomepageDishesReceived(dishes);

                        JSONArray eventsJSON = response.getJSONArray("Events");
                        ArrayList<HomepageEvent> events = new ArrayList<>();
                        for (int i = 0; i < eventsJSON.length(); i++) {
                            JSONObject event = eventsJSON.getJSONObject(i);
                            events.add(new HomepageEvent(event.optInt("id"), event.optString("label"), event.optString("title"), event.getString("image"), event.optInt("restoID"), event.getString("restoIcon"), event.optString("display_date"), event.getString("created_at"), event.optString("updated_at")));
                        }
                        ((HomepageAsyncResponse) context).onHomepageEventsReceived(events);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    ((HomepageAsyncResponse) context).onHomepageError(true);
                });

        queue.add(request);
    }

    public void getAppSettings() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/app_settings.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonSettings = jsonResponse.getJSONArray("Settings");
                            ArrayList<Setting> settingArrayList = new ArrayList<>();
                            for (int i = 0; i < jsonSettings.length(); i++) {
                                JSONObject setting = jsonSettings.getJSONObject(i);
                                settingArrayList.add(new Setting(setting.optString("id"),
                                        setting.optString("label"),
                                        setting.optString("valeur"),
                                        setting.optString("status")));
                            }


                            ((HomepageAsyncResponse) context).onHomepageSettingsReceived(settingArrayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //((RestaurantAsyncResponse) context).onError(error);
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


    public void getHome() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Home/settings.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        JSONArray listJSON = jsonResponse.getJSONArray("list");
                        ArrayList<HomeItem> listHomeElements = new ArrayList<>();
                        for (int i = 0; i < listJSON.length(); i++) {
                            JSONObject homeItemJSON = listJSON.getJSONObject(i);
                            listHomeElements.add(new HomeItem(homeItemJSON.optString("name"), homeItemJSON.optString("indication"), homeItemJSON.optString("description"), homeItemJSON.optString("icon"), homeItemJSON.optString("notif"), homeItemJSON.optString("serviceType"), homeItemJSON.optString("clickToAction"), homeItemJSON.optInt("status", 0)));
                        }

                        JSONArray gridJSON = jsonResponse.getJSONArray("grid");
                        ArrayList<HomeItem> gridHomeElements = new ArrayList<>();
                        for (int i = 0; i < gridJSON.length(); i++) {
                            JSONObject homeItemJSON = gridJSON.getJSONObject(i);
                            gridHomeElements.add(new HomeItem(homeItemJSON.optString("name"), homeItemJSON.optString("indication"), homeItemJSON.optString("description"), homeItemJSON.optString("icon"), homeItemJSON.optString("notif"), homeItemJSON.optString("serviceType"), homeItemJSON.optString("clickToAction"), homeItemJSON.optInt("status", 0)));
                        }
                        ((HomepageAsyncResponse) context).onHomeLoaded(listHomeElements, gridHomeElements);

                    } catch (JSONException e) {

                    }
                },
                error -> Log.d("ERROR", "onErrorResponse: " + error.getMessage())
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

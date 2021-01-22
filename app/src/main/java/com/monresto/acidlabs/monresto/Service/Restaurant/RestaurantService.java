package com.monresto.acidlabs.monresto.Service.Restaurant;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.FAQ;
import com.monresto.acidlabs.monresto.Model.Menu;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.Service.FAQ.FAQAsyncResponse;
import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantService {
    private Context context;

    public RestaurantService(Context context) {
        this.context = context;
    }

    public void getAll(final double lat, final double lon) {


        final ArrayList<Restaurant> RestaurantList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/findRestoGeo.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (!Utilities.isJSONValid(response)) {
                                ((RestaurantAsyncResponse) context).onServerDown();
                                return;
                            }
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status == 0) {
                                ((RestaurantAsyncResponse) context).onNoRestaurantsFound();
                                return;
                            }
                            JSONArray resto = jsonResponse.getJSONArray("Resto");
                            JSONObject obj;
                            for (int i = 0; i < resto.length(); i++) {
                                obj = resto.getJSONObject(i);
                                RestaurantList.add(Restaurant.createFromJson(obj));
                            }
                            ((RestaurantAsyncResponse) context).onListReceived(RestaurantList, status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int type = 0;
                String signature = Utilities.md5("" + lon + lat + type + Config.sharedKey);
                params.put("longitude", String.valueOf(lon));
                params.put("latitude", String.valueOf(lat));
                params.put("type", String.valueOf(type));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public void getRestoByRubrique(final double lat, final double lon, final String rubrique) {
        final ArrayList<Restaurant> RestaurantList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/findRestoGeo.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (!Utilities.isJSONValid(response)) {
                                ((RestaurantAsyncResponse) context).onServerDown();
                                return;
                            }
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status == 0) {
                                ((RestaurantAsyncResponse) context).onNoRestaurantsFound();
                                return;
                            }
                            JSONArray resto = jsonResponse.getJSONArray("Resto");
                            JSONObject obj;
                            for (int i = 0; i < resto.length(); i++) {
                                obj = resto.getJSONObject(i);
                                RestaurantList.add(Restaurant.createFromJson(obj));
                            }
                            ((RestaurantAsyncResponse) context).onListReceived(RestaurantList, status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int type = 0;
                String signature = Utilities.md5("" + lon + lat + type + Config.sharedKey);
                params.put("longitude", String.valueOf(lon));
                params.put("latitude", String.valueOf(lat));
                params.put("type", String.valueOf(type));
                params.put("rubrique", rubrique);
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public void getDetails(final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/restoDetails.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject resto = jsonResponse.getJSONObject("Resto");
                            Restaurant restaurant = Restaurant.createFromJson(resto);
                            ((RestaurantAsyncResponse) context).onDetailsReceived(restaurant);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5("" + id + Config.sharedKey);
                params.put("restoID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public void getMenus(final int id) {
        final ArrayList<Menu> menusList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/menu.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonMenus = jsonResponse.getJSONArray("Menus");
                            JSONObject obj;
                            for (int i = 0; i < jsonMenus.length(); i++) {
                                obj = jsonMenus.getJSONObject(i);
                                menusList.add(Menu.createFromJson(obj));
                            }
                            ((RestaurantAsyncResponse) context).onMenusReceived(menusList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5("" + id + Config.sharedKey);
                params.put("restoID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getDishes(final int restoID, final Menu menu) {
        ArrayList<Dish> dishesList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/subMenu.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonMenus = jsonResponse.getJSONArray("Dishes");
                            JSONObject obj;
                            for (int i = 0; i < jsonMenus.length(); i++) {
                                obj = jsonMenus.getJSONObject(i);
                                dishesList.add(Dish.createFromJson(obj));
                            }

                            ((RestaurantAsyncResponse) context).onDishesReceived(dishesList, menu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                int userID = User.getInstance() == null ? 0 : User.getInstance().getId();
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5("" + userID + restoID + menu.getId() + Config.sharedKey);
                params.put("userID", String.valueOf(userID));
                params.put("restoID", String.valueOf(restoID));
                params.put("menuID", String.valueOf(menu.getId()));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getComposedDish(final Dish dish) {
        if (!dish.isComposed())
            return;
        final int dishID = dish.getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Restaurant/composedDish.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject jsonComposed = jsonResponse.getJSONObject("Composed");
                            JSONArray jsonDimensions = jsonComposed.getJSONArray("Dimensions");
                            JSONArray jsonComponents = jsonComposed.getJSONArray("Components");

                            for (int i = 0; i < jsonDimensions.length(); i++) {
                                JSONObject dimensionObject = jsonDimensions.getJSONObject(i);
                                dish.addDimension(dimensionObject.optInt("dimensionID"), dimensionObject.optString("title"), dimensionObject.optDouble("price"));
                            }
                            for (int i = 0; i < jsonComponents.length(); i++) {
                                JSONObject componentObject = jsonComponents.getJSONObject(i);
                                JSONArray jsonOptions = componentObject.getJSONArray("Options");
                                ArrayList<Dish.Option> options = new ArrayList<>();
                                for (int j = 0; j < jsonOptions.length(); j++) {
                                    JSONObject optionObject = jsonOptions.getJSONObject(j);
                                    options.add(new Dish.Option(optionObject.optInt("optionID"), optionObject.optString("optionTitle"), optionObject.optDouble("optionPrice")));
                                }
                                dish.addComponent(componentObject.optInt("componentID"), componentObject.optString("componentName"), componentObject.optInt("numberChoice"), componentObject.optInt("numberChoiceMax"), options);
                            }
                            ((RestaurantAsyncResponse) context).onComposedDishReceived(dish);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5("" + dishID + Config.sharedKey);
                params.put("dishID", String.valueOf(dishID));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void setFavorite(final int dishID, final boolean favorite) {
        //userID , dishID , isFavorite, signature
        final int userID = User.getInstance().getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "User/addFavoriteDish.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String signature = Utilities.md5("" + userID + dishID + (favorite ? 1 : 0) + Config.sharedKey);
                params.put("userID", String.valueOf(userID));
                params.put("dishID", String.valueOf(dishID));
                params.put("isFavorite", String.valueOf((favorite ? 1 : 0)));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getSpecialities() {
        final int userID = User.getInstance() == null ? 0 : User.getInstance().getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "Speciality/speciality.php";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<Speciality> specialities = new ArrayList<>();
                            JSONArray specialitiesJson = response.getJSONArray("Speciality");
                            for (int i = 0; i < specialitiesJson.length(); i++) {
                                JSONObject spec = specialitiesJson.getJSONObject(i);
                                specialities.add(new Speciality(Integer.valueOf(spec.optString("specialityID")), spec.optString("specialityName")));
                            }
                            ((RestaurantAsyncResponse) context).onSpecialitiesReceived(specialities);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((RestaurantAsyncResponse) context).onError(error);
                    }
                });

        queue.add(request);
    }

    public void verifyPromo(String code) {
        ((RestaurantAsyncResponse) context).onPromoResponse();
    }


}

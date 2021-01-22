package com.monresto.acidlabs.monresto.Service;

import android.util.Log;

import com.huawei.hms.maps.model.LatLng;
import com.monresto.acidlabs.monresto.OnActionPerformed;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeocodingService {
    public static final String ROOT_URL = "https://siteapi.cloud.huawei.com/mapApi/v1/siteService/";

    public static final String conection = "?key=";
    public static final String apiKey = "CgB6e3x9Wggw82kre5tCmJfpXpVj1q2wddTP3bGKbn8LFTEhaZNrG2VQPOGQdyciR4TCFSCLziopZ6wHkmIeGrPQ";
    public static final String serviceName = "reverseGeocode";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static void reverseGeocoding(LatLng latLng, OnActionPerformed<String> onAdressFetched) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        JSONObject location = new JSONObject();
        try {
            location.put("lng", latLng.longitude);
            location.put("lat", latLng.latitude);
            json.put("location", location);

        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(json));

        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder().url(ROOT_URL + serviceName + conection + URLEncoder.encode(apiKey, "UTF-8"))
                        .post(body)
                        .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ReverseGeocoding", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    onAdressFetched.onPerform(jsonObject.getJSONArray("sites").getJSONObject(0).getString("formatAddress"));
                } catch (Exception e) {
                    Log.e("ERROOOOOR", e.getMessage() + " --- " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
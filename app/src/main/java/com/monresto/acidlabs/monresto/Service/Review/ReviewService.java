package com.monresto.acidlabs.monresto.Service.Review;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Review;
import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewService {
    private Context context;
    private Fragment fragment;
    private ArrayList<Review> ReviewList;

    public ReviewService(Context context) {
        this.context = context;
        ReviewList = new ArrayList<>();
    }

    public ReviewService(Fragment fragment) {
        this.fragment = fragment;
        ReviewList = new ArrayList<>();
    }

    /**
     * @param id Restaurant id
     */
    public void getAll(final int id) {
        RequestQueue queue = Volley.newRequestQueue(fragment.getContext());
        String url = Config.server + "Review/reviews.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray resto = jsonResponse.getJSONArray("Reviews");
                            JSONObject obj;
                            for (int i = 0; i < resto.length(); i++) {
                                obj = resto.getJSONObject(i);
                                ReviewList.add(Review.createFromJson(obj));
                            }
                            if (context != null)
                                ((ReviewAsyncResponse) context).onReviewsReceived(ReviewList);
                            else if (fragment != null)
                                ((ReviewAsyncResponse) fragment).onReviewsReceived(ReviewList);

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
                String signature = Utilities.md5("" + id + Config.sharedKey);
                params.put("restoID", String.valueOf(id));
                params.put("signature", signature);

                return params;
            }
        };
        queue.add(postRequest);

    }

}

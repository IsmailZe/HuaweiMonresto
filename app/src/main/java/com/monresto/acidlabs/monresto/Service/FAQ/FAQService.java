package com.monresto.acidlabs.monresto.Service.FAQ;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.FAQ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FAQService {
    private Context context;
    private ArrayList<FAQ> FAQList;

    public FAQService(Context context) {
        this.context = context;
        FAQList = new ArrayList<>();
    }

    public void getAll() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.server + "FAQ/faq.php";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray faq = response.getJSONArray("FAQ");
                            JSONObject obj;
                            for (int i = 0; i < faq.length(); i++) {
                                obj = faq.getJSONObject(i);
                                FAQList.add(new FAQ(obj.getString("question"), obj.getString("answer")));
                            }
                            ((FAQAsyncResponse) context).processFinish(FAQList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        queue.add(request);
    }
}

package com.monresto.acidlabs.monresto.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Version;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Dialog.DialogMonrestoForcedUpdate;
import com.monresto.acidlabs.monresto.UI.Dialog.DialogMonrestoInfo;
import com.monresto.acidlabs.monresto.UI.Homepage.HomepageActivity;
import com.monresto.acidlabs.monresto.UI.Homepage.Snacks.SnacksActivity;
import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends Activity {

    PackageInfo pInfo;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splashscreen);
        checkForUpdates();
        printHashKey(this);

    }

    public void checkForUpdates() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Config.server + "Version/version.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                        Version a = new Version(version);
                        Version b = new Version(jsonObject.optString("versionNameAndroid"));
                        if (a.compareTo(b) < 0) {
                            DialogMonrestoForcedUpdate dialogMonrestoForcedUpdate = new DialogMonrestoForcedUpdate(SplashScreen.this);
                            dialogMonrestoForcedUpdate.setCancelable(false);
                            dialogMonrestoForcedUpdate.show();
                        } else startApplication();
                    } catch (Exception e) {

                    }
                },
                error -> startApplication()
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

    public void startApplication() {
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, HomepageActivity.class);
            startActivity(i);
            finish();
        }, 1000);
    }

    private void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                //Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
                System.out.println("printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
        }
    }

}



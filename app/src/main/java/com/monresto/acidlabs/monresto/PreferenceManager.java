package com.monresto.acidlabs.monresto;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    public static void saveFirstSemsemPayTuto(Context context) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("first_semsempay_tuto", false).apply();
    }

    public static boolean isFirstSemsemPayTuto(Context context) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("first_semsempay_tuto", true);
    }
}

package com.monresto.acidlabs.monresto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Utilities {
    private static AlertDialog infoDialog;

    public static String md5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public static String decodeUTF(String encoded) {
        String decoded = encoded;
        try {
            decoded = new String(encoded.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            return encoded;
        }
        return decoded;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static int convDpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    // Use this only for Loading or Breakdown alerts
    public static void statusChanger(Context context, int resource, ViewGroup statusContainer, ViewGroup listContainer) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(resource, statusContainer, false);

        statusContainer.removeAllViews();
        statusContainer.addView(layout);

        listContainer.setVisibility(View.INVISIBLE);
        statusContainer.setVisibility(View.VISIBLE);
    }

    // Use this only for Unavailability alerts (+Custom message)
    public static void statusChangerUnavailable(Context context, String message, ViewGroup statusContainer, ViewGroup listContainer) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = Objects.requireNonNull(inflater).inflate(R.layout.fragment_unavailable, statusContainer, false);

        TextView unavailable_msg = layout.findViewById(R.id.unavailable_msg);
        unavailable_msg.setText(message);

        statusContainer.removeAllViews();
        statusContainer.addView(layout);

        listContainer.setVisibility(View.INVISIBLE);
        statusContainer.setVisibility(View.VISIBLE);
    }


    public static void statusUnavailable(Context context, String message, ViewGroup statusContainer, ViewGroup listContainer) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = Objects.requireNonNull(inflater).inflate(R.layout.fragment_unavailable, statusContainer, false);

        TextView unavailable_msg = layout.findViewById(R.id.unavailable_msg);
        ImageView img = layout.findViewById(R.id.imageView8);

        img.setImageResource(R.drawable.del);
        Button btn = layout.findViewById(R.id.unavailable_btn);
        btn.setVisibility(View.VISIBLE);
        unavailable_msg.setText(message);

        statusContainer.removeAllViews();
        statusContainer.addView(layout);

        listContainer.setVisibility(View.INVISIBLE);
        statusContainer.setVisibility(View.VISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listContainer.setVisibility(View.VISIBLE);
                statusContainer.setVisibility(View.INVISIBLE);

            }
        });
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static void showInfoDialog(final Activity activity, String title,
                                      String msg, boolean cancellable, String positiveButtonText,
                                      final Runnable positivefunction) {
        if (!activity.isFinishing()) {
            Log.d("show dialog", "my dialog");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle(title).setMessage(msg)
                    .setCancelable(cancellable);

            alertDialogBuilder.setPositiveButton(positiveButtonText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (positivefunction == null) {

                                return;
                            }

                            try {
                                activity.runOnUiThread(positivefunction);
                            } catch (Exception e) {
                            }
                        }
                    });

            if (infoDialog != null) {
                if (infoDialog.isShowing()) {
                    try {
                        infoDialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            }

            infoDialog = alertDialogBuilder.create();
            infoDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorAccent));
                }
            });
            infoDialog.show();
        }
    }


    /**
     * validate email input
     *
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * network check
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void changeStatusBarColors(Activity activity, int color) {
        try {
            Window w = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }

            // finally change the color

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.setStatusBarColor(ContextCompat.getColor(activity, color));
            }

            if ((color == R.color.white) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                w.getDecorView().setSystemUiVisibility(0);
            }


        } catch (Exception e) {
        }


    }


}

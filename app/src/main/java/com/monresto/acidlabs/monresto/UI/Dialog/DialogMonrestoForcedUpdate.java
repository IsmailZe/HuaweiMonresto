package com.monresto.acidlabs.monresto.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.monresto.acidlabs.monresto.BuildConfig;
import com.monresto.acidlabs.monresto.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DialogMonrestoForcedUpdate extends Dialog {

    private Activity activity;
    private Unbinder unbinder;

    public DialogMonrestoForcedUpdate(Activity context) {
        super(context, R.style.DialogFullScreen);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_monresto_forced_update);
        unbinder = ButterKnife.bind(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @OnClick(R.id.btn_update)
    void close() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
        } catch (ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        }
    }

}

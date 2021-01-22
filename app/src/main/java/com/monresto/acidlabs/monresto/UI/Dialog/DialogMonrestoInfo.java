package com.monresto.acidlabs.monresto.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;

import com.monresto.acidlabs.monresto.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DialogMonrestoInfo extends Dialog {

    private Activity activity;
    private Unbinder unbinder;

    public DialogMonrestoInfo(Activity context) {
        super(context, R.style.DialogFullScreen);
        this.activity = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_monresto_info);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_submit)
    void close() {
        dismiss();
    }

}

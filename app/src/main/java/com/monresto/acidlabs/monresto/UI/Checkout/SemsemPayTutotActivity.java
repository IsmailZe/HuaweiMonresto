package com.monresto.acidlabs.monresto.UI.Checkout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SemsemPayTutotActivity extends AppCompatActivity {
    @BindView(R.id.iv_arrow_back)
    ImageView ivBack;
    @BindView(R.id.tv_i_understand)
    View tvDone;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semsem_pay_tuto);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(e -> finish());
        tvDone.setOnClickListener(e -> finish());


    }


}

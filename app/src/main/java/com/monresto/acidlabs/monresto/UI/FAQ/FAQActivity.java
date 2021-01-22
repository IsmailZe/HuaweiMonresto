package com.monresto.acidlabs.monresto.UI.FAQ;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.monresto.acidlabs.monresto.Model.FAQ;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.FAQ.FAQAsyncResponse;
import com.monresto.acidlabs.monresto.Service.FAQ.FAQService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQActivity extends AppCompatActivity implements FAQAsyncResponse {
    @BindView(R.id.imageProfileBack)
    ImageView imageProfileBack;
    @BindView(R.id.faq_loading)
    ConstraintLayout faq_loading;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    FAQRecyclerViewAdapter adapter;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);

        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        adapter = new FAQRecyclerViewAdapter(this);

        imageProfileBack.setOnClickListener(e -> {
            finish();
        });

        FAQService faqService = new FAQService(this);
        faqService.getAll();
    }


    @Override
    public void processFinish(ArrayList<FAQ> FAQList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setFAQ(FAQList);
        adapter.notifyDataSetChanged();

        faq_loading.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}

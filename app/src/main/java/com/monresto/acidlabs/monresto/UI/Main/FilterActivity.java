package com.monresto.acidlabs.monresto.UI.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Window;
import android.widget.LinearLayout;

import com.monresto.acidlabs.monresto.Model.Filter;
import com.monresto.acidlabs.monresto.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {
    FilterRecyclerViewAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sorting);
        ButterKnife.bind(this);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
        getWindow().setLayout(width, height);

        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        adapter = new FilterRecyclerViewAdapter(this);
        adapter.setFilters(Filter.getFilters());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    void sendFilter(int filter) {
        Intent intent = new Intent();
        intent.putExtra("filter", filter);
        setResult(RESULT_OK, intent);
        finish();
    }
}

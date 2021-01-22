package com.monresto.acidlabs.monresto.UI.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Profile.Address.NewAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectAddressActivity extends AppCompatActivity implements UserAsyncResponse, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerview_address)
    RecyclerView recyclerview_address;
    @BindView(R.id.address_pull_to_refresh)
    TextView address_pull_to_refresh;
    @BindView(R.id.status_address)
    ConstraintLayout status_address;
    @BindView(R.id.swiper_address)
    SwipeRefreshLayout swiper_address;
    @BindView(R.id.btnClose)
    ImageView btnClose;
    @BindView(R.id.buttonNewAddress)
    Button buttonNewAddress;

    AddressRecyclerViewAdapter adapter;
    UserService userService;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_address_select);
        ButterKnife.bind(this);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
        getWindow().setLayout(width, height);

        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        swiper_address.setOnRefreshListener(this);

        userService = new UserService(this);
        adapter = new AddressRecyclerViewAdapter(this);
        adapter.setServiceType(getIntent().getStringExtra("EXTRA_SESSION_ID"));
        recyclerview_address.setLayoutManager(new LinearLayoutManager(this));

        userService.getAddress(User.getInstance().getId());
        btnClose.setOnClickListener(e -> close());
        buttonNewAddress.setOnClickListener(e -> {
            Intent intent = new Intent(this, NewAddressActivity.class);
            startActivityForResult(intent, Config.REQUEST_CODE_ADRESS_SELECT);
        });
    }

    private void close() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void updateList(ArrayList<Address> addresses) {
        if (addresses.size() == 0) {
            Utilities.statusChangerUnavailable(this, "Aucune adresse trouvée", status_address, swiper_address);
            return;
        }

        adapter.setAddresses(addresses);
        recyclerview_address.setAdapter(adapter);

        status_address.setVisibility(View.INVISIBLE);
        swiper_address.setVisibility(View.VISIBLE);
        address_pull_to_refresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        updateList(addresses);
        if (User.getInstance() != null)
            User.getInstance().setAddresses(addresses);
    }


    @Override
    public void onRefresh() {
        userService.getAddress(User.getInstance().getId());
        swiper_address.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_CODE_ADRESS_SELECT) {
            onRefresh();
        }
    }
}

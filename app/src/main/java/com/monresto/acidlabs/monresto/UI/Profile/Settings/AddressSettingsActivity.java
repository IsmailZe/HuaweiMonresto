package com.monresto.acidlabs.monresto.UI.Profile.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Profile.Address.AddressRecyclerViewAdapter;
import com.monresto.acidlabs.monresto.UI.Profile.Address.NewAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressSettingsActivity extends AppCompatActivity
        implements UserAsyncResponse,
        SwipeRefreshLayout.OnRefreshListener,
        AddressRecyclerViewAdapter.AddressListener {
    @BindView(R.id.recyclerview_address)
    RecyclerView recyclerview_address;
    @BindView(R.id.address_pull_to_refresh)
    TextView address_pull_to_refresh;
    @BindView(R.id.status_address)
    ConstraintLayout status_address;
    @BindView(R.id.swiper_address)
    SwipeRefreshLayout swiper_address;
    @BindView(R.id.buttonNewAddress)
    Button buttonNewAddress;
    @BindView(R.id.buttonBack)
    ImageView buttonBack;
    AddressRecyclerViewAdapter adapter;
    private boolean _hasLoadedOnce = false;
    private UserService userService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings_address);
        ButterKnife.bind(this);

        swiper_address.setOnRefreshListener(this);

        userService = new UserService(this);
        adapter = new AddressRecyclerViewAdapter(this, this);
        recyclerview_address.setLayoutManager(new LinearLayoutManager(this));

        buttonBack.setOnClickListener(e -> finish());
        buttonNewAddress.setOnClickListener(e -> {
            Intent intent = new Intent(this, NewAddressActivity.class);
            startActivity(intent);
        });

        userService.getAddress(User.getInstance().getId());
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
    }


    @Override
    public void onRefresh() {
        userService.getAddress(User.getInstance().getId());
        swiper_address.setRefreshing(false);
    }


    @Override
    public void deleteAddress(int id, int position) {
        if (Utilities.isNetworkAvailable(this)) {
            userService.deleteAddress(id);
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_connection_problem), Toast.LENGTH_SHORT).show();
        }
    }
}

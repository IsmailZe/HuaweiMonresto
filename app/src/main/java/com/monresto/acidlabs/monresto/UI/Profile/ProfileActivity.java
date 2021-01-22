package com.monresto.acidlabs.monresto.UI.Profile;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Model.Order;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Profile.Address.FragmentAddress;
import com.monresto.acidlabs.monresto.UI.Profile.Favorites.FragmentFavorites;
import com.monresto.acidlabs.monresto.UI.Profile.History.FragmentHistory;
import com.monresto.acidlabs.monresto.UI.Profile.Orders.FragmentOrders;
import com.monresto.acidlabs.monresto.UI.Profile.Settings.ProfileSettingsActivity;
import com.monresto.acidlabs.monresto.UI.Restaurants.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements UserAsyncResponse {
    @BindView(R.id.viewPagerProfile)
    ViewPager viewPagerProfile;
    @BindView(R.id.tabLayoutProfile)
    TabLayout tabLayoutProfile;

    @BindView(R.id.imageProfileSettings)
    ImageView imageSettings;

    @BindView(R.id.buttonBack)
    ImageView buttonBack;

    FragmentAddress fragmentAddress;

    ViewPagerAdapter adapter;

    FragmentOrders fragmentOrders;
    FragmentHistory fragmentHistory;
    FragmentFavorites fragmentFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        fragmentAddress = new FragmentAddress();
        fragmentOrders = new FragmentOrders();
        fragmentHistory = new FragmentHistory();
        fragmentFavorites = new FragmentFavorites();

        adapter.AddFragment(fragmentOrders, "Commandes");
        adapter.AddFragment(fragmentHistory, "Historique");
        adapter.AddFragment(fragmentFavorites, "Favoris");
        adapter.AddFragment(fragmentAddress, "Adresses");

        viewPagerProfile.setAdapter(adapter);
        viewPagerProfile.setOffscreenPageLimit(4);
        tabLayoutProfile.setupWithViewPager(viewPagerProfile);

        tabLayoutProfile.getTabAt(0).setIcon(R.drawable.icon_orders);
        tabLayoutProfile.getTabAt(1).setIcon(R.drawable.icon_history);
        tabLayoutProfile.getTabAt(2).setIcon(R.drawable.icon_dishes);
        tabLayoutProfile.getTabAt(3).setIcon(R.drawable.icon_address);

        buttonBack.setOnClickListener(e -> finish());

        imageSettings.setOnClickListener(e -> {
            if (User.getInstance() != null) {
                Intent intent = new Intent(this, ProfileSettingsActivity.class);
                startActivity(intent);
            }
        });

        UserService userService = new UserService(this);
        if (User.getInstance() != null) {
            userService.getOrders(User.getInstance().getId());
            userService.getHistory(User.getInstance().getId(), 1);
            userService.getFavoritesDishes(User.getInstance().getId());
        }
    }

    @Override
    public void onPendingReceived(ArrayList<Order> orders) {
        fragmentOrders.fillPending(orders);
    }

    @Override
    public void onHistoryReceived(ArrayList<Order> orders) {
        fragmentHistory.fillHistory(orders);
    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        fragmentAddress.updateList(addresses);
    }

    @Override
    public void onFavoriteDishesReceived(ArrayList<Dish> dishes) {
        fragmentFavorites.updateList(dishes);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (User.getInstance() == null)
            finish();
    }


    public void refreshOrders() {
        UserService userService = new UserService(this);
        if (User.getInstance() != null) {
            userService.getOrders(User.getInstance().getId());
        }
    }
}

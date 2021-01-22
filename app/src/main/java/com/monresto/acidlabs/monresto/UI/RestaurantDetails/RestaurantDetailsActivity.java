package com.monresto.acidlabs.monresto.UI.RestaurantDetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.material.tabs.TabLayout;
import com.monresto.acidlabs.monresto.BadgeCountChangeListener;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.HackViewPager;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.Menu;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.UI.Cart.CartActivity;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsActivity extends AppCompatActivity implements RestaurantAsyncResponse {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.pager)
    HackViewPager pager;

    @BindView(R.id.storeBg)
    ImageView storeBg;
    @BindView(R.id.storeName)
    TextView storeName;
    @BindView(R.id.storeState)
    TextView storeState;
    @BindView(R.id.cart_frame)
    FrameLayout cart_frame;

    @BindView(R.id.cart_quantity)
    TextView cart_quantity;
    @BindView(R.id.cart_total)
    TextView cart_total;


    RestaurantDetailsPager adapter;
    List<String> MenusList;
    CharSequence[] Titles;

    RestaurantService service;

    HashMap<Menu, ArrayList<Dish>> dishes;

    Restaurant restaurant;
    int filledDishes; // Used for stability and improvements
    BadgeCountChangeListener badgeCountChangeListener;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        try {
            ButterKnife.bind(this);

            dishes = new HashMap<>();

            // Setting up the toolbar
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(v -> finish());


            // Get restaurant information from the caller intent
            Intent i = getIntent();
            restaurant = i.getParcelableExtra("restaurant");

            // Assigning details to views
            Picasso.get().load(restaurant.getBackground()).into(storeBg);
            storeName.setText(restaurant.getName());
            if (restaurant.getState().equals("open"))
                storeState.setText("OUVERT");
            else storeState.setText("FERMÉ");

            filledDishes = 0;

            // Get menus
            service = new RestaurantService(this);
            service.getMenus(restaurant.getId());

            cart_frame.setOnClickListener(e -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            finish();
        }

    }


    void setUpTabs() {
        adapter = new RestaurantDetailsPager(this.getSupportFragmentManager(), Titles, restaurant, dishes);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        tabs.setupWithViewPager(pager);
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        badgeCountChangeListener = () -> {
            MenuItem itemCart = menu.findItem(R.id.cart_btn);
            LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
            Utilities.setBadgeCount(this, icon, String.valueOf(ShoppingCart.getInstance().getCount()));
        };
        badgeCountChangeListener.onBadgeCountChanged();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cart_btn) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onListReceived(ArrayList<Restaurant> restaurantList, int status) {

    }

    @Override
    public void onMenusReceived(ArrayList<Menu> menus) {

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Menu");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, menus.toString());

        Config.logger.logEvent("View Content",
                menus.size(),
                params);

        MenusList = new ArrayList<>();
        MenusList.add("INFORMATIONS");
        counter = menus.size();

        for (int j = 0; j < menus.size(); j++) {
            dishes.put(menus.get(j), null);

            MenusList.add(Utilities.decodeUTF(menus.get(j).getTitle()));
            // Get dishes according to menu
            service.getDishes(restaurant.getId(), menus.get(j));
        }

        Titles = MenusList.toArray(new CharSequence[MenusList.size()]);
        Button button = new Button(this);
        button.setOnClickListener(view -> {

        });
    }

    @Override
    public void onDishesReceived(ArrayList<Dish> dishes, Menu menu) {


        try {
            counter--;
            menu.setTitle(Utilities.decodeUTF(menu.getTitle()));
            menu.setDescription(Utilities.decodeUTF(menu.getDescription()));

            for (int i = 0; i < dishes.size(); i++)
                dishes.get(i).setRestoID(restaurant.getId());

            this.dishes.put(menu, dishes);
            filledDishes++;

            if (counter == 0)
                setUpTabs();
        } catch (Exception e) {

            finish();
        }

    }

    @Override
    public void onComposedDishReceived(Dish dish) {

    }

    @Override
    public void onSpecialitiesReceived(ArrayList<Speciality> specialities) {

    }

    @Override
    public void onServerDown() {

    }

    @Override
    public void onError(VolleyError error) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (badgeCountChangeListener != null)
            badgeCountChangeListener.onBadgeCountChanged();

        updateCartInfo();
    }

    @SuppressLint("SetTextI18n")
    public void updateCartInfo() {
        cart_quantity.setText(String.valueOf(ShoppingCart.getInstance().getItems().size()));
        DecimalFormat dec = new DecimalFormat("#0.00");
        if (restaurant != null) {
            cart_total.setText(dec.format(ShoppingCart.getInstance().getCartSubTotal()) + " DT");
        }
        try {
            if (ShoppingCart.getInstance() != null && ShoppingCart.getInstance().getCartSubTotal() > 0) {
                cart_frame.setVisibility(View.VISIBLE);
            } else {
                cart_frame.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


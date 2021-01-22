package com.monresto.acidlabs.monresto.UI.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.GPSTracker;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.ObjectSerializer;
import com.monresto.acidlabs.monresto.PlacePickerDialog;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Cart.CartActivity;
import com.monresto.acidlabs.monresto.UI.Profile.ProfileActivity;
import com.monresto.acidlabs.monresto.UI.User.LoginActivity;
import com.monresto.acidlabs.monresto.UI.User.SelectAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


//Testing fetch information from api

public class MainActivity extends AppCompatActivity implements RestaurantAsyncResponse, UserAsyncResponse, SwipeRefreshLayout.OnRefreshListener, MaterialSearchBar.OnSearchActionListener {
    @BindView(R.id.home_profile_icon)
    ImageView home_profile_icon;
    @BindView(R.id.home_close)
    ImageView home_close;

    @BindView(R.id.stores_recyclerview)
    RecyclerView stores_recyclerview;
    @BindView(R.id.restaurants_swiper)
    SwipeRefreshLayout restaurants_swiper;
    @BindView(R.id.status_restaurants)
    ConstraintLayout status_restaurants;
    @BindView(R.id.cart_frame)
    FrameLayout cart_frame;
    @BindView(R.id.cart_quantity)
    TextView cart_quantity;
    @BindView(R.id.cart_total)
    TextView cart_total;
    @BindView(R.id.deliveryLabel)
    TextView deliveryLabel;
    @BindView(R.id.change_address_container)
    LinearLayout change_address_container;
    GPSTracker gpsTracker;
    Geocoder geocoder;
    boolean list_init = false;
    private ArrayList<Restaurant> searchList;
    private ArrayList<Speciality> specialities;
    private boolean firstResume;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserService userService;
    private RestaurantService service;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        restaurants_swiper.setOnRefreshListener(this);
        stores_recyclerview.setNestedScrollingEnabled(false);
        firstResume = true;

        init();


    }

    public void init() {
        gpsTracker = new GPSTracker(this);
        service = new RestaurantService(this);
        userService = new UserService(this);
        List<android.location.Address> addresses;
        geocoder = new Geocoder(this);

        service.getDetails(251);

        try {
            if (User.getInstance() != null && User.getInstance().getSelectedAddress() != null)
                deliveryLabel.setText(User.getInstance().getSelectedAddress().getAdresse());
            else {
                addresses = geocoder.getFromLocation(Semsem.getLat(), Semsem.getLon(), 3);
                if (addresses != null && !addresses.isEmpty()) {
                    deliveryLabel.setText(addresses.get(0).getAddressLine(0));
                    if (deliveryLabel.getText().equals(""))
                        deliveryLabel.setText("Adresse inconnue");
                } else
                    deliveryLabel.setText("Adresse inconnue");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (getIntent().getStringExtra("serviceType") != null) {
            service.getRestoByRubrique(Semsem.getLat(), Semsem.getLon(), getIntent().getStringExtra("serviceType"));
//            service.getRestoByRubrique(33.824944f, 8.396075f, getIntent().getStringExtra("serviceType"));
        } else
            service.getAll(Semsem.getLat(), Semsem.getLon());
//            service.getAll(33.82f, 8.39f);
        service.getSpecialities();

        stores_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        change_address_container.setOnClickListener(e -> {
            if (User.getInstance() != null) {
                Intent intent = new Intent(this, SelectAddressActivity.class);
                startActivity(intent);
            }
        });
        home_profile_icon.setOnClickListener(view -> {
            Intent intent;
            if (User.getInstance() == null)
                intent = new Intent(this, LoginActivity.class);
            else
                intent = new Intent(this, ProfileActivity.class);

            startActivity(intent);
        });
        cart_frame.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(this, CartActivity.class);

            startActivity(intent);
        });

        home_close.setOnClickListener(view -> {
            finish();
        });

    }

    @Override
    public void onListReceived(ArrayList<Restaurant> restaurantList, int status) {

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "resto");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, restaurantList.toString());

        Config.logger.logEvent("Rechercher",
                restaurantList.size(),
                params);
        Semsem.getInstance().setRestaurants(restaurantList);
        if (ShoppingCart.getInstance() != null && !ShoppingCart.getInstance().isEmpty())
            updateHomeCart();
        if (ShoppingCart.getInstance() != null && Semsem.getInstance().findRestaurant(ShoppingCart.getInstance().getRestoID()) == null) {
            ShoppingCart.getInstance().clear();
            updateHomeCart();
        }
        if (!restaurantList.isEmpty()) {
            try {
                if (ShoppingCart.getInstance() != null && ShoppingCart.getInstance().getCartSubTotal() > 0) {
                    cart_frame.setVisibility(View.VISIBLE);
                } else {
                    cart_frame.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            restaurants_swiper.setVisibility(View.VISIBLE);
            status_restaurants.setVisibility(View.INVISIBLE);
            populateRecyclerView(restaurantList);

            if (status == 10) {
                Utilities.statusUnavailable(this, "Suite à une forte demande, cette zone est fermée", status_restaurants, restaurants_swiper);
            }

            if (!list_init) {
                //Load saved shopping cart
                SharedPreferences sharedPreferences = getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                String serialItems;
                if (sharedPreferences.contains("items")) {
                    serialItems = sharedPreferences.getString("items", "");
                    ShoppingCart shoppingCart = (ShoppingCart) ObjectSerializer.deserialize(serialItems);

                    if (shoppingCart != null && Semsem.getInstance().findRestaurant(shoppingCart.getRestoID()) != null) {
                        ShoppingCart.setInstance((ShoppingCart) ObjectSerializer.deserialize(serialItems));
                        updateHomeCart();
                    }

                }
                list_init = true;
            }
        } else {
            Utilities.statusChangerUnavailable(this, "Aucun restaurant trouvé", status_restaurants, restaurants_swiper);
        }


        ShowcaseConfig configs = new ShowcaseConfig();
        configs.setDelay(500);
        configs.setMaskColor(Color.BLACK);
        // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "2");

        sequence.setConfig(configs);

        sequence.addSequenceItem(change_address_container,
                "Vous pouvez changer de position rapidement", "SUIVANT");

        if (cart_frame.isShown())
            sequence.addSequenceItem(cart_frame,
                    "Accédez au panier fluidement", "COMPRIS");
        sequence.start();
    }


    @Override
    public void onSpecialitiesReceived(ArrayList<Speciality> specialities) {
        this.specialities = specialities;
        if (recyclerViewAdapter == null)
            recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerViewAdapter.setSpecialities(specialities);
    }

    @Override
    public void onServerDown() {
        Utilities.statusChanger(this, R.layout.fragment_breakdown, status_restaurants, restaurants_swiper);
    }

    @Override
    public void onServerHighDemand() {
        Utilities.statusChanger(this, R.layout.fragment_highdemand, status_restaurants, restaurants_swiper);
    }

    @Override
    public void onNoRestaurantsFound() {
        Utilities.statusChanger(this, R.layout.fragment_unavailable, status_restaurants, restaurants_swiper);
    }

    @Override
    public void onError(VolleyError error) {

    }

    public void populateRecyclerView(ArrayList<Restaurant> restaurantList) {
        if (recyclerViewAdapter == null)
            recyclerViewAdapter = new RecyclerViewAdapter(this, restaurantList);
        else recyclerViewAdapter.setRestaurants(restaurantList);

        stores_recyclerview.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        if (getIntent().getStringExtra("serviceType") != null) {
            service.getRestoByRubrique(Semsem.getLat(), Semsem.getLon(), getIntent().getStringExtra("serviceType"));
        } else
            service.getAll(Semsem.getLat(), Semsem.getLon());
        restaurants_swiper.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        finish();
        /*
        //// SCROLL TO TOP, APPARENTLY NOT LIKED BY EVERYONE...
        LinearLayoutManager layoutManager = (LinearLayoutManager) stores_recyclerview.getLayoutManager();
        assert layoutManager != null;
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            super.onBackPressed();
        } else {
            stores_recyclerview.smoothScrollToPosition(0);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
            Semsem.locationChanged = false;
        } else {
            if (Semsem.locationChanged) {
                Semsem.locationChanged = false;
                Utilities.statusChanger(this, R.layout.fragment_loading, status_restaurants, restaurants_swiper);

                cart_frame.setVisibility(View.INVISIBLE);
                service.getAll(Semsem.getLat(), Semsem.getLon());
                service.getSpecialities();

                List<android.location.Address> addresses;
                geocoder = new Geocoder(this);
                try {
                    if (User.getInstance() != null && User.getInstance().getSelectedAddress() != null)
                        deliveryLabel.setText(User.getInstance().getSelectedAddress().getAdresse());
                    else {
                        addresses = geocoder.getFromLocation(Semsem.getLat(), Semsem.getLon(), 3);
                        if (addresses != null && !addresses.isEmpty()) {
                            deliveryLabel.setText(addresses.get(0).getAddressLine(0));
                            if (deliveryLabel.getText().equals(""))
                                deliveryLabel.setText("Adresse inconnue");
                        } else
                            deliveryLabel.setText("Adresse inconnue");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updateHomeCart();
        }

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled)
            onRefresh();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchList = new ArrayList<>();
        for (Restaurant restaurant : Semsem.getInstance().getRestaurants()) {
            if (restaurant.getName().toLowerCase().contains(text.toString().toLowerCase())) {
                searchList.add(restaurant);
            }
        }
        if (!searchList.isEmpty()) {
            populateRecyclerView(searchList);
            try {
                Utilities.hideKeyboard(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Aucune donnée trouvée", Toast.LENGTH_SHORT).show();
            try {
                Utilities.hideKeyboard(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void searchWithSpeciality(Speciality speciality) {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        for (Restaurant restaurant : Semsem.getInstance().getRestaurants()) {
            for (Speciality currSpeciality : restaurant.getSpecialities()) {
                if (currSpeciality.getTitle().equals(speciality.getTitle()))
                    searchList.add(restaurant);
            }
        }
        populateRecyclerView(searchList);
    }

    public void searchWithFilter(int filter) {
        ArrayList<Restaurant> searchList = new ArrayList<>();
        switch (filter) {
            case Semsem.FILTER_OPEN: {
                for (Restaurant restaurant : Semsem.getInstance().getRestaurants()) {
                    if (restaurant.getState().toLowerCase().equals("open"))
                        searchList.add(restaurant);
                }
            }
            break;
            case Semsem.FILTER_PROMO: {
                for (Restaurant restaurant : Semsem.getInstance().getRestaurants()) {
                    if (restaurant.getWithPromotion() != 0)
                        searchList.add(restaurant);
                }
            }
            break;
            case Semsem.FILTER_NOTE: {
                searchList = Restaurant.sort(Semsem.getInstance().getRestaurants(), (e1, e2) -> e1.getRate() > e2.getRate() ? -1 : 1);
            }
            break;
            case Semsem.FILTER_TIME: {
                searchList = Restaurant.sort(Semsem.getInstance().getRestaurants(), (e1, e2) -> e1.getEstimatedTime() - e2.getEstimatedTime());
            }
            break;
        }
        populateRecyclerView(searchList);
    }

    public void resetRecyclerView() {
        populateRecyclerView(Semsem.getInstance().getRestaurants());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @SuppressLint("SetTextI18n")
    public void updateHomeCart() {
        cart_quantity.setText(String.valueOf(ShoppingCart.getInstance().getItems().size()));

        DecimalFormat dec = new DecimalFormat("#0.00");
        cart_total.setText(dec.format(ShoppingCart.getInstance().getCartSubTotal()) + " DT");
        try {
            if (ShoppingCart.getInstance().getCartSubTotal() == 0) {
                cart_frame.setVisibility(View.GONE);
            } else {
                cart_frame.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void onUserLogin(User user) {
        userService.getDetails(user.getId(), true);
    }

    @Override
    public void onUserDetailsReceived(User user) {
        userService.getAddress(User.getInstance().getId());
    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        if (User.getInstance() != null)
            User.getInstance().setAddresses(addresses);
        Intent intent = new Intent(this, SelectAddressActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.REQUEST_CODE_FILTER_SELECT && data != null) {
                int filter = data.getIntExtra("filter", 0);
                searchWithFilter(filter);
            }
        }
    }
}

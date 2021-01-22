package com.monresto.acidlabs.monresto.UI.Homepage;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.facebook.AccessToken;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.monresto.acidlabs.monresto.Application;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.GPSTracker;
import com.monresto.acidlabs.monresto.InternetCheck;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.HomepageConfig;
import com.monresto.acidlabs.monresto.Model.HomepageDish;
import com.monresto.acidlabs.monresto.Model.HomepageEvent;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.Setting;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.ObjectSerializer;
import com.monresto.acidlabs.monresto.PlacePickerDialog;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Homepage.HomepageAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Homepage.HomepageService;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Cart.CartActivity;
import com.monresto.acidlabs.monresto.UI.Dialog.DialogMonrestoIntroduit;
import com.monresto.acidlabs.monresto.UI.Homepage.Snacks.SnacksActivity;
import com.monresto.acidlabs.monresto.UI.Main.MainActivity;
import com.monresto.acidlabs.monresto.UI.Main.RecyclerViewAdapter;
import com.monresto.acidlabs.monresto.UI.Profile.Address.NewAddressActivity;
import com.monresto.acidlabs.monresto.UI.Profile.ProfileActivity;
import com.monresto.acidlabs.monresto.UI.Profile.Settings.EditProfileActivity;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.RestaurantDetailsActivity;
import com.monresto.acidlabs.monresto.UI.User.LoginActivity;
import com.monresto.acidlabs.monresto.UI.User.SelectAddressActivity;
import com.monresto.acidlabs.monresto.WrappableGridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.requestlist.RequestListActivity;

import static com.monresto.acidlabs.monresto.Config.REQUEST_CODE_ASK_FOR_LOCATION;
import static com.monresto.acidlabs.monresto.Model.Semsem.loginPending;
import static com.monresto.acidlabs.monresto.UI.Maps.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class HomepageActivity extends AppCompatActivity implements UserAsyncResponse, HomepageAsyncResponse, RestaurantAsyncResponse {


    public static HashMap<String, String> settingsMap = new HashMap<>();
    @BindView(R.id.dishesRecycler)
    RecyclerView dishesRecycler;
    @BindView(R.id.eventsRecycler)
    RecyclerView eventsRecycler;
    @BindView(R.id.home_profile_icon)
    ImageView home_profile_icon;
    @BindView(R.id.cart_btn)
    ImageView cart_btn;
    @BindView(R.id.homepage_swiper)
    SwipeRefreshLayout homepage_swiper;
    @BindView(R.id.platsJour)
    TextView platsJour;
    @BindView(R.id.evenements)
    TextView evenements;
    @BindView(R.id.homepageLayout)
    ConstraintLayout homepageLayout;
    HomepageDishesAdapter Dishesadapter;
    HomepageEventsAdapter Eventsadapter;
    HomepageExtrasAdapter Extrasadapter;
    HomepageParapharmacieAdapter parapharmacieAdapter;
    HomepageSupermarcheAdapter supermarcheAdapter;
    GPSTracker gpsTracker;
    UserService userService;
    HomepageService homepageService;
    TextView tvTimeLiv;
    @BindView(R.id.cardViewBg_support)
    CardView cardViewBgSupport;
    @BindView(R.id.rv_home_list)
    RecyclerView rvHomeList;
    @BindView(R.id.rv_home_grid)
    RecyclerView rvHomeGrid;
    private boolean askedMain = false;
    //Request for location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);


        Dishesadapter = new HomepageDishesAdapter(this);
        Eventsadapter = new HomepageEventsAdapter(this);
        Extrasadapter = new HomepageExtrasAdapter(this);
        parapharmacieAdapter = new HomepageParapharmacieAdapter(this);
        supermarcheAdapter = new HomepageSupermarcheAdapter(this);

        userService = new UserService(this);

        homepageService = new HomepageService(this);


        dishesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dishesRecycler.setAdapter(Dishesadapter);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventsRecycler.setAdapter(Eventsadapter);

        homepageService.getAppSettings();
        homepageService.getHome();

        deepLink();


        dishesRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        homepage_swiper.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        homepage_swiper.setEnabled(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });
        eventsRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        homepage_swiper.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        homepage_swiper.setEnabled(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });

        homepageService.getAll();

        home_profile_icon.setOnClickListener(e -> {
            if (User.getInstance() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        homepage_swiper.setOnRefreshListener(() -> {
            homepageService.getHome();
            homepageService.getAll();
        });

        if (checkLocationPermission())
            checkInternet();

        SharedPreferences sharedPreferences = getSharedPreferences("itemsList", Context.MODE_PRIVATE);
        String serialItems;
        if (sharedPreferences.contains("items")) {
            serialItems = sharedPreferences.getString("items", "");
            ShoppingCart shoppingCart = (ShoppingCart) ObjectSerializer.deserialize(serialItems);

            if (shoppingCart != null) {
                ShoppingCart.setInstance((ShoppingCart) ObjectSerializer.deserialize(serialItems));
            }
        }

        cart_btn.setOnClickListener(e -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        try {
            initZendesk();
        } catch (Exception ignored) {

        }
        cardViewBgSupport.setOnClickListener(e -> {

            if (User.getInstance() == null) {
                /*Snackbar.make(homepageLayout, R.string.connect, Snackbar.LENGTH_SHORT)
                        .show();*/
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                String userName = "";
                try {
                    userName = User.getInstance().getLname() + " " + User.getInstance().getFname();
                } catch (Exception ignored) {
                    try {
                        userName = User.getInstance().getLogin();
                    } catch (Exception s) {
                        s.printStackTrace();
                    }
                }
                Identity identity = new AnonymousIdentity.Builder()
                        .withNameIdentifier(userName)
                        .withEmailIdentifier(User.getInstance().getEmail())
                        .build();
                Zendesk.INSTANCE.setIdentity(identity);
                Support.INSTANCE.init(Zendesk.INSTANCE);
                Intent requestActivityIntent = RequestListActivity.builder()
                        .intent(getApplicationContext());
                requestActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(requestActivityIntent);
            }

        });

        rvHomeList.setLayoutManager(new LinearLayoutManager(this));
        rvHomeGrid.setLayoutManager(new WrappableGridLayoutManager(this, 2));
        rvHomeGrid.setNestedScrollingEnabled(false);
        rvHomeList.setNestedScrollingEnabled(false);
    }


    public void checkInternet() {
        new InternetCheck(internet -> {
            if (internet) {
                if (!login())
                    displayLocationSettingsRequest(this);
            }
        });
    }

    public void init() {
        gpsTracker = new GPSTracker(this);
        double lat = gpsTracker.getLatitude();
        double lon = gpsTracker.getLongitude();
        Semsem.setLat(lat);
        Semsem.setLon(lon);
    }

    public boolean login() {
        SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String savedLogin = sharedPref.getString("passwordLogin", null);
        JSONObject loginObj;
        if (savedLogin != null) {
            try {
                loginObj = new JSONObject(savedLogin);
                userService.login(loginObj.optString("login"), loginObj.optString("password"), sharedPref);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            savedLogin = sharedPref.getString("fbLogin", null);
            if (savedLogin != null) {
                try {
                    loginObj = new JSONObject(savedLogin);
                    User.setInstance(new User(loginObj.optInt("id"), loginObj.optString("email"), loginObj.optString("fname"), loginObj.optString("lname"), loginObj.optString("mobile")));
                    if (TextUtils.isEmpty(User.getInstance().getMobile()) || User.getInstance().getMobile().length() != 8) {
                        Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Demande d'autorisation")
                        .setMessage("Monresto a besoin de savoir votre position")
                        .setPositiveButton("Accépter", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(HomepageActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create();
                alertDialog.show();

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkInternet();
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public void onUserLogin(User user) {
        if (user != null)
            userService.getDetails(user.getId(), true);
        else {
            askedMain = false;
            loginPending = false;
        }
    }


    @Override
    public void onUserDetailsReceived(User user) {
        userService.getAddress(User.getInstance().getId());
    }

    @Override
    public void onAddressListReceived(ArrayList<Address> addresses) {
        if (User.getInstance() != null) {

            if (TextUtils.isEmpty(User.getInstance().getMobile()) || User.getInstance().getMobile().length() != 8) {

                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);

            }

            User.getInstance().setAddresses(addresses);
            loginPending = false;
            if (addresses.isEmpty()) {
                Intent intent = new Intent(this, NewAddressActivity.class);
                startActivity(intent);
            } else if (askedMain) {
                Intent intent = new Intent(this, SelectAddressActivity.class);
                startActivityForResult(intent, Config.REQUEST_CODE_ADRESS_SELECT);
            }
        }


    }

    @Override
    public void onHomepageSettingsReceived(ArrayList<Setting> settings) {
        if (settings != null)
            initAllHomeCards(settings);
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        init();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(HomepageActivity.this, REQUEST_CODE_ASK_FOR_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(HomepageActivity.this, "TODO no permission", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_ASK_FOR_LOCATION): {
                if (resultCode == Activity.RESULT_OK) {
                    init();
                }
            }
            break;
            case (Config.REQUEST_CODE_POSITION_SELECT):
            case (Config.REQUEST_CODE_ADRESS_SELECT): {
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("serviceType", data.getStringExtra("serviceType"));
                    startActivity(intent);
                }
            }
            break;
            case Config.REQUEST_PLACE_PICKER: {
                if (data != null) {
                    /*Place place = PlacePicker.getPlace(data, this);
                    Semsem.setLat(place.getLatLng().latitude);
                    Semsem.setLon(place.getLatLng().longitude);*/
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
            break;
        }
    }

    @Override
    public void onHomepageConfigReceived(HomepageConfig config) {


        //Picasso.get().load(config.getCover_image()).into(config_bg);

        ShowcaseConfig configs = new ShowcaseConfig();
        configs.setDelay(500); // half second between each showcase view
        configs.setMaskColor(Color.parseColor("#cc1eb999"));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "100");

        sequence.setConfig(configs);

        /*sequence.addSequenceItem(config_bg,
                "Découvrez les meilleurs restos", "SUIVANT");*/
        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                DialogMonrestoIntroduit dialogMonrestoIntroduit = new DialogMonrestoIntroduit(HomepageActivity.this);
                dialogMonrestoIntroduit.setCancelable(true);
                dialogMonrestoIntroduit.show();
            }
        });
        //sequence.addSequenceItem(extrasRecycler,"Mais aussi deux autres services", "COMPRIS");


        HomepageConfig.setInstance(config);
        homepage_swiper.setRefreshing(false);
        HomepageConfig.getInstance().getBusket_image();
        ArrayList<String> images = new ArrayList<>();
        images.add(config.getSnack_image());
        images.add(config.getDelivery_image());

        ArrayList<String> imagesParapharmacie = new ArrayList<>();
        imagesParapharmacie.add(config.getSnack_image());

        ArrayList<String> imagesSupermarche = new ArrayList<>();
        imagesSupermarche.add(config.getSnack_image());
        imagesSupermarche.add(config.getSnack_image());

        Extrasadapter.setImages(images);
        Extrasadapter.notifyDataSetChanged();

        parapharmacieAdapter.setImages(imagesParapharmacie);
        parapharmacieAdapter.notifyDataSetChanged();

        supermarcheAdapter.setImages(imagesSupermarche);
        supermarcheAdapter.notifyDataSetChanged();


        cardViewBgSupport.setVisibility(View.VISIBLE);

        sequence.start();


    }

    @Override
    public void onHomepageEventsReceived(ArrayList<HomepageEvent> events) {
        Eventsadapter.setEvents(events);
        Eventsadapter.notifyDataSetChanged();
        homepage_swiper.setRefreshing(false);
        if (events.isEmpty()) {
            evenements.setVisibility(View.GONE);
            eventsRecycler.setVisibility(View.GONE);
        } else {
            evenements.setVisibility(View.VISIBLE);
            eventsRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHomepageDishesReceived(ArrayList<HomepageDish> dishes) {
        Dishesadapter.setDishes(dishes);
        Dishesadapter.notifyDataSetChanged();
        homepage_swiper.setRefreshing(false);
        if (dishes.isEmpty()) {
            platsJour.setVisibility(View.GONE);
            dishesRecycler.setVisibility(View.GONE);
        } else {
            platsJour.setVisibility(View.VISIBLE);
            dishesRecycler.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onHomepageError(boolean b) {
        if (b) {
            homepage_swiper.setRefreshing(false);
        }

    }

    @Override
    public void onHomeLoaded(ArrayList<HomeItem> listHomeElements, ArrayList<HomeItem> gridHomeElements) {
        HomeListAdapter homeListAdapter = new HomeListAdapter(this, listHomeElements);
        rvHomeList.setAdapter(homeListAdapter);


        HomeGridAdapter homeGridAdapter = new HomeGridAdapter(this, gridHomeElements);
        rvHomeGrid.setAdapter(homeGridAdapter);
        rvHomeGrid.setLayoutManager(new WrappableGridLayoutManager(this, 2));
    }


    /**
     * initialize all home categories actions : supermarche, coursier, parapharmacie ...
     */
    private void initAllHomeCards(ArrayList<Setting> settings) {

        settingsMap = new HashMap<>();

        for (int i = 0; i < settings.size(); i++) {
            Setting currentSetting = settings.get(i);
            String label = settings.get(i).getLabel();
            switch (label) {
                case "text_restos":
                    settingsMap.put("resto", currentSetting.getValeur());
                    break;
                case "text_supermarches":
                    settingsMap.put("superm", currentSetting.getValeur());
                    break;
                case "text_jibly":
                    settingsMap.put("jibly", currentSetting.getValeur());
                    break;
                case "text_coursier":
                    settingsMap.put("coursier", currentSetting.getValeur());
                    break;
                case "text_traiteur":
                    settingsMap.put("traiteur", currentSetting.getValeur());
                    break;
                case "text_parapharmacie":
                    settingsMap.put("parapharma", currentSetting.getValeur());
                    break;
                case "notif_price_1":
                    settingsMap.put("notif_price_1", currentSetting.getValeur());
                    break;
                case "text_delivery_time":
                    settingsMap.put("text_delivery_time", currentSetting.getValeur());
                    break;

                default:
                    break;
            }
        }

        // set all cards descriptions from the returned list from ws
//        tvTimeLiv.setText(settingsMap.get("text_delivery_time"));


    }

    private void deepLink() {
        /*FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        String idCat;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            idCat = pendingDynamicLinkData.getLink().getQueryParameter("catId");
                            if (!TextUtils.isEmpty(idCat)) {
                                Intent intent = new Intent(HomepageActivity.this, SnacksActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", Integer.valueOf(idCat));
                                startActivity(intent);
                            }
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "getDynamicLink:onFailure", e);
                    }
                });*/
    }

    private void initZendesk() {
        Zendesk.INSTANCE.init(this, "https://monrestohelp.zendesk.com",
                "168961d33f0a3232b57c58606e65ed0efcd49652f9660290",
                "mobile_sdk_client_457abd155e44c1cf7cfa");
        Support.INSTANCE.init(Zendesk.INSTANCE);
    }

    @Override
    public void onDetailsReceived(Restaurant restaurant) {
        if (!ShoppingCart.getInstance().isEmpty() && ShoppingCart.getInstance().getRestoID() != restaurant.getId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vider le panier et changer de restaurant ?");

            builder.setPositiveButton("OK", (dialog, which) -> {
                ShoppingCart.getInstance().clear();
                SharedPreferences sharedPreferences = getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);

            });
            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(arg0 -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#33b998"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#33b998"));
            });
            dialog.show();
        } else {
            Intent intent = new Intent(this, RestaurantDetailsActivity.class);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
        }
    }

    @Override
    public void onError(VolleyError error) {

    }
}

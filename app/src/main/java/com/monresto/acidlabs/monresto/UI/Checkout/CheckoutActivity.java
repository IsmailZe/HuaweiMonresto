package com.monresto.acidlabs.monresto.UI.Checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;
import com.facebook.appevents.AppEventsConstants;
import com.monresto.acidlabs.monresto.BuildConfig;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.PreferenceManager;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.RadioListAdapter;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.UI.Dialog.DialogMonRestoPay;
import com.monresto.acidlabs.monresto.UI.Profile.ProfileActivity;
import com.monresto.acidlabs.monresto.UI.Restaurants.ViewPagerAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckoutActivity extends AppCompatActivity implements UserAsyncResponse,
        RestaurantAsyncResponse {

    private final int TYPE_PAYMENT_MODE = 1, TYPE_UNAVAILABLE_OPTION = 2, TYPE_DELIVERY_DATE = 3;
    @BindView(R.id.recycler_cerdit_card_delivery_options)
    RecyclerView recyclerCreditCardDeliveryOptions;
    @BindView(R.id.layout_credit_card_delivery_options)
    LinearLayout layoutCreditCardDeliveryOptions;
    @BindView(R.id.imageProfileBack)
    ImageView imageProfileBack;
    @BindView(R.id.cart_delivery)
    TextView cart_delivery;
    @BindView(R.id.cart_total)
    TextView cart_total;
    @BindView(R.id.orderBtn)
    ConstraintLayout orderBtn;
    @BindView(R.id.orderLoading)
    ActionProcessButton orderLoading;
    @BindView(R.id.livraison)
    RadioButton livraison;
    @BindView(R.id.emporter)
    RadioButton emporter;
    @BindView(R.id.address)
    TextView textAddress;
    @BindView(R.id.paymentMethods)
    RecyclerView paymentMethods;
    @BindView(R.id.paymentItemUnavailable)
    RecyclerView paymentItemUnavailable;
    @BindView(R.id.deliveryDate)
    RecyclerView deliveryDate;
    @BindView(R.id.timePicker)
    TimePicker timePicker;
    @BindView(R.id.tv_title_time_picker)
    TextView tvTitleTimePicker;
    @BindView(R.id.linearLayout11)
    LinearLayout addressLayout;
    @BindView(R.id.checkout_scroll_view)
    NestedScrollView checkout_scroll_view;
    @BindView(R.id.verify_btn)
    Button verify_btn;
    @BindView(R.id.promo_text)
    TextView promo_text;
    @BindView(R.id.textView29)
    TextView jibly_text;
    @BindView(R.id.tv_semsem_pay)
    TextView tvSemsemPay;
    @BindView(R.id.iv_banner_semsem_pay)
    ImageView ivBannerSemsemPay;
    @BindView(R.id.root_view)
    View rootView;
    RadioListAdapter payementRadioListAdapter;
    private Address address;
    private int type = 0;
    private String hour = "";
    private UserService userService;
    private ViewPagerAdapter adapter;
    private String deliveryStart = "09:00";
    private String deliveryEnd = "22:00";
    private Restaurant restaurant;
    private Date deliveryStartDate;
    private Date deliveryEndDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);

        //init timePicker format 24h
        timePicker.setIs24HourView(true);

        //Declare
        userService = new UserService(this);

        //Init
        orderLoading.setMode(ActionProcessButton.Mode.ENDLESS);
        orderLoading.setProgress(0);

        DecimalFormat dec = new DecimalFormat("#0.00");
        cart_delivery.setText(getIntent().getExtras().getDouble("delivery") + " DT");
        cart_total.setText(dec.format(getIntent().getExtras().getDouble("total")) + " DT");

        if (User.getInstance() != null && User.getInstance().getSelectedAddress() != null) {
            address = User.getInstance().getSelectedAddress();
            if (address != null) {
                textAddress.setText(address.getAdresse());
            }
        }

        initRecyclerViews();


        //Event handlers
        imageProfileBack.setOnClickListener(e -> {

            if (ShoppingCart.getInstance().getRestoID() == 370)
                ShoppingCart.getInstance().clear();

            finish();
        });

        livraison.setOnCheckedChangeListener((e1, e2) -> {
            if (e1.isChecked()) {
                addressLayout.setVisibility(View.VISIBLE);
                cart_delivery.setText(getIntent().getExtras().getDouble("delivery") + " DT");
                cart_total.setText(dec.format(getIntent().getExtras().getDouble("total")) + " DT");
                type = 0;
                payementRadioListAdapter.setText(0, "Paiement à la livraison");
            } else {
                addressLayout.setVisibility(View.GONE);
                cart_delivery.setText("0 DT");
                double deliveryCost = getIntent().getExtras().getDouble("delivery");
                cart_total.setText(dec.format(getIntent().getExtras().getDouble("total") - deliveryCost) + " DT");
                type = 1;
                payementRadioListAdapter.setText(0, "À emporter");
            }
        });


        orderLoading.setOnClickListener(e -> {
            int paymentMethod = getItemIdByType(paymentMethods, TYPE_PAYMENT_MODE);
            int orderOptionID = getItemIdByType(paymentItemUnavailable, TYPE_UNAVAILABLE_OPTION);
            int deliveryTime = getItemIdByType(deliveryDate, TYPE_DELIVERY_DATE);
            if (deliveryTime > 1) {

                String hh = timePicker.getCurrentHour() < 12 ? "0" : "" + timePicker.getCurrentHour();
                String mm = timePicker.getCurrentMinute() < 10 ? "0" : "" + timePicker.getCurrentMinute();
                hour = hh + mm;
            }


            if (paymentMethod == 1)
                paymentMethod++;
            else if (paymentMethod == 2)
                paymentMethod = 1;

            if (User.getInstance() != null)
                try {
                    String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                    if (!restaurant.isOpen(time)) {
                        Toast.makeText(this, "Le restaurant est fermé à cette heure", Toast.LENGTH_SHORT).show();
                    } else {
                        String promo = promo_text.getText().toString();
                        orderLoading.setEnabled(false);
                        orderLoading.setProgress(1);
                        userService.submitOrders(User.getInstance().getId(), 0, User.getInstance().getSelectedAddress().getId(), ShoppingCart.getInstance().getCurrentRestaurantID(), promo, paymentMethod, orderOptionID, deliveryTime, hour, ShoppingCart.getInstance().getOrdersJson());
                    }
                } catch (Exception ex) {
                }
        });

        if (ShoppingCart.getInstance().getRestoID() == 251) {

            jibly_text.setVisibility(View.VISIBLE);
        } else {
            jibly_text.setVisibility(View.GONE);
        }

        RestaurantService restaurantService = new RestaurantService(this);
        restaurantService.getDetails(ShoppingCart.getInstance().getCurrentRestaurantID());
        verify_btn.setOnClickListener(e -> restaurantService.verifyPromo(promo_text.getText().toString()));

        /*try {
            if (PreferenceManager.isFirstSemsemPayTuto(this))
                displayPayInfo();
        } catch (Exception ignored) {

        }*/
    }

    private void displayPayInfo() {
        DialogMonRestoPay dialogMonrestoInfo = new DialogMonRestoPay(this);

        dialogMonrestoInfo.show();
        PreferenceManager.saveFirstSemsemPayTuto(this);
    }

    void initRecyclerViews() {
        RadioListAdapter radioListAdapter;
        CharSequence[] subjects;
        CharSequence[] descriptions;


        //1
        subjects = getResources().getStringArray(R.array.payment_methods);
        descriptions = getResources().getStringArray(R.array.payment_methods_description);

        payementRadioListAdapter = new RadioListAdapter(new ArrayList<>(Arrays.asList(subjects)), new ArrayList<>(Arrays.asList(descriptions)), this, 0);

        if (ShoppingCart.getInstance().getRestoID() == 370) {
            payementRadioListAdapter = new RadioListAdapter(new ArrayList<>(Arrays.asList(subjects)), new ArrayList<>(Arrays.asList(descriptions)), this, 0, 0);
        }

        /*radioListAdapter.setOnActionPerformed(object -> {
            if (object == 0) {
                tvSemsemPay.setVisibility(View.VISIBLE);
                ivBannerSemsemPay.setVisibility(View.VISIBLE);
            } else {
                tvSemsemPay.setVisibility(View.GONE);
                ivBannerSemsemPay.setVisibility(View.GONE);
            }
        });*/

        ivBannerSemsemPay.setOnClickListener(view -> startActivity(new Intent(CheckoutActivity.this, SemsemPayTutotActivity.class)));
        paymentMethods.setLayoutManager(new LinearLayoutManager(this));
        paymentMethods.setAdapter(payementRadioListAdapter);

        //2
        subjects = getResources().getStringArray(R.array.purchase_unavailable_options);
        radioListAdapter = new RadioListAdapter(new ArrayList<CharSequence>(Arrays.asList(subjects)), this, 0);
        paymentItemUnavailable.setLayoutManager(new LinearLayoutManager(this));
        paymentItemUnavailable.setAdapter(radioListAdapter);


        // setting recycler of credit card delivery options
        /*subjects = getResources().getStringArray(R.array.delivery_credit_card_options);
        radioListAdapter = new RadioListAdapter(new ArrayList<CharSequence>(Arrays.asList(subjects)), this, 0);
        recyclerCreditCardDeliveryOptions.setLayoutManager(new LinearLayoutManager(this));
        recyclerCreditCardDeliveryOptions.setAdapter(radioListAdapter);*/

    }


    int getItemIdByType(RecyclerView view, int type) {
        int option = ((RadioListAdapter) (Objects.requireNonNull(view.getAdapter()))).getSelectedItem();
        CharSequence optionTitle = ((RadioListAdapter) (Objects.requireNonNull(view.getAdapter()))).getSelectedOption();
        switch (type) {
            case TYPE_PAYMENT_MODE:
                return option;
            case TYPE_UNAVAILABLE_OPTION:
                return option;
            case TYPE_DELIVERY_DATE:
                if (optionTitle.equals("Plus tard"))
                    return 2;
                if (optionTitle.equals("Demain"))
                    return 3;
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //address = User.getInstance().getSelectedAddress();
        //textAddress.setText(address.getAdresse());
    }

    @Override
    public void onDetailsReceived(Restaurant restaurant) {
        this.restaurant = restaurant;
        initDeliveryTime();
        initializeCheckoutTime(restaurant);
    }

    private void initDeliveryTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            deliveryStart = restaurant.getBeginMorning();
            deliveryEnd = restaurant.getClosingTime();

            deliveryStartDate = simpleDateFormat.parse(deliveryStart);
            deliveryEndDate = simpleDateFormat.parse(deliveryEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initializeCheckoutTime(Restaurant restaurant) {
        RadioListAdapter radioListAdapter;
        CharSequence[] subjects;
        CharSequence[] descriptions;

        //3
        subjects = getResources().getStringArray(R.array.delivery_time_options);
        List<CharSequence> timeList = new LinkedList<>(Arrays.asList(subjects));
        int hour = Calendar.getInstance().getTime().getHours();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date currentTime = Calendar.getInstance().getTime();
        try {
            currentTime = simpleDateFormat.parse(simpleDateFormat.format(currentTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if (!restaurant.isOpen(simpleDateFormat.format(currentTime))) {
                if (currentTime.after(simpleDateFormat.parse(restaurant.getEndMorning()))) {
                    timePicker.setCurrentHour(Integer.parseInt(restaurant.getBeginNight().substring(0, 2)));
                    timePicker.setCurrentMinute(Integer.parseInt(restaurant.getBeginNight().substring(3, 5)));
                } else if (currentTime.after(simpleDateFormat.parse(restaurant.getEndNight()))) {
                    timePicker.setCurrentHour(Integer.parseInt(restaurant.getBeginMorning().substring(0, 2)));
                    timePicker.setCurrentMinute(Integer.parseInt(restaurant.getBeginMorning().substring(3, 5)));
                }
            }
        } catch (Exception e) {
            //do nothing
        }

        try {
            if (!restaurant.getState().toLowerCase().equals("open") || currentTime.after(deliveryEndDate)) {
                timeList.remove(0);
                timeList.remove(0);
                timePicker.setVisibility(View.VISIBLE);
                tvTitleTimePicker.setVisibility(View.VISIBLE);
            } else if (currentTime.before(deliveryStartDate) || !restaurant.isOpen(simpleDateFormat.format(currentTime))) {
                timeList.remove(0);
                timePicker.setVisibility(View.VISIBLE);
                tvTitleTimePicker.setVisibility(View.VISIBLE);
                //checkout_scroll_view.postDelayed(() -> checkout_scroll_view.fullScroll(ScrollView.FOCUS_DOWN),100);
            }
        } catch (Exception e) {
            //do nothing
        }
        radioListAdapter = new RadioListAdapter(new ArrayList<CharSequence>(timeList), this, 0);
        deliveryDate.setLayoutManager(new LinearLayoutManager(this));
        deliveryDate.setAdapter(radioListAdapter);
        try {
            tvTitleTimePicker.setText(((RadioListAdapter) deliveryDate.getAdapter()).getSelectedLabel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deliveryDate.getAdapter() != null) {
            deliveryDate.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if ((((RadioListAdapter) deliveryDate.getAdapter()).getSelectedLabel().equals("Demain") || ((RadioListAdapter) deliveryDate.getAdapter()).getSelectedLabel().equals("Plus tard"))) {
                        timePicker.setVisibility(View.VISIBLE);
                        tvTitleTimePicker.setVisibility(View.VISIBLE);
                        tvTitleTimePicker.setText(((RadioListAdapter) deliveryDate.getAdapter()).getSelectedLabel());
                        checkout_scroll_view.postDelayed(() -> checkout_scroll_view.fullScroll(ScrollView.FOCUS_DOWN), 100);
                    } else {
                        timePicker.setVisibility(View.GONE);
                        tvTitleTimePicker.setVisibility(View.GONE);
                    }
                }
            });

            timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {

                String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

                if (getItemIdByType(deliveryDate, TYPE_DELIVERY_DATE) == 2) {
                    int cur_hour = Calendar.getInstance().getTime().getHours();
                    int cur_min = Calendar.getInstance().getTime().getMinutes();
                    if (timePicker.getCurrentHour() == cur_hour) {
                        if (timePicker.getCurrentMinute() < cur_min)
                            timePicker.setCurrentMinute(cur_min);
                    } else if (timePicker.getCurrentHour() < cur_hour) {
                        timePicker.setCurrentHour(cur_hour);
                        timePicker.setCurrentMinute(cur_min);
                    }

                    try {
                        if (timePicker.getCurrentHour() < Integer.parseInt(deliveryStart.substring(0, 2))) {
                            timePicker.setCurrentHour(Integer.parseInt(deliveryStart.substring(0, 2)));
                            timePicker.setCurrentMinute(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (timePicker.getCurrentHour() > Integer.parseInt(deliveryEnd.substring(0, 2))) {
                            timePicker.setCurrentHour(Integer.parseInt(deliveryEnd.substring(0, 2)));
                            timePicker.setCurrentMinute(0);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }


                if (!restaurant.isOpen(time)) {
                    Toast.makeText(this, "Le restaurant est fermé a cette heure", Toast.LENGTH_SHORT).show();
                }

            });

        }
    }

    @Override
    public void onSubmitOrder(boolean success) {
        if (success) {

            Bundle params = new Bundle();

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, ShoppingCart.getInstance().getOrdersJson().toString());

            Config.logger.logEvent("Achat",
                    1,
                    params);

            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            ShoppingCart.getInstance().clear();
            SharedPreferences sharedPreferences = getSharedPreferences("itemsList", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            finish();
        } else {
            orderLoading.setProgress(-1);
        }
    }

    @Override
    public void onSubmitOrder(boolean success, int orderID) {
        if (success) {

            Bundle params = new Bundle();

            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, ShoppingCart.getInstance().getOrdersJson().toString());

            Config.logger.logEvent("Ajouter des infos de paiement",
                    1,
                    params);

            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("orderID", orderID);
            startActivityForResult(intent, Config.REQUEST_CODE_PAYMENT);
            ShoppingCart.getInstance().clear();
            finish();
        } else {
            orderLoading.setProgress(-1);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_CODE_PAYMENT:
                if (resultCode == Activity.RESULT_OK) {
                }
        }
    }*/

    @Override
    public void onPromoResponse() {

    }

    @Override
    public void onError(VolleyError error) {

    }

    @OnClick(R.id.btn_share)
    void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Faites profiter vos proches : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Partage"));
    }

}

package com.monresto.acidlabs.monresto.UI.Homepage.Snacks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.monresto.acidlabs.monresto.CategoryIds;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.Menu;
import com.monresto.acidlabs.monresto.Model.PopupInfo;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.UI.Cart.CartActivity;
import com.monresto.acidlabs.monresto.UI.Dialog.DialogMonrestoInfo;
import com.monresto.acidlabs.monresto.UI.Homepage.HomepageActivity;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order.OrderActivity;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SnacksActivity extends AppCompatActivity
        implements RestaurantAsyncResponse,
        SnacksAdapter.SnacksAdapterListener,
        AdapterView.OnItemSelectedListener {


    @BindView(R.id.layout_price_warning)
    RelativeLayout layoutPriceWarning;
    @BindView(R.id.snacksLayout)
    LinearLayout snacksLayout;
    @BindView(R.id.loader)
    ConstraintLayout loader;
    @BindView(R.id.storeBg)
    ImageView storeBg;
    @BindView(R.id.storeName)
    TextView storeName;
    @BindView(R.id.cart_quantity)
    TextView cart_quantity;
    @BindView(R.id.cart_total)
    TextView cart_total;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cart_frame)
    FrameLayout cart_frame;
    @BindView(R.id.warning_txt)
    TextView warningTxt;
    @BindView(R.id.storeState)
    TextView storeState;
    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout collapseToolbar;
    @BindView(R.id.iv_warning)
    ImageView ivWarning;
    @BindView(R.id.categories_spinner)
    Spinner categoriesSpinner;
    @BindView(R.id.loading_msg)
    TextView loadingMsg;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.textView13)
    TextView textView13;
    @BindView(R.id.delivery_holder2)
    ImageView deliveryHolder2;

    private RestaurantService restaurantService;
    private int counter = 0;
    private int idResto;
    private double deliveryCost = 0;
    private double minPrice = 0;
    private ArrayList<SnacksAdapter> snacksAdapterArrayList = new ArrayList<>();
    private int selectedSnackId;
    private ArrayList<ConstraintLayout> containerArrayList = new ArrayList<>();
    private ArrayList<String> categoriesList;
    private ArrayList<String> menuListFromDishes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_snacks);
        ButterKnife.bind(this);

        Utilities.changeStatusBarColors(this, R.color.green2);

        // Setting up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(v -> finish());

        categoriesSpinner.setOnItemSelectedListener(this);

        idResto = getIntent().getIntExtra("EXTRA_SESSION_ID", 0);


        categoriesSpinner.setVisibility(idResto == CategoryIds.SUPERMARCHE ? View.VISIBLE : View.GONE);


        restaurantService = new RestaurantService(this);
        restaurantService.getMenus(idResto);
        restaurantService.getDetails(idResto);

        if (!TextUtils.isEmpty(HomepageActivity.settingsMap.get("notif_price_1"))) {
            warningTxt.setText(HomepageActivity.settingsMap.get("notif_price_1"));
        } else {
            warningTxt.setText(getResources().getString(R.string.price_verification_warning_txt));
        }

        //Picasso.get().load(HomepageConfig.getInstance().getBusket_image()).into(couffin);
        cart_frame.setOnClickListener(e -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("DELIVERY_COST", deliveryCost);
            intent.putExtra("MIN_PRICE", minPrice);
            startActivityForResult(intent, 777);
        });
    }


    @Override
    public void onDetailsReceived(Restaurant restaurant) {

        deliveryCost = restaurant.getDeliveryCost();
        ShoppingCart.getInstance().setDeliveryCost(restaurant.getDeliveryCost());
        minPrice = restaurant.getMinimalPrice();
        Picasso.get().load(restaurant.getBackground()).into(storeBg);
        storeName.setText(restaurant.getName());

        if (restaurant.getId() == CategoryIds.JIBLY ||
                restaurant.getId() == CategoryIds.PARAPHARMACIE ||
                restaurant.getId() == CategoryIds.TRAITEUR ||
                restaurant.getId() == CategoryIds.SUPERMARCHE ||
                restaurant.getId() == CategoryIds.PACK) {
            layoutPriceWarning.setVisibility(View.VISIBLE);
        } else {
            layoutPriceWarning.setVisibility(View.GONE);
        }
        try {
            if (restaurant.getId() == CategoryIds.SUPERMARCHE && PopupInfo.getInstance() != null && !PopupInfo.getInstance().isShowed()) {
                PopupInfo popupInfo = new PopupInfo();
                popupInfo.setShowed(true);
                PopupInfo.setInstance(popupInfo);
                DialogMonrestoInfo dialogMonrestoInfo = new DialogMonrestoInfo(SnacksActivity.this);
                dialogMonrestoInfo.setCancelable(false);
                dialogMonrestoInfo.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateCartInfo();
    }

    @Override
    public void onListReceived(ArrayList<Restaurant> restaurantList, int status) {
    }

    @Override
    public void onMenusReceived(ArrayList<Menu> menus) {
        counter = menus.size();


        if (counter > 0) {
            if (this.idResto == CategoryIds.SUPERMARCHE) {
                categoriesList = getMenusName(menus);
                ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                        android.R.layout.simple_spinner_item,
                        categoriesList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoriesSpinner.setAdapter(arrayAdapter);
            }
            for (int i = 0; i < menus.size(); i++) {
                restaurantService.getDishes(idResto, menus.get(i));
            }
        } else {
            loader.setVisibility(View.GONE);
            Utilities.showInfoDialog(this, "", "La liste est vide", true, "OK", null);
        }
    }

    /**
     * get a list of menus names from received menus list
     *
     * @param menus
     * @return
     */
    private ArrayList<String> getMenusName(ArrayList<Menu> menus) {
        ArrayList<String> filtredMenus = new ArrayList<>();
        filtredMenus.add("Sélectionner un filtre");
        for (Menu menu : menus
        ) {
            filtredMenus.add(menu.getTitle());
        }

        return filtredMenus;
    }

    @Override
    public void onDishesReceived(ArrayList<Dish> dishes, Menu menu) {
        menuListFromDishes.add(menu.getTitle());
        ArrayList<Dish> dishes2 = new ArrayList<Dish>();
        for (Dish d : dishes) {
            d.setRestoID(idResto);
            dishes2.add(d);

        }

        dishes.clear();
        dishes = dishes2;
        counter--;
        ConstraintLayout container = (ConstraintLayout) View.inflate(this, R.layout.fragment_snacks, null);
        TextView snacksTitle = container.findViewById(R.id.snacksTitle);
        snacksTitle.setText(Utilities.decodeUTF(menu.getTitle()));
        RecyclerView snacksRecycler = container.findViewById(R.id.snacksRecycler);
        if (dishes.isEmpty()) {
            snacksTitle.setVisibility(View.GONE);
            snacksRecycler.setVisibility(View.GONE);
        }
        SnacksAdapter snacksAdapter = new SnacksAdapter(this);
        snacksRecycler.setAdapter(snacksAdapter);
        snacksAdapter.setSnacks(dishes);
        snacksAdapter.notifyDataSetChanged();


        snacksAdapterArrayList.add(snacksAdapter);

        snacksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loader.setVisibility(View.GONE);
        snacksLayout.addView(container);
        containerArrayList.add(container);
        if (counter == 0) {
            ConstraintLayout footer = (ConstraintLayout) View.inflate(this, R.layout.item_footer, null);
            snacksLayout.addView(footer);
        }
    }

    @Override
    public void onError(VolleyError error) {
        loader.setVisibility(View.GONE);
        //Utilities.showInfoDialog(this, "", "", true, "OK", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartInfo();
    }

    public void updateCartInfo() {

        DecimalFormat dec = new DecimalFormat("#0.00");
        try {
            cart_total.setText(dec.format(ShoppingCart.getInstance().getCartSubTotal()) + " DT");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int quantity = ShoppingCart.getInstance().getItems().size();
        JSONArray jsonArray = new JSONArray();
        jsonArray = ShoppingCart.getInstance().getOrdersJson();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getInt("quantity") == 0) {
                    quantity--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cart_quantity.setText(String.valueOf(quantity));
        try {
            if (quantity > 0) {
                cart_frame.setVisibility(View.VISIBLE);
            } else {
                cart_frame.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRestaurant(Dish dish) {

        ArrayList<Dish.Option> options = new ArrayList<>();
        options.add(new Dish.Option(dish.getId(), dish.getTitle(), 0));

        ArrayList<Dish.Component> components = new ArrayList<>();

        ShoppingCart.getInstance().addToCart(dish, 1, null, components, "");
        updateCartInfo();
    }

    @Override
    public void deleteRestaurant(Dish dish) {
        ArrayList<Dish.Option> options = new ArrayList<>();
        options.add(new Dish.Option(dish.getId(), dish.getTitle(), 0));

        ArrayList<Dish.Component> components = new ArrayList<>();

        ShoppingCart.getInstance().addToCart(dish, -1, null, components, "");
        updateCartInfo();

    }

    @Override
    public void displaySnackDetails(Dish dish) {
        selectedSnackId = dish.getId();
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("dish", (Parcelable) dish);
        //intent.putExtra("snackQuatity", Integer.parseInt(viewHolder.tvQuantity.getText().toString()));
        startActivityForResult(intent, 555);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    ArrayList<Integer> snackIdList = data.getIntegerArrayListExtra("SNACK_ID");
                    HashMap<Integer, Integer> idQuantityMap = (HashMap<Integer, Integer>) data.getSerializableExtra("QUANTITY");

                    if (idQuantityMap != null && idQuantityMap.size() > 0) {

                        for (SnacksAdapter snackAdapter : snacksAdapterArrayList) {

                            for (Dish dish : snackAdapter.getSnacks()) {

                                for (Map.Entry<Integer, Integer> entry : idQuantityMap.entrySet()) {
                                    System.out.println(entry.getKey() + "/" + entry.getValue());
                                    if (entry.getKey() == dish.getId()) {
                                        snackAdapter.setSnacksListQuantity(new ArrayList<>(idQuantityMap.keySet()), new ArrayList<>(idQuantityMap.values()));
                                    }
                                }


                            }
                        }

                    } else if (snackIdList != null && snackIdList.size() > 0) {
                        for (SnacksAdapter snackAdapter : snacksAdapterArrayList) {

                            for (Dish dish : snackAdapter.getSnacks()) {
                                for (Integer id : snackIdList) {
                                    if (dish.getId() == id) {
                                        snackAdapter.setSnacksListQuantity(snackIdList);
                                    }
                                }

                            }
                        }

                    }
                }
            }
        } else if (requestCode == 555) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    int addedQuantity = data.getIntExtra("ADDED_QUANTITY", 0);
                    for (SnacksAdapter snackAdapter : snacksAdapterArrayList) {

                        for (Dish dish : snackAdapter.getSnacks()) {

                            if (dish.getId() == selectedSnackId) {
                                snackAdapter.incrementSnackQuantity(selectedSnackId, addedQuantity);
                            }
                        }


                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        for (int j = 0; j < menuListFromDishes.size(); j++) {

            if (i > 0) {
                if (menuListFromDishes.get(j).equals(categoriesList.get(i))) {
                    containerArrayList.get(j).setVisibility(View.VISIBLE);
                } else {
                    containerArrayList.get(j).setVisibility(View.GONE);
                }
            } else {
                containerArrayList.get(j).setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

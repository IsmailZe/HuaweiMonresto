package com.monresto.acidlabs.monresto.UI.Cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.HomepageConfig;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.Checkout.CheckoutActivity;
import com.monresto.acidlabs.monresto.UI.Profile.Address.NewAddressActivity;
import com.monresto.acidlabs.monresto.UI.User.LoginActivity;
import com.monresto.acidlabs.monresto.UI.User.SelectAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements CartItemRecyclerViewAdapter.CartItemListener {
    @BindView(R.id.cart_delivery)
    TextView cart_delivery;
    @BindView(R.id.cart_total)
    TextView cart_total;
    @BindView(R.id.back_button)
    ImageView back_button;
    @BindView(R.id.iv_arrow_back)
    ImageView ivArrowBack;
    @BindView(R.id.cart_items_list)
    RecyclerView cart_items_list;
    @BindView(R.id.cart_empty)
    ConstraintLayout cart_empty;
    @BindView(R.id.layout_order_info)
    View layoutOrderInfo;
    @BindView(R.id.orderBtn)
    LinearLayout orderBtn;

    private CartItemRecyclerViewAdapter cartItemAdapter;
    private double deliveryCost = 0;
    private double minPrice = 0;
    private int snackId;
    private ArrayList<Integer> snackIdList = new ArrayList<>();
    private HashMap<Integer, Integer> idQuantityMap = new HashMap<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ButterKnife.bind(this);

        //snackIdList = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            deliveryCost = getIntent().getExtras().getDouble("DELIVERY_COST");
            minPrice = getIntent().getExtras().getDouble("MIN_PRICE");
        }
        if (deliveryCost == 0) {
            deliveryCost = ShoppingCart.getInstance().getCartDelivery();
        }
        if (minPrice == 0) {
            minPrice = ShoppingCart.getInstance().getMinCartTotal();
        }

        back_button.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SNACK_ID", snackIdList);
            resultIntent.putExtra("QUANTITY", idQuantityMap);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        ivArrowBack.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SNACK_ID", snackIdList);
            resultIntent.putExtra("QUANTITY", idQuantityMap);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        cart_items_list.setLayoutManager(new LinearLayoutManager(this));
        cartItemAdapter = new CartItemRecyclerViewAdapter(this, this);
        cart_items_list.setAdapter(cartItemAdapter);
        //Bitmap bitmap = ((BitmapDrawable)HomepageConfig.getInstance().getBusket().getDrawable()).getBitmap();
        //couffin.setImageBitmap(bitmap);


        orderBtn.setOnClickListener(e -> {
            if (User.getInstance() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("ask_for_select", true);
                startActivityForResult(intent, Config.REQUEST_CODE_CHECKOUT);
            } else {
                if (ShoppingCart.getInstance().isEmpty()) {
                    Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_LONG).show();
                } else if (ShoppingCart.getInstance().getCartSubTotal() < minPrice /*ShoppingCart.getInstance().getMinCartTotal()*/) {
                    Toast.makeText(this, "Votre panier est inférieur à " + minPrice/*ShoppingCart.getInstance().getMinCartTotal()*/ + " DT", Toast.LENGTH_LONG).show();
                } else {
                    if (!User.getInstance().getAddresses().isEmpty() && User.getInstance().getSelectedAddress() != null) {
                        Intent intent = new Intent(this, CheckoutActivity.class);
                        intent.putExtra("sub-total", ShoppingCart.getInstance().getCartSubTotal());
                        intent.putExtra("delivery", deliveryCost /*ShoppingCart.getInstance().getCartDelivery()*/);
                        intent.putExtra("total", ShoppingCart.getInstance().getCartSubTotal() + deliveryCost/*ShoppingCart.getInstance().getCartDelivery()*/);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(this, SelectAddressActivity.class);
                        startActivity(intent);
                    }

                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if ((getSupportFragmentManager() != null)) {
            update();
        }
    }


    public void update() {
        ShoppingCart cart = ShoppingCart.getInstance();
        DecimalFormat dec = new DecimalFormat("#0.00");
        cart_delivery.setText(dec.format(cart.getCartDelivery()) + " DT");
        cart_total.setText(dec.format(cart.getCartSubTotal() + cart.getCartDelivery()) + " DT");

        if (ShoppingCart.getInstance().isEmpty()) {
            Utilities.statusChangerUnavailable(this, "Le panier est vide", cart_empty, cart_items_list);
            cart_items_list.setVisibility(View.VISIBLE);
            layoutOrderInfo.setVisibility(View.GONE);
        } else {

            containsPricelessItems(ShoppingCart.getInstance());
            if (ShoppingCart.getInstance().getItems().size() > 0) {
                layoutOrderInfo.setVisibility(View.VISIBLE);
                cart_empty.setVisibility(View.GONE);
            } else {
                layoutOrderInfo.setVisibility(View.GONE);
                Utilities.statusChangerUnavailable(this, "Le panier est vide", cart_empty, cart_items_list);
                cart_items_list.setVisibility(View.VISIBLE);
            }
           /* if (containsPricelessItems(ShoppingCart.getInstance()) > 0) {

                Utilities.statusChangerUnavailable(this, "Le panier est vide", cart_empty, cart_items_list);
                cart_items_list.setVisibility(View.VISIBLE);
            } else {
                cart_empty.setVisibility(View.INVISIBLE);
            }*/

        }

        cartItemAdapter.setCartItems(ShoppingCart.getInstance().getItems());
        cartItemAdapter.notifyDataSetChanged();

    }

    /**
     * remove snack from ShoppingCart if its price
     *
     * @param shoppingCart
     * @return
     */
    private int containsPricelessItems(ShoppingCart shoppingCart) {
        int pricelessCount = 0;
        Iterator it = shoppingCart.getItems().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry snack = (Map.Entry) it.next();
            if (((ShoppingCart.Options) snack.getValue()).getQuantity() == 0) {
                // shoppingCart.getItems().remove(snack.getKey());
                it.remove();
                pricelessCount++;
            }


        }
        return pricelessCount;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_CODE_CHECKOUT) {
            if (User.getInstance() != null && !User.getInstance().getAddresses().isEmpty() && User.getInstance().getSelectedAddress() != null) {
                Intent intent = new Intent(this, CheckoutActivity.class);
                intent.putExtra("sub-total", ShoppingCart.getInstance().getCartSubTotal());
                intent.putExtra("delivery", deliveryCost /*ShoppingCart.getInstance().getCartDelivery()*/);
                intent.putExtra("total", ShoppingCart.getInstance().getCartSubTotal() + deliveryCost/*ShoppingCart.getInstance().getCartDelivery()*/);
                startActivity(intent);
            } else if (User.getInstance() != null) {
                Intent intent = new Intent(this, NewAddressActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void getSnackId(int snackId) {
        this.snackId = snackId;
        snackIdList.add(snackId);
        if (idQuantityMap != null) {
            idQuantityMap.remove(snackId);
        }
    }

    @Override
    public void addSnack(Dish dish) {

        ArrayList<Dish.Option> options = new ArrayList<>();
        options.add(new Dish.Option(dish.getId(), dish.getTitle(), 0));

        ArrayList<Dish.Component> components = new ArrayList<>();


        ShoppingCart.getInstance().addToCart(dish, 1, null, components, "");
        update();


        idQuantityMap.put(dish.getId(), dish.getQuantity());


    }

    @Override
    public void deleteSnack(Dish dish) {
        ArrayList<Dish.Option> options = new ArrayList<>();
        options.add(new Dish.Option(dish.getId(), dish.getTitle(), 0));

        ArrayList<Dish.Component> components = new ArrayList<>();

        ShoppingCart.getInstance().addToCart(dish, -1, null, components, "");
        update();

        idQuantityMap.put(dish.getId(), dish.getQuantity());
    }
}

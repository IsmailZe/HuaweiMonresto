package com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.Menu;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.MyListView;
import com.monresto.acidlabs.monresto.ObjectSerializer;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantAsyncResponse;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity implements RestaurantAsyncResponse {

    CollapsingToolbarLayout toolbar_layout;
    RestaurantService restaurantService;
    OptionRecyclerViewAdapter optionsAdapter;
    ComponentsAdapter componentsAdapter;
    ArrayList<ListView> componentsLists;
    int currentQuantity;
    @BindView(R.id.lists_container)
    LinearLayout lists_container;
    @BindView(R.id.dish_name)
    TextView dish_name;
    @BindView(R.id.total_order)
    TextView total_order;
    @BindView(R.id.dimensions_list)
    RecyclerView dimensions_list;
    @BindView(R.id.dimensions_text)
    TextView dimensions_text;
    @BindView(R.id.dish_description)
    TextView dish_description;
    @BindView(R.id.dish_price)
    TextView dish_price;
    @BindView(R.id.dish_quantity)
    TextView dish_quantity;
    @BindView(R.id.dish_quantity_add)
    ImageView dish_quantity_add;
    @BindView(R.id.dish_quantity_reduce)
    ImageView dish_quantity_reduce;
    @BindView(R.id.cancel_order)
    Button cancel_order;
    @BindView(R.id.add_to_cart)
    Button add_to_cart;
    @BindView(R.id.btnClose)
    ImageView btnClose;
    @BindView(R.id.additional_informations)
    TextView comments;
    ShoppingCart.Options cartOptions;
    private Dish dish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get dish information from the caller intent
        Intent i = getIntent();
        dish = i.getParcelableExtra("dish");
        cartOptions = null;

        //Init fields for modify after calling from cart
        /*try{
            String serialDish = i.getExtras().getString("serialDish", "");
            String serialOptions = i.getExtras().getString("serialOptions", "");
            if(!serialDish.equals("") && !serialOptions.equals("")){
                dish = (Dish) ObjectSerializer.deserialize(serialDish);
                cartOptions = (ShoppingCart.Options) ObjectSerializer.deserialize(serialDish);
            }

        }
        catch (Exception ignored){

        }*/

        //if(cartOptions!=null)
        //initFields();


        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        currentQuantity = Integer.valueOf(dish_quantity.getText().toString());

        dish_name.setText(Utilities.decodeUTF(dish.getTitle()));
        if (Double.isNaN(dish.getPrice()))
            dish_price.setText("Choisissez parmi les options");
        else {
            DecimalFormat dec = new DecimalFormat("#0.00");
            dish_price.setText(dec.format(dish.getPrice()) + " DT");
        }
        dish_description.setText(Utilities.decodeUTF(dish.getDescription()));

        dish_quantity_add.setOnClickListener(view -> {
            updateTotalOnQuantityChanged(currentQuantity + 1);
            dish_quantity.setText(Integer.toString(currentQuantity));
        });

        dish_quantity_reduce.setOnClickListener(view -> {
            if (Integer.valueOf(dish_quantity.getText().toString()) > 1) {
                updateTotalOnQuantityChanged(currentQuantity - 1);
                dish_quantity.setText(Integer.toString(currentQuantity));
            }
        });

        total_order.setText(String.valueOf(Math.round(dish.getPrice())));

        btnClose.setOnClickListener(view -> finish());
        cancel_order.setOnClickListener(view -> finish());

        dish_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty())
                    updateTotalOnQuantityChanged(1);
                else updateTotalOnQuantityChanged(Integer.valueOf(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        add_to_cart.setOnClickListener(view -> {
            ArrayList<Dish.Component> components = new ArrayList<>();
            ArrayList<Dish.Option> options;

            if (componentsLists != null)
                for (int i12 = 0; i12 < componentsLists.size(); i12++) {
                    options = new ArrayList<>();
                    ComponentsAdapter componentsAdapterTemp = (ComponentsAdapter) componentsLists.get(i12).getAdapter();

                    for (int j = 0; j < componentsAdapterTemp.getCheckedItemsPositions().size(); j++)
                        options.add(componentsAdapterTemp.getItem(componentsAdapterTemp.getCheckedItemsPositions().get(j)));

                    components.add(new Dish.Component(dish.getComponents().get(i12).getId(), dish.getComponents().get(i12).getName(), dish.getComponents().get(i12).getNumberChoice(), dish.getComponents().get(i12).getNumberChoiceMax(), options));
                }

            boolean added;
            if (optionsAdapter != null)
                added = ShoppingCart.getInstance().addToCart(dish, Integer.valueOf(dish_quantity.getText().toString()), optionsAdapter.getItem(optionsAdapter.getSelectedItem()), components, comments.getText().toString());
            else
                added = ShoppingCart.getInstance().addToCart(dish, Integer.valueOf(dish_quantity.getText().toString()), null, components, comments.getText().toString());

            if (added) {

                Toast.makeText(OrderActivity.this, "Ajouté: " + Integer.valueOf(dish_quantity.getText().toString()) + " " + dish.getTitle(), Toast.LENGTH_LONG).show();
                Bundle params = new Bundle();

                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Dish");
                params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, dish.toString());

                Config.logger.logEvent("Add to cart",
                        1,
                        params);

            } else
                Toast.makeText(OrderActivity.this, "Veuillez valider votre panier avant de commander d'un autre restaurant", Toast.LENGTH_LONG).show();

            SharedPreferences sharedPreferences = getSharedPreferences("itemsList", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();

            String pos = ObjectSerializer.serialize(ShoppingCart.getInstance());
            editor.putString("items", pos);

            editor.apply();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("ADDED_QUANTITY", Integer.valueOf(dish_quantity.getText().toString()));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        final ImageView img = new ImageView(this);

        if (dish.isComposed()) {
            total_order.setText("0");
            restaurantService = new RestaurantService(this);
            restaurantService.getComposedDish(dish);
        }


        setSupportActionBar(toolbar);

    }

    @Override
    public void onListReceived(ArrayList<Restaurant> restaurantList, int status) {

    }

    @Override
    public void onMenusReceived(ArrayList<Menu> menus) {

    }

    @Override
    public void onDishesReceived(ArrayList<Dish> dishes, Menu menu) {

    }

    @Override
    public void onComposedDishReceived(final Dish dish) {
        this.dish = dish;

        dimensions_list.setVisibility(View.VISIBLE);
        dimensions_text.setVisibility(View.VISIBLE);


        optionsAdapter = new OptionRecyclerViewAdapter(dish.getDimensions(), this, total_order, dish_quantity);
        dimensions_list.setAdapter(optionsAdapter);
//        dimensions_list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utilities.convDpToPx(this, 30) * dish.getDimensions().size() + Utilities.convDpToPx(this, 16)));
        dimensions_list.setLayoutManager(new LinearLayoutManager(this));
        dimensions_list.setNestedScrollingEnabled(false);
        dimensions_list.requestLayout();

        componentsLists = new ArrayList<>();

        for (int i = 0; i < dish.getComponents().size(); i++) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.textview_order_header, null);
            textView.setText(dish.getComponents().get(i).getName() + " (" + dish.getComponents().get(i).getNumberChoiceMax() + " CHOIX)");
            lists_container.addView(textView);

            MyListView listViewTemp = (MyListView) LayoutInflater.from(this).inflate(R.layout.listview_order_options, null);
            componentsAdapter = new ComponentsAdapter(dish.getComponents().get(i).getOptions(), this, dish.getComponents().get(i).getNumberChoiceMax(), total_order, dish_quantity);

            componentsLists.add(listViewTemp);
            componentsLists.get(i).setAdapter(componentsAdapter);


            componentsLists.get(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utilities.convDpToPx(this, 30) * dish.getComponents().get(i).getNumberChoice() + Utilities.convDpToPx(this, 16)));
            componentsLists.get(i).requestLayout();

            lists_container.addView(componentsLists.get(i));
        }

    }

    @Override
    public void onError(VolleyError error) {

    }

    private void updateTotalOnQuantityChanged(int quantity) {
        DecimalFormat dec = new DecimalFormat("#0.00");
        try {
            total_order.setText(String.valueOf(dec.format(Double.valueOf(total_order.getText().toString().replace(",", ".")) / currentQuantity * quantity)));
        } catch (Exception ignored) {

        }
        currentQuantity = quantity;
    }

    public void initFields() {
        updateTotalOnQuantityChanged(cartOptions.getQuantity());
    }

}

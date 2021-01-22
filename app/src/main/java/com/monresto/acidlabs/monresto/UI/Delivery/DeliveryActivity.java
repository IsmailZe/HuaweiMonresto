package com.monresto.acidlabs.monresto.UI.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.User.UserAsyncResponse;
import com.monresto.acidlabs.monresto.UI.Checkout.CheckoutActivity;
import com.monresto.acidlabs.monresto.UI.User.LoginActivity;
import com.monresto.acidlabs.monresto.UI.User.SelectAddressActivity;
import com.monresto.acidlabs.monresto.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryActivity extends AppCompatActivity implements UserAsyncResponse {

    @BindView(R.id.dish_quantity_add)
    ImageView dish_quantity_add;
    @BindView(R.id.dish_quantity_reduce)
    ImageView dish_quantity_reduce;
    @BindView(R.id.dish_quantity)
    TextView dish_quantity;
    @BindView(R.id.text_comments)
    EditText text_comments;
    @BindView(R.id.btnOrder)
    Button btnOrder;
    private int quantity = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        ButterKnife.bind(this);

        Utilities.changeStatusBarColors(this, R.color.black_hlib_ghoula);

        dish_quantity_add.setOnClickListener(view -> {
            quantity++;
            dish_quantity.setText(Integer.toString(quantity));
        });

        dish_quantity_reduce.setOnClickListener(view -> {
            if (Integer.valueOf(dish_quantity.getText().toString()) > 1) {
                quantity--;
                dish_quantity.setText(Integer.toString(quantity));
            }
        });

        btnOrder.setOnClickListener(e -> {
            if (User.getInstance() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("ask_for_select", true);
                startActivityForResult(intent, Config.REQUEST_CODE_CHECKOUT);
            } else {
                Intent intent = new Intent(this, SelectAddressActivity.class);
                startActivityForResult(intent, Config.REQUEST_CODE_CHECKOUT);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_CODE_CHECKOUT)
            if (resultCode == RESULT_OK) {
                submit();
            }
    }

    @Override
    public void onSubmitOrder(boolean success) {
        if (success)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Failed: check logs for order response", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onBackPressed(View v) {
        onBackPressed();
    }

    /**
     * display CheckoutActivity
     */
    private void submit() {

        String comment = text_comments.getText().toString();
        quantity = Integer.valueOf(dish_quantity.getText().toString());

        Dish dish = new Dish(25101, "", 1);

        dish.setRestoID(370);
        ShoppingCart shoppingCart = ShoppingCart.createInstance();

        shoppingCart.addToCart(dish, quantity, null, null, comment);

        ShoppingCart.setInstance(shoppingCart);
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("delivery", 3.0);
        intent.putExtra("total", 3.0);
        startActivity(intent);
        finish();
    }
}

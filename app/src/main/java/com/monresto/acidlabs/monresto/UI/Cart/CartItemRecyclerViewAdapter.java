package com.monresto.acidlabs.monresto.UI.Cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {


    private ArrayList mData;
    private Context context;
    private CartItemListener listener;
    private DecimalFormat dec = new DecimalFormat("#0.00");

    public CartItemRecyclerViewAdapter(Context context, CartItemListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Map.Entry<Dish, ShoppingCart.Options> item = (Map.Entry<Dish, ShoppingCart.Options>) mData.get(position);

        viewHolder.cart_title.setText(item.getKey().getTitle());
        viewHolder.cart_quantity.setText("Quantité: " + item.getValue().getQuantity());
        viewHolder.cart_quantity2.setText(String.valueOf(item.getValue().getQuantity()));
        if (item.getValue().getDimension() != null)
            viewHolder.cart_option.setText(item.getValue().getDimension().getTitle());
        else viewHolder.cart_option.setText("Pas d'options");

        Picasso.get().load(item.getKey().getImagePath()).into(viewHolder.cart_picture);
        double price = 0, unitPrice = 0; //price to calculate total, unit price is the dish price or the selected dimension price if dish is composed and has dimensions

        int compSize = 0;
        double compPrice = 0;
        for (int i = 0; i < item.getValue().getComponents().size(); i++) {
            compSize += item.getValue().getComponents().get(i).getOptions().size();
            for (int j = 0; j < item.getValue().getComponents().get(i).getOptions().size(); j++) {
                compPrice += item.getValue().getComponents().get(i).getOptions().get(j).getPrice();
            }
        }
        if (compSize > 0) {
            viewHolder.cart_components.setText("+ " + compSize + " suppléments");
            viewHolder.cart_components.setVisibility(View.VISIBLE);
        } else viewHolder.cart_components.setVisibility(View.GONE);

        unitPrice = item.getKey().getPrice();

        if (item.getValue().getDimension() != null && item.getValue().getDimension().getPrice() != 0) {
            unitPrice = item.getValue().getDimension().getPrice();
        }

        price = (unitPrice + compPrice) * item.getValue().getQuantity();


        viewHolder.cart_price.setText(dec.format(price) + " DT");
        viewHolder.cart_remove_btn.setOnClickListener(view -> {
            ShoppingCart.getInstance().removeFromCart(item.getKey());
            mData.remove(mData.get(position));
            ((CartActivity) context).update();
            SharedPreferences sharedPreferences = context.getSharedPreferences("itemsList", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            if (listener != null) {
                listener.getSnackId(item.getKey().getId());
            }
        });
        viewHolder.cart_picture.setOnClickListener(e -> {
            //maybe add later open order details to change options
            //String serialDish = ObjectSerializer.serialize(item.getKey());
            //String serialOptions = ObjectSerializer.serialize(item.getValue());
            //Intent intent = new Intent(context, OrderActivity.class);
            //intent.putExtra("serialDish", serialDish);
            //intent.putExtra("serialOptions", serialOptions);
            //context.startActivity(intent);
        });
        viewHolder.dishQuantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View click) {

                item.getKey().setQuantity(item.getValue().getQuantity() + 1);
                viewHolder.cart_quantity.setText("Quantité: " + item.getKey().getQuantity());
                double currentItemPrice = item.getKey().getQuantity() * item.getKey().getPrice();
                viewHolder.cart_price.setText(dec.format(currentItemPrice) + " DT");
                if (listener != null) {
                    listener.addSnack(item.getKey());
                }

            }
        });
        viewHolder.dishQuantityReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View click) {
                if (item.getValue().getQuantity() > 1) {

                    item.getKey().setQuantity(item.getValue().getQuantity() - 1);
                    viewHolder.cart_quantity.setText("Quantité: " + item.getKey().getQuantity());
                    double currentItemPrice = item.getKey().getQuantity() * item.getKey().getPrice();
                    viewHolder.cart_price.setText(dec.format(currentItemPrice) + " DT");


                    if (listener != null) {
                        listener.deleteSnack(item.getKey());
                    }
                }
            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_cart, viewGroup, false);

        return new ViewHolder(v);
    }

    public void setCartItems(Map<Dish, ShoppingCart.Options> items) {
        mData = new ArrayList();
        mData.addAll(items.entrySet());
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public interface CartItemListener {
        void getSnackId(int snackId);

        void addSnack(Dish snack);

        void deleteSnack(Dish snack);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cart_title)
        TextView cart_title;
        @BindView(R.id.cart_option)
        TextView cart_option;
        @BindView(R.id.cart_components)
        TextView cart_components;
        @BindView(R.id.cart_price)
        TextView cart_price;
        @BindView(R.id.cart_quantity)
        TextView cart_quantity;
        @BindView(R.id.cart_quantity2)
        TextView cart_quantity2;
        @BindView(R.id.cart_picture)
        ImageView cart_picture;
        @BindView(R.id.cart_remove_btn)
        ImageView cart_remove_btn;
        @BindView(R.id.dish_quantity_add)
        ImageView dishQuantityAdd;
        @BindView(R.id.dish_quantity_reduce)
        ImageView dishQuantityReduce;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

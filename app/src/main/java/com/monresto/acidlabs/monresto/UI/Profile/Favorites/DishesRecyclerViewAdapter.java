package com.monresto.acidlabs.monresto.UI.Profile.Favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.RoundedTransformation;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order.OrderActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishesRecyclerViewAdapter extends RecyclerView.Adapter<DishesRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Dish> dishes;
    private RestaurantService service;

    public DishesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_dish, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Dish dish;
        if (dishes != null && !dishes.isEmpty()) {
            dish = dishes.get(position);
            viewHolder.dish_name_id.setText(dish.getTitle());
            viewHolder.dish_price_id.setText(String.format("%s DT", String.valueOf(dish.getPrice())));
            Picasso.get().load(dish.getImagePath()).transform(new RoundedTransformation(80, 0)).into(viewHolder.dish_bg);

            // Checks if dish is favorite
            if (dishes.get(position).isFavorite()) {
                Picasso.get().load(R.drawable.heart_filled).into(viewHolder.heart);
            }
            viewHolder.heart.setOnClickListener(view -> {
                if (dishes.get(position).isFavorite()) {
                    service = new RestaurantService(context);
                    if (User.getInstance() != null) {
                        service.setFavorite(dishes.get(position).getId(), false);
                    }
                    dishes.get(position).setFavorite(false);
                    Toast.makeText(context, "Le plat n'est plus dans vos favoris.", Toast.LENGTH_LONG).show();
                    Picasso.get().load(R.drawable.heart_empty).into(viewHolder.heart);
                } else {
                    service = new RestaurantService(context);
                    dishes.get(position).setFavorite(true);
                    if (User.getInstance() != null) {
                        service.setFavorite(dishes.get(position).getId(), true);
                    }
                    Toast.makeText(context, "Le plat a été ajouté aux favoris.", Toast.LENGTH_LONG).show();
                    Picasso.get().load(R.drawable.heart_filled).into(viewHolder.heart);
                }
            });

            viewHolder.constraintLayout.setOnClickListener(e -> {
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("dish", (Parcelable) dish);
                context.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemCount() {
        if (dishes == null)
            return 0;
        return dishes.size();
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dish_name_id)
        TextView dish_name_id;
        @BindView(R.id.dish_price_id)
        TextView dish_price_id;
        @BindView(R.id.dish_bg_id)
        ImageView dish_bg;
        @BindView(R.id.constraintLayout)
        ConstraintLayout constraintLayout;
        @BindView(R.id.heart)
        ImageView heart;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

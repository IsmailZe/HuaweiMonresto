package com.monresto.acidlabs.monresto.UI.RestaurantDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.facebook.appevents.AppEventsConstants;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Service.Restaurant.RestaurantService;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order.OrderActivity;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private ArrayList<Dish> dishes;
    private Context context;
    private RestaurantService service;

    public RestaurantDetailsAdapter(ArrayList<Dish> dishes, Context context) {
        this.dishes = dishes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView;

        if (viewType == TYPE_ITEM) {
            itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish, null);
            return new HolderItem(itemLayoutView);
        } else {
            itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, null);
            return new HolderFooter(itemLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == TYPE_ITEM) {
            ((HolderItem) viewHolder).name.setText(Utilities.decodeUTF(dishes.get(position).getTitle()));

            // Checks if price is unavailable
            if (Double.isNaN(dishes.get(position).getPrice()))
                ((HolderItem) viewHolder).price.setText("Prix indisponible");
            else
                ((HolderItem) viewHolder).price.setText("Prix: " + dishes.get(position).getPrice() + " DT");

            // Loads background image
            Log.e("link", dishes.get(position).getImagePath());
            if (dishes.get(position).getImagePath().equals("https://www.monresto.net/test/images/nophoto.png")) {

                ((HolderItem) viewHolder).cardView.setVisibility(View.INVISIBLE);
            } else {

                Picasso.get().load(dishes.get(position).getImagePath()).into(((HolderItem) viewHolder).bg_img);

            }

            if (dishes.get(position).isRestoOpen())
                ((HolderItem) viewHolder).addButton.setVisibility(View.VISIBLE);
            else ((HolderItem) viewHolder).addButton.setVisibility(View.GONE);


            // Checks if dish is favorite
            if (dishes.get(position).isFavorite()) {
                Picasso.get().load(R.drawable.heart_filled).into(((HolderItem) viewHolder).heart);
            }
            ((HolderItem) viewHolder).heart.setOnClickListener(view -> {
                if (dishes.get(position).isFavorite()) {
                    service = new RestaurantService(context);
                    if (User.getInstance() != null) {
                        service.setFavorite(dishes.get(position).getId(), false);
                    }
                    dishes.get(position).setFavorite(false);
                    Toast.makeText(context, "Le plat n'est plus dans vos favoris.", Toast.LENGTH_LONG).show();
                    Picasso.get().load(R.drawable.heart_empty).into(((HolderItem) viewHolder).heart);
                } else {
                    service = new RestaurantService(context);
                    dishes.get(position).setFavorite(true);
                    if (User.getInstance() != null) {
                        service.setFavorite(dishes.get(position).getId(), true);
                    }
                    Toast.makeText(context, "Le plat a été ajouté aux favoris.", Toast.LENGTH_LONG).show();
                    Picasso.get().load(R.drawable.heart_filled).into(((HolderItem) viewHolder).heart);

                    Bundle params = new Bundle();
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Menu");
                    params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, dishes.get(position).toString());

                    Config.logger.logEvent("Ajouter à la liste de souhaits",
                            1,
                            params);

                }
            });


            // On click event
            if (dishes.get(position).isRestoOpen())
                ((HolderItem) viewHolder).constraintLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("dish", (Parcelable) dishes.get(viewHolder.getAdapterPosition()));
                    context.startActivity(intent);
                });

            // Re-setting layout width because somehow it changes automatically
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            ((HolderItem) viewHolder).constraintLayout.setLayoutParams(lp);

            ((HolderItem) viewHolder).addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("dish", (Parcelable) dishes.get(viewHolder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dishes.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < dishes.size())
            return TYPE_ITEM;

        return TYPE_FOOTER;
    }

    public static class HolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.dish_name_id)
        TextView name;
        @BindView(R.id.dish_price_id)
        TextView price;
        @BindView(R.id.dish_bg_id)
        ImageView bg_img;
        @BindView(R.id.heart)
        ImageView heart;
        @BindView(R.id.constraintLayout)
        ConstraintLayout constraintLayout;
        @BindView(R.id.button_add)
        Button addButton;
        @BindView(R.id.dish_bg_card)
        CardView cardView;

        public HolderItem(View itemLayoutView) {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }
    }

    public static class HolderFooter extends RecyclerView.ViewHolder {
        public HolderFooter(View itemLayoutView) {
            super(itemLayoutView);
        }
    }
}

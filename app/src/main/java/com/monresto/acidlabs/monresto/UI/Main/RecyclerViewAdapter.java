package com.monresto.acidlabs.monresto.UI.Main;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.monresto.acidlabs.monresto.Config;
import com.monresto.acidlabs.monresto.Model.Restaurant;
import com.monresto.acidlabs.monresto.Model.ShoppingCart;
import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.UI.RestaurantDetails.RestaurantDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    ViewHolder header;
    private Context context;
    private List<Restaurant> restaurants;
    private ArrayList<Speciality> specialities;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public RecyclerViewAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        View v;
        final ViewHolder viewHolder;
        if (viewType == TYPE_HEADER) {
            if (header == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_restaurants_header, viewGroup, false);
                header = new HolderHeader(v);
                ((HolderHeader) header).setSearchBarContext(context);
                ((HolderHeader) header).initFilterRecyclerView(context);
                ((HolderHeader) header).setSpecialities(specialities);
            }
            viewHolder = header;


        } else if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(context).inflate(R.layout.item_store, viewGroup, false);
            viewHolder = new HolderItem(v);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.fragment_unavailable, viewGroup, false);
            viewHolder = new HolderItem(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == TYPE_ITEM) {
            final Restaurant restaurantItem = restaurants.get(i - 1);
            setFadeAnimation(viewHolder.itemView);

            Picasso.get().load(restaurantItem.getBackground()).fit().into(((HolderItem) viewHolder).store_bg, new com.squareup.picasso.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSuccess() {
                    ((HolderItem) viewHolder).storeName.setText(restaurantItem.getName());

                    if (restaurantItem.getState().equals("open"))
                        ((HolderItem) viewHolder).storeState.setText("OUVERT");
                    else ((HolderItem) viewHolder).storeState.setText("FERMÉ");

                    if (restaurantItem.getRate() == 0) {
                        ((HolderItem) viewHolder).rating_smiley.setVisibility(View.GONE);
                        ((HolderItem) viewHolder).item_store_rating.setVisibility(View.GONE);
                    }
                    if (restaurantItem.getRate() * 20 < 20)
                        Picasso.get().load(R.drawable.rate_74xhdpi).fit().into(((HolderItem) viewHolder).rating_smiley);
                    else if (restaurantItem.getRate() * 20 < 40)
                        Picasso.get().load(R.drawable.rate_73xhdpi).fit().into(((HolderItem) viewHolder).rating_smiley);
                    else if (restaurantItem.getRate() * 20 < 60)
                        Picasso.get().load(R.drawable.rate_72xhdpi).fit().into(((HolderItem) viewHolder).rating_smiley);
                    else if (restaurantItem.getRate() * 20 < 80)
                        Picasso.get().load(R.drawable.rate_71xhdpi).fit().into(((HolderItem) viewHolder).rating_smiley);
                    else
                        Picasso.get().load(R.drawable.rate_70xhdpi).fit().into(((HolderItem) viewHolder).rating_smiley);

                    ((HolderItem) viewHolder).item_store_rating.setText((int) (restaurantItem.getRate() * 20) + "%");
                    ((HolderItem) viewHolder).restaurant_delivery.setText(restaurantItem.getEstimatedTime() + "\nMin");
                    ((HolderItem) viewHolder).restaurantItem.setOnClickListener(e -> {
                        if (!ShoppingCart.getInstance().isEmpty() && ShoppingCart.getInstance().getRestoID() != restaurantItem.getId()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Vider le panier et changer de restaurant ?");

                            builder.setPositiveButton("OK", (dialog, which) -> {
                                ShoppingCart.getInstance().clear();
                                SharedPreferences sharedPreferences = context.getSharedPreferences("itemsList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                                intent.putExtra("restaurant", restaurantItem);

                                try {

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context.getApplicationContext(), ((HolderItem) viewHolder).store_bg, "store_bg");
                                    // start the new activity
                                    context.startActivity(intent, options.toBundle());

                                } catch (ClassCastException ce) {

                                    // start the new activity
                                    context.startActivity(intent);

                                }

                            });
                            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

                            AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(arg0 -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#33b998"));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#33b998"));
                            });
                            dialog.show();
                        } else {
                            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                            intent.putExtra("restaurant", restaurantItem);
                            context.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else if (viewHolder.getItemViewType() == TYPE_HEADER) {
            ((HolderHeader) viewHolder).filtersToggle.setOnClickListener(e -> {
                Intent intent = new Intent(context, FilterActivity.class);
                ((MainActivity) context).startActivityForResult(intent, Config.REQUEST_CODE_FILTER_SELECT);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (restaurants.isEmpty())
            return 2;
        return restaurants.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;

        if (restaurants.isEmpty() && position == 1)
            return TYPE_EMPTY;

        return TYPE_ITEM;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public void setSpecialities(ArrayList<Speciality> specialities) {
        this.specialities = specialities;
        notifyDataSetChanged();
    }

    public static class HolderItem extends ViewHolder {
        TextView storeName;
        TextView storeState;
        ImageView store_bg;
        TextView restaurant_delivery;
        ConstraintLayout restaurantItem;
        TextView item_store_rating;
        ImageView rating_smiley;

        HolderItem(@NonNull View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.storeName);
            storeState = itemView.findViewById(R.id.storeState);
            restaurant_delivery = itemView.findViewById(R.id.restaurant_delivery);
            store_bg = itemView.findViewById(R.id.store_bg);
            restaurantItem = itemView.findViewById(R.id.restaurant_id);
            item_store_rating = itemView.findViewById(R.id.item_store_rating);
            rating_smiley = itemView.findViewById(R.id.rating_smiley);
        }
    }

    public static class HolderHeader extends ViewHolder {
        ImageView filtersToggle;
        MaterialSearchBar searchBar;
        TextView searchPlaceHolder;
        RecyclerView filterRecylcerView;
        ArrayList<Speciality> specialities;
        SpecialityRecyclerViewAdapter specialityRecyclerViewAdapter;

        HolderHeader(@NonNull View itemView) {
            super(itemView);
            filtersToggle = itemView.findViewById(R.id.filtersToggle);
            searchBar = itemView.findViewById(R.id.searchBar);
            filterRecylcerView = itemView.findViewById(R.id.filterRecylcerView);

            searchPlaceHolder = itemView.findViewById(R.id.mt_placeholder);


        }

        void setSearchBarContext(Context context) {
            searchBar.setOnSearchActionListener((MaterialSearchBar.OnSearchActionListener) context);
            searchPlaceHolder.setTextColor(Color.parseColor("#cdcdcd"));
            searchPlaceHolder.setTextAppearance(context, R.style.DefaultTextView);
        }

        void initFilterRecyclerView(Context context) {
            filterRecylcerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            specialityRecyclerViewAdapter = new SpecialityRecyclerViewAdapter(context);
            filterRecylcerView.setAdapter(specialityRecyclerViewAdapter);
        }

        void setSpecialities(ArrayList<Speciality> specialities) {
            this.specialities = specialities;
            specialityRecyclerViewAdapter.setFilters(specialities);
        }

    }
}

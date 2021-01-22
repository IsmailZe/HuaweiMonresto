package com.monresto.acidlabs.monresto.UI.Homepage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.HomepageDish;
import com.monresto.acidlabs.monresto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomepageDishesAdapter extends RecyclerView.Adapter<HomepageDishesAdapter.ViewHolder> {

    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context context;
    private ArrayList<HomepageDish> dishes;

    public HomepageDishesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HomepageDishesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_homepage_dish, viewGroup, false);

        return new HomepageDishesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomepageDishesAdapter.ViewHolder viewHolder, int i) {
        HomepageDish dish;
        setFadeAnimation(viewHolder.itemView);

        /*if(dishes != null && dishes.size()>1)
            viewHolder.cardViewBg.setLayoutParams(new ConstraintLayout.LayoutParams(viewHolder.cardViewBg.getMeasuredWidth() - 20, viewHolder.cardViewBg.getMeasuredHeight()));*/
        if (dishes != null && !dishes.isEmpty()) {
            dish = dishes.get(i);
            viewHolder.item_title.setText(dish.getTitle());
            viewHolder.item_label.setText(dish.getLabel());
            Picasso.get().load(dish.getImage()).into(viewHolder.item_bg);
            viewHolder.itemContainer.setOnClickListener(e -> {
                //
                /*Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);*/
            });
        }
    }

    @Override
    public int getItemCount() {
        if (dishes == null)
            return 0;
        return dishes.size();
    }

    public void setDishes(ArrayList<HomepageDish> dishes) {
        this.dishes = dishes;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView item_title;
        @BindView(R.id.item_label)
        TextView item_label;
        @BindView(R.id.item_bg)
        ImageView item_bg;
        @BindView(R.id.cardViewBg)
        CardView cardViewBg;
        @BindView(R.id.itemContainer)
        ConstraintLayout itemContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}

package com.monresto.acidlabs.monresto.UI.Homepage.Snacks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SnacksAdapter extends RecyclerView.Adapter<SnacksAdapter.ViewHolder> {

    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context context;
    private ArrayList<Dish> snacks;

    public SnacksAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SnacksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_snack, viewGroup, false);

        return new SnacksAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SnacksAdapter.ViewHolder viewHolder, int i) {
        Dish snack;
        setFadeAnimation(viewHolder.itemView);
        if (snacks != null && !snacks.isEmpty()) {
            snack = snacks.get(i);
            viewHolder.item_title.setText(Utilities.decodeUTF(snack.getTitle()));
            viewHolder.item_price.setText(snack.getPrice() + " TND");
            System.out.println("PATH: " + snack.getImagePath());
            if (!snack.getImagePath().equals(""))
                Picasso.get().load(snack.getImagePath()).into(viewHolder.item_bg);

            if (snack.isSnackSelected()) {
                viewHolder.layoutChooseQuantity.setVisibility(View.VISIBLE);
                viewHolder.addToCart.setVisibility(View.GONE);
            } else {
                viewHolder.addToCart.setVisibility(View.VISIBLE);
                viewHolder.layoutChooseQuantity.setVisibility(View.GONE);
            }

            viewHolder.item_bg.setOnClickListener(c -> {
                ((SnacksAdapterListener) context).displaySnackDetails(snack);
            });
            viewHolder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View e) {


                    viewHolder.layoutChooseQuantity.setVisibility(View.VISIBLE);
                    viewHolder.addToCart.setVisibility(View.GONE);
                    snack.setSnackSelected(true);
                    viewHolder.tvQuantity.setText(String.valueOf(snack.getQuantity() + 1));
                    snack.setQuantity(snack.getQuantity() + 1);

                    if (context instanceof SnacksAdapterListener) {
                        ((SnacksAdapterListener) context).addRestaurant(snack);
                    }

                }
            });
            viewHolder.tvQuantity.setText(String.valueOf(snack.getQuantity()));
            viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    viewHolder.tvQuantity.setText(String.valueOf(snack.getQuantity() + 1));
                    snack.setQuantity(snack.getQuantity() + 1);

                    if (context instanceof SnacksAdapterListener) {
                        ((SnacksAdapterListener) context).addRestaurant(snack);
                    }

                }
            });
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    if (snack.getQuantity() > 0) {
                        viewHolder.tvQuantity.setText(String.valueOf(snack.getQuantity() - 1));
                        snack.setQuantity(snack.getQuantity() - 1);
                        if (context instanceof SnacksAdapterListener) {
                            ((SnacksAdapterListener) context).deleteRestaurant(snack);
                        }
                        if (snack.getQuantity() == 0) {
                            viewHolder.layoutChooseQuantity.setVisibility(View.GONE);
                            viewHolder.addToCart.setVisibility(View.VISIBLE);
                            snack.setSnackSelected(false);
                        }
                    }

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (snacks == null)
            return 0;
        return snacks.size();
    }

    public ArrayList<Dish> getSnacks() {
        return this.snacks;
    }

    public void setSnacks(ArrayList<Dish> snacks) {
        this.snacks = snacks;
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void setSnacksListQuantity(ArrayList<Integer> snackIdList) {
        for (int i = 0; i < snackIdList.size(); i++) {
            for (int j = 0; j < snacks.size(); j++) {
                if (snacks.get(j).getId() == snackIdList.get(i)) {
                    snacks.get(j).setSnackSelected(false);
                    snacks.get(j).setQuantity(0);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSnacksListQuantity(ArrayList<Integer> snackIdList, ArrayList<Integer> quantityList) {
        for (int i = 0; i < snackIdList.size(); i++) {
            for (int j = 0; j < snacks.size(); j++) {
                if (snacks.get(j).getId() == snackIdList.get(i)) {
                    snacks.get(j).setSnackSelected(true);
                    snacks.get(j).setQuantity(quantityList.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void incrementSnackQuantity(int id, int quantity) {

        for (int j = 0; j < snacks.size(); j++) {
            if (snacks.get(j).getId() == id) {
                snacks.get(j).setQuantity(snacks.get(j).getQuantity() + quantity);
            }
        }

        notifyDataSetChanged();
    }

    public interface SnacksAdapterListener {
        void addRestaurant(Dish dish);

        void deleteRestaurant(Dish dish);

        void displaySnackDetails(Dish dish);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView item_title;
        @BindView(R.id.item_price)
        TextView item_price;
        @BindView(R.id.item_bg)
        ImageView item_bg;
        @BindView(R.id.addToCart)
        Button addToCart;
        @BindView(R.id.itemContainer)
        ConstraintLayout itemContainer;

        @BindView(R.id.layout_choose_quantity)
        RelativeLayout layoutChooseQuantity;
        @BindView(R.id.tv_quantity)
        TextView tvQuantity;
        @BindView(R.id.iv_add)
        ImageView ivAdd;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

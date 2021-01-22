package com.monresto.acidlabs.monresto.UI.RestaurantDetails.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionRecyclerViewAdapter extends RecyclerView.Adapter<OptionRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Dish.Option> optionsList;
    private Context context;
    private int selectedItem;
    private TextView total_order;
    private double oldPriceSelected;
    private TextView dish_quantity;

    public OptionRecyclerViewAdapter(ArrayList<Dish.Option> optionsList, Context context, TextView total_order, TextView dish_quantity) {
        this.optionsList = optionsList;
        this.context = context;
        selectedItem = 0;
        this.total_order = total_order;
        try {
            if (!optionsList.isEmpty() && optionsList.get(0) != null) {
                oldPriceSelected = optionsList.get(0).getPrice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.total_order.setText(String.valueOf(oldPriceSelected));
        this.dish_quantity = dish_quantity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_dish_option, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Dish.Option option = optionsList.get(position);


        if (option.getTitle().trim().length() > 0) {
            viewHolder.optionName.setText(option.getTitle());
        } else viewHolder.optionName.setText("Option " + position + 1);

        viewHolder.optionPrice.setText("(" + String.valueOf(option.getPrice()) + " DT)");

        if (position == selectedItem) {
            viewHolder.optionRadio.setChecked(true);
            total_order.setText(String.valueOf(Double.valueOf(total_order.getText().toString().replace(",", ".")) - oldPriceSelected * Integer.valueOf(dish_quantity.getText().toString())));
            total_order.setText(String.valueOf(Double.valueOf(total_order.getText().toString().replace(",", ".")) + option.getPrice() * Integer.valueOf(dish_quantity.getText().toString())));
            oldPriceSelected = option.getPrice();
        } else viewHolder.optionRadio.setChecked(false);

        viewHolder.itemView.setOnClickListener(v -> {
            selectedItem = position;
            notifyDataSetChanged();
        });
        viewHolder.optionRadio.setOnClickListener(v -> {
            selectedItem = position;
            notifyDataSetChanged();
        });
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public Dish.Option getItem(int position) {
        try {
            return optionsList.get(position);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public int getItemCount() {
        if (optionsList == null)
            return 0;
        return optionsList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_name)
        TextView optionName;
        @BindView(R.id.option_price)
        TextView optionPrice;
        @BindView(R.id.option_radio)
        RadioButton optionRadio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}

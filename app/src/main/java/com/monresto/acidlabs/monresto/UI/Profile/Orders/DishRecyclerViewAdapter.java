package com.monresto.acidlabs.monresto.UI.Profile.Orders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Dish;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishRecyclerViewAdapter extends RecyclerView.Adapter<DishRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Dish> dishes;

    public DishRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_store, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (dishes != null && !dishes.isEmpty()) {
            Dish dish = dishes.get(i);
            viewHolder.textOrderDish.setText(dish.getTitle());
            //viewHolder.textOrderTotal.setText(dish.getPrice());
            viewHolder.textOrderStatus.setText(dishes.get(i).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (dishes == null)
            return 0;
        return dishes.size();
    }

    public void setOrders(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textOrderDish)
        TextView textOrderDish;
        @BindView(R.id.textOrderTotal)
        TextView textOrderTotal;
        @BindView(R.id.textOrderStatus)
        TextView textOrderStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

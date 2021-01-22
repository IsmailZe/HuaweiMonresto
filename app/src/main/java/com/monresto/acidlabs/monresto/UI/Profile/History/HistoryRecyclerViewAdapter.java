package com.monresto.acidlabs.monresto.UI.Profile.History;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Order;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orders;

    public HistoryRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_history, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Order order;
        if (orders != null && !orders.isEmpty()) {
            order = orders.get(i);
            viewHolder.textHistoryNameItem.setText(order.getRestoName());
            viewHolder.textHistoryDateITem.setText(order.getOrderDate());
            viewHolder.textHistoryPriceItem.setText(String.format("%s DT", String.valueOf(order.getOrderPrice())));
            Picasso.get().load(order.getRestoImagePath()).transform(new RoundedTransformation(30, 0)).into(viewHolder.imageHistoryItem);
        }
    }

    @Override
    public int getItemCount() {
        if (orders == null)
            return 0;
        return orders.size();
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textHistoryNameItem)
        TextView textHistoryNameItem;
        @BindView(R.id.textHistoryPriceItem)
        TextView textHistoryPriceItem;
        @BindView(R.id.textHistoryDateITem)
        TextView textHistoryDateITem;
        @BindView(R.id.imageHistoryItem)
        ImageView imageHistoryItem;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

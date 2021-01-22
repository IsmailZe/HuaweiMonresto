package com.monresto.acidlabs.monresto.UI.Profile.Orders;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.Order;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;
import com.monresto.acidlabs.monresto.RoundedTransformation;
import com.monresto.acidlabs.monresto.Service.User.UserService;
import com.monresto.acidlabs.monresto.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orders;
    private Fragment fragment;

    public OrderRecyclerViewAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String dishes = "";
        Order order;
        order = orders.get(orders.size() - (i + 1));
        dishes = order.getDishesString();
        viewHolder.textRestoName.setText(Utilities.decodeUTF(order.getRestoName()));
        viewHolder.textOrderDish.setText(Utilities.decodeUTF(dishes));
        viewHolder.textOrderTotal.setText(String.format("%sDT", String.valueOf(order.getOrderPrice())));
        Picasso.get().load(order.getRestoImagePath()).transform(new RoundedTransformation(80, 0)).into(viewHolder.imageViewLogo);
        viewHolder.cause_refuse.setText(order.getCauseRefuse());

        viewHolder.layoutCancel.setVisibility(View.GONE);


        switch (order.getStatus()) {
            case "en_attente":
                viewHolder.textOrderStatus.setText("Commande en attente");
                viewHolder.status = 0;
                viewHolder.layoutCancel.setVisibility(View.VISIBLE);
                break;
            case "en_attente_rep_liv":
                viewHolder.textOrderStatus.setText("Commande acceptée");
                viewHolder.status = 1;
                break;
            case "arrive_resto":
                viewHolder.textOrderStatus.setText("Commande En préparation");
                viewHolder.status = 2;
                break;
            case "en_cours_prep":
                viewHolder.textOrderStatus.setText("Commande En préparation");
                viewHolder.status = 2;
                break;
            case "en_cours_liv":
                viewHolder.textOrderStatus.setText("Commande en route");
                viewHolder.status = 3;
                break;
            case "arrive_client":
                viewHolder.textOrderStatus.setText("Commande arrivée");
                viewHolder.status = 4;
                break;

            case "refuser":
                viewHolder.textOrderStatus.setText("Commande refusée");
                viewHolder.status = 6;

                break;


            default:

                break;
        }

        for (int j = 0; j < 5; j++) {


            if (j > viewHolder.status) {
                Picasso.get().load(viewHolder.pending_images[j]).into(viewHolder.pending_views[j]);
                viewHolder.pending_views[j].setColorFilter(Color.LTGRAY);
            } else {
                Picasso.get().load(viewHolder.pending_images[j]).into(viewHolder.pending_views[j]);
                viewHolder.pending_views[j].setColorFilter(Color.parseColor("#33b998"));
            }
        }

        if (viewHolder.status == 6) {

            for (int j = 0; j < 5; j++) {

                Picasso.get().load(viewHolder.pending_images[j]).into(viewHolder.pending_views[j]);
                viewHolder.pending_views[j].setColorFilter(Color.LTGRAY);
                Picasso.get().load(viewHolder.pending_images[j]).into(viewHolder.pending_views[j]);
                viewHolder.pending_views[1].setColorFilter(Color.RED);
                viewHolder.pending_views[0].setColorFilter(Color.parseColor("#33b998"));
                //viewHolder.textOrderStatus.setTextColor(Color.RED);

            }
        }

        viewHolder.layoutCancel.setOnClickListener(view -> {

            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Annuler la commande")
                    .setMessage("Voulez vous vraiment annuler la commande?")
                    .setPositiveButton("Oui", (dialogInterface, i1) -> {
                        UserService userService = new UserService(context, fragment);
                        userService.cancelOrder(User.getInstance().getId(), order);
                    })
                    .setNegativeButton("non", (dialogInterface, i1) -> {
                        dialogInterface.dismiss();
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.red_error));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.fragment_profile_orders_item, viewGroup, false);

        return new ViewHolder(v);
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
        @BindView(R.id.textOrderDish)
        TextView textOrderDish;
        @BindView(R.id.textOrderTotal)
        TextView textOrderTotal;
        @BindView(R.id.textOrderStatus)
        TextView textOrderStatus;
        @BindView(R.id.imageRestoLogo)
        ImageView imageViewLogo;
        @BindView(R.id.textRestoName)
        TextView textRestoName;
        @BindView(R.id.pending_status_1)
        ImageView pending_status_1;
        @BindView(R.id.pending_status_2)
        ImageView pending_status_2;
        @BindView(R.id.pending_status_3)
        ImageView pending_status_3;
        @BindView(R.id.pending_status_4)
        ImageView pending_status_4;
        @BindView(R.id.pending_status_5)
        ImageView pending_status_5;
        @BindView(R.id.cause_refuse)
        TextView cause_refuse;
        @BindView(R.id.layout_cancel)
        View layoutCancel;

        ImageView[] pending_views;
        int[] pending_images;
        int status = 0;
        boolean toggle = false;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            pending_views = new ImageView[5];
            pending_images = new int[5];

            pending_views[0] = pending_status_1;
            pending_views[1] = pending_status_2;
            pending_views[2] = pending_status_3;
            pending_views[3] = pending_status_4;
            pending_views[4] = pending_status_5;


            pending_images[0] = R.drawable.delivery_variant_1;
            pending_images[1] = R.drawable.delivery_variant_2;
            pending_images[2] = R.drawable.delivery_variant_3;
            pending_images[3] = R.drawable.delivery_variant_4;
            pending_images[4] = R.drawable.delivery_variant_5;


        }
    }
}

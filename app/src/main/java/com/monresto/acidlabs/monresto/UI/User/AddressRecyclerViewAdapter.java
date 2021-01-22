package com.monresto.acidlabs.monresto.UI.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.Model.User;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<AddressRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Address> addresses;
    private String serviceType;

    public AddressRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_address, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Address address;
        address = addresses.get(i);
        viewHolder.address_title_item.setText(address.getAdresse());
        viewHolder.layoutAddressItem.setOnClickListener(e -> {
            Intent intent = new Intent();
            intent.putExtra("address", address);
            intent.putExtra("serviceType", serviceType);
            Semsem.setLat(address.getLat());
            Semsem.setLon(address.getLon());
            Semsem.locationChanged = true;
            if (User.getInstance() != null)
                User.getInstance().setSelectedAddress(address);
            ((SelectAddressActivity) context).setResult(Activity.RESULT_OK, intent);
            ((SelectAddressActivity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        if (addresses == null)
            return 0;
        return addresses.size();
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.address_title_item)
        TextView address_title_item;
        @BindView(R.id.layoutAddressItem)
        RelativeLayout layoutAddressItem;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivDelete.setVisibility(View.GONE);
        }
    }
}

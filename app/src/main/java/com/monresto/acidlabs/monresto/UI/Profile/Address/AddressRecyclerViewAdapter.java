package com.monresto.acidlabs.monresto.UI.Profile.Address;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<AddressRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Address> addresses;
    private AddressListener listener;

    public AddressRecyclerViewAdapter(Context context, AddressListener listener) {
        this.context = context;
        this.listener = listener;
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
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View d) {
                if (listener != null)
                    listener.deleteAddress(address.getId(), i);
                try {
                    addresses.remove(address);
                    notifyDataSetChanged();
                } catch (Exception ignored) {

                }

            }
        });
        viewHolder.layoutAddressItem.setOnClickListener(e -> {
            Intent intent = new Intent(context, EditAddressActivity.class);
            intent.putExtra("address", address);
            context.startActivity(intent);
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

    public interface AddressListener {
        void deleteAddress(int id, int position);
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
        }
    }
}

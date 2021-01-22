package com.monresto.acidlabs.monresto.UI.Profile.Address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Address;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends BaseAdapter {

    @BindView(R.id.address_title_item)
    TextView address_title_item;
    private ArrayList<Address> mData;

    public AddressAdapter(ArrayList<Address> mData) {
        this.mData = new ArrayList<>();
        if (mData != null)
            this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final View result;

        if (view == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            ButterKnife.bind(this, result);
        } else {
            result = view;
        }

        Address item = (Address) getItem(position);

        address_title_item.setText(item.getAdresse());

        return result;
    }

    public ArrayList<Address> getmData() {
        return mData;
    }

    public void setmData(ArrayList<Address> mData) {
        this.mData = mData;
    }

}

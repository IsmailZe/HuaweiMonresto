package com.monresto.acidlabs.monresto.UI.Main;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.monresto.acidlabs.monresto.Model.Speciality;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecialityRecyclerViewAdapter extends RecyclerView.Adapter<SpecialityRecyclerViewAdapter.ViewHolder> {
    private ArrayList mData;
    private Context context;
    private ViewHolder oldFilter;

    public SpecialityRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_filter, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        viewHolder.filter.setText(((Speciality) mData.get(i)).getTitle());
        viewHolder.filter.setOnClickListener(view -> {
            viewHolder.filter.setBackgroundResource(R.drawable.button_bg_rounded_corners_selected);
            viewHolder.filter.setTextColor(Color.WHITE);


            if (viewHolder.equals(oldFilter)) {
                ((MainActivity) context).resetRecyclerView();
                oldFilter.filter.setBackgroundResource(R.drawable.button_bg_rounded_corners);
                oldFilter.filter.setTextColor(Color.BLACK);
                oldFilter = null;
            } else {
                ((MainActivity) context).searchWithSpeciality((Speciality) mData.get(i));
                if (oldFilter != null) {
                    oldFilter.filter.setBackgroundResource(R.drawable.button_bg_rounded_corners);
                    oldFilter.filter.setTextColor(Color.BLACK);
                }
                oldFilter = viewHolder;
                oldFilter.setIsRecyclable(true);
            }

        });
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public void setFilters(ArrayList<Speciality> specialities) {
        mData = specialities;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.filter)
        Button filter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

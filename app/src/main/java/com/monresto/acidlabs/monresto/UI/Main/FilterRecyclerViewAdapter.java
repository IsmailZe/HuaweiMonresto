package com.monresto.acidlabs.monresto.UI.Main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.monresto.acidlabs.monresto.Model.Filter;
import com.monresto.acidlabs.monresto.Model.Semsem;
import com.monresto.acidlabs.monresto.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Filter> filters;
    private Context context;
    private ViewHolder oldFilter;

    public FilterRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_sort, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        Filter filter = filters.get(i);

        viewHolder.sortName.setText(filter.getTitle());
        viewHolder.sortPic.setImageResource(filter.getIcon());

        switch (filter.getType()) {
            case Semsem.FILTER_NOTE: {
                viewHolder.sortContainer.setOnClickListener(view -> {
                    ((FilterActivity) context).sendFilter(Semsem.FILTER_NOTE);
                });
            }
            break;
            case Semsem.FILTER_OPEN: {
                viewHolder.sortContainer.setOnClickListener(view -> {
                    ((FilterActivity) context).sendFilter(Semsem.FILTER_OPEN);
                });
            }
            break;
            case Semsem.FILTER_TIME: {
                viewHolder.sortContainer.setOnClickListener(view -> {
                    ((FilterActivity) context).sendFilter(Semsem.FILTER_TIME);
                });
            }
            break;
            case Semsem.FILTER_PROMO: {
                viewHolder.sortContainer.setOnClickListener(view -> {
                    ((FilterActivity) context).sendFilter(Semsem.FILTER_PROMO);
                });
            }
            break;
        }
    }

    @Override
    public int getItemCount() {
        if (filters == null)
            return 0;
        return filters.size();
    }

    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sortName)
        TextView sortName;
        @BindView(R.id.sortPic)
        ImageView sortPic;
        @BindView(R.id.sortContainer)
        ConstraintLayout sortContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

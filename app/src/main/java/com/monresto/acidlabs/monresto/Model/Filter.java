package com.monresto.acidlabs.monresto.Model;

import com.monresto.acidlabs.monresto.R;

import java.io.Serializable;
import java.util.ArrayList;

public class Filter implements Serializable {
    private static ArrayList<Filter> filters;
    private String title;
    private int icon;
    private int type;

    private Filter(String title, int icon, int type) {
        this.title = title;
        this.icon = icon;
        this.type = type;
    }

    public static ArrayList<Filter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<>();
            filters.add(new Filter("Temps de livraison", R.drawable.time, Semsem.FILTER_TIME));
            filters.add(new Filter("Promotion", R.drawable.prom, Semsem.FILTER_PROMO));
            filters.add(new Filter("Note", R.drawable.rate, Semsem.FILTER_NOTE));
            filters.add(new Filter("Ouvert", R.drawable.open, Semsem.FILTER_OPEN));
        }
        return filters;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }
}

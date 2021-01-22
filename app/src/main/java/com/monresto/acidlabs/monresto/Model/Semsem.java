package com.monresto.acidlabs.monresto.Model;

import java.util.ArrayList;

public class Semsem {
    public final static int FILTER_PRICE = 1;
    public final static int FILTER_OPEN = 2;
    public final static int FILTER_PROMO = 3;
    public final static int FILTER_TIME = 4;
    public final static int FILTER_NOTE = 5;
    public static boolean locationChanged;
    public static boolean loginPending = false;
    private static double lat = 0;
    private static double lon = 0;
    private static Semsem instance;
    private ArrayList<Restaurant> restaurants;
    private Address address;

    public static Semsem getInstance() {
        if (instance == null)
            instance = new Semsem();
        return instance;
    }

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        Semsem.lat = lat;
    }

    public static double getLon() {
        return lon;
    }

    public static void setLon(double lon) {
        Semsem.lon = lon;
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public Restaurant findRestaurant(int id) {
        for (Restaurant R : restaurants) {
            if (R.getId() == id)
                return R;
        }
        return null;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

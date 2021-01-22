package com.monresto.acidlabs.monresto.Model;

import java.util.ArrayList;

public class City {
    private int id;
    private String name;
    private ArrayList<Zone> zones;

    public City(int id, String name, ArrayList<Zone> zones) {
        this.id = id;
        this.name = name;
        this.zones = zones;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public class Zone {

    }
}

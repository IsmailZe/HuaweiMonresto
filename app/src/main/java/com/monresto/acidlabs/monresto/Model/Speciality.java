package com.monresto.acidlabs.monresto.Model;

import java.io.Serializable;

public class Speciality implements Serializable {
    private int id;
    private String title;

    public Speciality(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}

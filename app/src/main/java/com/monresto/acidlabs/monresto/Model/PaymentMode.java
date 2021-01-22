package com.monresto.acidlabs.monresto.Model;

import java.io.Serializable;

public class PaymentMode implements Serializable {
    private int id;
    private String title;
    private String image;

    public PaymentMode(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
}
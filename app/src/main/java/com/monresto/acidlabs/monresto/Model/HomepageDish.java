package com.monresto.acidlabs.monresto.Model;

public class HomepageDish extends HomepageEvent {
    private int dishID;

    public HomepageDish(int id, String label, String title, String image, int restoID, int dishID, String display_date, String created_at, String updated_at) {
        super(id, label, title, image, restoID, null, display_date, created_at, updated_at);
        this.dishID = dishID;
    }

    public int getDishID() {
        return dishID;
    }

    public void setDishID(int dishID) {
        this.dishID = dishID;
    }
}

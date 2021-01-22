package com.monresto.acidlabs.monresto.Model;

public class HomepageEvent {
    private int id;
    private String label;
    private String title;
    private String image;
    private int restoID;
    private String restoIcon;
    private String display_date;
    private String created_at;
    private String updated_at;

    public HomepageEvent(int id, String label, String title, String image, int restoID, String restoIcon, String display_date, String created_at, String updated_at) {
        this.id = id;
        this.label = label;
        this.title = title;
        this.image = image;
        this.restoID = restoID;
        this.restoIcon = restoIcon;
        this.display_date = display_date;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRestoID() {
        return restoID;
    }

    public void setRestoID(int restoID) {
        this.restoID = restoID;
    }

    public String getDisplay_date() {
        return display_date;
    }

    public void setDisplay_date(String display_date) {
        this.display_date = display_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getRestoIcon() {
        return restoIcon;
    }

    public void setRestoIcon(String restoIcon) {
        this.restoIcon = restoIcon;
    }
}

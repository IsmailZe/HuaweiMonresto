package com.monresto.acidlabs.monresto.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu {
    private int id;
    private String title;
    private String description;
    private int count;
    private String image;

    private Menu(int id, String title, String description, int count, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.count = count;
        this.image = image;
    }

    public static Menu createFromJson(JSONObject obj) {
        return new Menu(obj.optInt("menuID"), obj.optString("title"), obj.optString("description"),
                obj.optInt("piecesCount"), obj.optString("imagePath"));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public String getImage() {
        return image;
    }
}

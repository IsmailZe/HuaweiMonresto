package com.monresto.acidlabs.monresto.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Dish implements Parcelable, Cloneable, Serializable {
    // This is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };
    ArrayList<Option> dimensions;
    ArrayList<Component> components;
    private int id;
    private String title;
    private String description;
    private double price;
    private String promotion;
    private String tva;
    private boolean isOrdered;
    private boolean isFavorite;
    private boolean isComposed;
    private String imagePath;
    private int restoID;
    private boolean isRestoOpen = true;
    private String optionsHash;
    private boolean isBay;
    private String ingredient;
    private String restoname;
    private String restoimage;
    private JSONArray paymentmethode;
    private int quantity;
    private boolean isSnackSelected;

    public Dish(int id, String name, int quantity) {
        this.id = id;
        this.title = name;
        this.quantity = quantity;
    }

    private Dish(int id, int restoID, String title, String description, double price, String promotion, String tva, boolean isOrdered, boolean isFavorite, boolean isComposed, String imagePath) {
        this.id = id;
        this.restoID = restoID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.promotion = promotion;
        this.tva = tva;
        this.isOrdered = isOrdered;
        this.isFavorite = isFavorite;
        this.isComposed = isComposed;
        this.imagePath = imagePath;

        if (isComposed) {
            dimensions = new ArrayList<>();
            components = new ArrayList<>();
        }
    }

    // Constructor that takes a Parcel and gives you an object populated with it's values
    private Dish(Parcel in) {
        id = in.readInt();
        restoID = in.readInt();
        title = in.readString();
        description = in.readString();
        price = in.readDouble();
        promotion = in.readString();
        tva = in.readString();
        isOrdered = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        isComposed = in.readByte() != 0;
        imagePath = in.readString();
        dimensions = in.readArrayList(Option.class.getClassLoader());
        components = in.readArrayList(Component.class.getClassLoader());
    }

    public static Dish getOrderedDish(int id, String name, int quantity) {
        return new Dish(id, name, quantity);
    }

    /*public static Dish Copy(Dish dish) {
        return new Dish(dish.id, dish.restoID, dish.title, dish.description, dish.price, dish.promotion, dish.tva, dish.isOrdered, dish.isFavorite, dish.isComposed, dish.imagePath);
    }*/

    public static Dish createFromJson(JSONObject obj) {
        return new Dish(obj.optInt("dishID"), obj.optInt("restoID"), obj.optString("title"), obj.optString("description"),
                obj.optDouble("price"), obj.optString("promotion"), obj.optString("tva"),
                obj.optInt("isOrdered") != 0, obj.optInt("isFavorite") != 0, obj.optInt("isComposed") != 0,
                obj.optString("imagePath"));
    }

    public Dish clone() throws CloneNotSupportedException {
        return (Dish) super.clone();
    }

    public boolean isSnackSelected() {
        return isSnackSelected;
    }

    public void setSnackSelected(boolean snackSelected) {
        isSnackSelected = snackSelected;
    }

    public int getId() {
        return id;
    }

    public int getRestoID() {
        return restoID;
    }

    public void setRestoID(int restoID) {
        this.restoID = restoID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getPromotion() {
        return promotion;
    }

    public String getTva() {
        return tva;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isComposed() {
        return isComposed;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void addDimension(int id, String title, double price) {
        dimensions.add(new Option(id, title, price));
    }

    public boolean isRestoOpen() {
        return isRestoOpen;
    }

    public void setRestoOpen(boolean restoOpen) {
        isRestoOpen = restoOpen;
    }

    public void addComponent(int componentID, String name, int numberChoice, int numberChoiceMax, ArrayList<Option> options) {
        components.add(new Component(componentID, name, numberChoice, numberChoiceMax, options));
    }

    public ArrayList<Option> getDimensions() {
        return dimensions;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public String getOptionsHash() {
        return optionsHash;
    }

    public void setOptionsHash(String optionsHash) {
        this.optionsHash = optionsHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creates a parcel to be passed through Intents
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(restoID);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeDouble(price);
        parcel.writeString(promotion);
        parcel.writeString(tva);
        parcel.writeByte((byte) (isOrdered ? 1 : 0));
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
        parcel.writeByte((byte) (isComposed ? 1 : 0));
        parcel.writeString(imagePath);
        parcel.writeList(dimensions);
        parcel.writeList(components);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && obj instanceof Dish && ((Dish) obj).getId() == id && ((Dish) obj).getOptionsHash().equals(optionsHash);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return 67 * hash + id;
    }

    public static class Option implements Serializable {
        private int id;
        private String title;
        private double price;

        public Option(int id, String title, double price) {
            this.id = id;
            this.title = title;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public double getPrice() {
            return price;
        }
    }

    public static class Component implements Serializable {
        private int id;
        private String name;
        private int numberChoice;
        private int numberChoiceMax;


        private ArrayList<Option> options;

        public Component(int id, String name, int numberChoice, int numberChoiceMax, ArrayList<Option> options) {
            this.id = id;
            this.name = name;
            this.numberChoice = numberChoice;
            this.numberChoiceMax = numberChoiceMax;
            this.options = options;
        }


        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getNumberChoice() {
            return numberChoice;
        }

        public int getNumberChoiceMax() {
            return numberChoiceMax;
        }

        public ArrayList<Option> getOptions() {
            return options;
        }
    }

}

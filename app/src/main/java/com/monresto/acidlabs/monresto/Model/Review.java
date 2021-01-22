package com.monresto.acidlabs.monresto.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Review implements Parcelable {
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    private int orderID;
    private String clientname;
    private String review;
    private String reviewdate;
    private String orderdate;
    private int note;
    private ArrayList<Dish> dishes;

    private Review(int orderID, String clientname, String review, String reviewdate, String orderdate, int note, ArrayList<Dish> dishes) {
        this.orderID = orderID;
        this.clientname = clientname;
        this.review = review;
        this.reviewdate = reviewdate;
        this.orderdate = orderdate;
        this.note = note;
        this.dishes = dishes;
    }

    protected Review(Parcel in) {
        orderID = in.readInt();
        clientname = in.readString();
        review = in.readString();
        reviewdate = in.readString();
        orderdate = in.readString();
        note = in.readInt();
    }

    public static Review createFromJson(JSONObject obj) {
        ArrayList<Dish> dishes = new ArrayList<>();
        try {
            JSONArray orderedDishes = obj.getJSONArray("OrderedDishes");

            for (int i = 0; i < orderedDishes.length(); i++) {
                JSONObject dish = orderedDishes.getJSONObject(i);
                dishes.add(Dish.createFromJson(dish));
            }
            return new Review(Integer.valueOf(obj.getString("orderID")), obj.getString("firstname") + " " + obj.getString("lastname"), obj.getString("verbatim"), obj.getString("reviewDate"), obj.getString("orderDate"), obj.getInt("globalNote"), dishes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getClientname() {
        return clientname;
    }

    public String getReview() {
        return review;
    }

    public String getReviewdate() {
        return reviewdate;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public int getNote() {
        return note;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(orderID);
        parcel.writeString(clientname);
        parcel.writeString(review);
        parcel.writeString(reviewdate);
        parcel.writeString(orderdate);
        parcel.writeInt(note);
    }
}

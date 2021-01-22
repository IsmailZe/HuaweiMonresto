package com.monresto.acidlabs.monresto.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Restaurant implements Parcelable {
    // This is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
    private boolean isOpenTomorrow;
    private int id;
    private double latitude;
    private double longitude;
    private double distance;
    private Boolean isNew;
    private String name;
    private String description;
    private int estimatedTime;
    private String image;
    private String background;
    private double minimalPrice;
    private double deliveryCost;
    private String phone;
    private double rate;
    private String state;
    private int withPromotion;
    private String openTime;
    private String openDay;
    private String beginMorning;
    private String endMorning;
    private String beginNight;
    private String endNight;
    private int nbrAvis;
    private ArrayList<Speciality> specialities;
    private ArrayList<PaymentMode> paymentModes;

    public Restaurant(int id, double latitude, double longitude, double distance, Boolean isNew, String name, String description, int estimatedTime, String image,
                      String background, double minimalPrice, double deliveryCost, String phone, double rate, String state, int withPromotion, String openTime,
                      String openDay, String beginMorning, String endMorning, String beginNight, String endNight, boolean isOpenTomorrow, int nbrAvis, ArrayList<Speciality> specialities,
                      ArrayList<PaymentMode> paymentModes) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.isNew = isNew;
        this.name = name;
        this.description = description;
        this.estimatedTime = estimatedTime;
        this.image = image;
        this.background = background;
        this.minimalPrice = minimalPrice;
        this.deliveryCost = deliveryCost;
        this.phone = phone;
        this.rate = rate;
        this.state = state;
        this.withPromotion = withPromotion;
        this.openTime = openTime;
        this.openDay = openDay;
        this.beginMorning = beginMorning;
        this.endMorning = endMorning;
        this.beginNight = beginNight;
        this.endNight = endNight;
        this.isOpenTomorrow = isOpenTomorrow;
        this.nbrAvis = nbrAvis;
        this.specialities = specialities;
        this.paymentModes = paymentModes;
        /*this.beginMorning = beginMorning;
        this.endMorning = endMorning;
        this.beginNight = beginNight;
        this.endNight = endNight;*/
    }

    public Restaurant(int id, String name, String background) {
        this.id = id;
        this.name = name;
        this.background = background;
    }

    // This constructor is for testing purposes, will be using first constructor instead.
    public Restaurant(int id, String name, String background, String state, int nbrAvis) {
        this.id = id;
        this.name = name;
        this.background = background;
        this.state = state;
        this.nbrAvis = nbrAvis;
    }

    public Restaurant() {

    }

    // Constructor that takes a Parcel and gives you an object populated with it's values
    private Restaurant(Parcel in) {
        id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        distance = in.readDouble();
        isNew = in.readByte() != 0;
        name = in.readString();
        description = in.readString();
        estimatedTime = in.readInt();
        image = in.readString();
        background = in.readString();
        minimalPrice = in.readDouble();
        deliveryCost = in.readDouble();
        phone = in.readString();
        rate = in.readDouble();
        state = in.readString();
        withPromotion = in.readInt();
        openTime = in.readString();
        openDay = in.readString();
        beginMorning = in.readString();
        endMorning = in.readString();
        beginNight = in.readString();
        endNight = in.readString();
        isOpenTomorrow = in.readByte() != 0;
        nbrAvis = in.readInt();
        specialities = in.readArrayList(Speciality.class.getClassLoader());
        paymentModes = in.readArrayList(PaymentMode.class.getClassLoader());
    }

    public static Restaurant createFromJson(JSONObject obj) {
        ArrayList<Speciality> S = new ArrayList<>();
        ArrayList<PaymentMode> P = new ArrayList<>();
        try {
            JSONArray specs = obj.getJSONArray("Specialities");
            JSONArray payms = obj.getJSONArray("PaymentModes");
            for (int i = 0; i < specs.length(); i++) {
                JSONObject spec = specs.getJSONObject(i);
                S.add(new Speciality(Integer.valueOf(spec.optString("specialityID")), spec.optString("specialityTitle")));
            }
            for (int i = 0; i < payms.length(); i++) {
                JSONObject paym = payms.getJSONObject(i);
                P.add(new PaymentMode(Integer.valueOf(paym.optString("paymentModeID")), paym.optString("paymentModeTitle"), paym.optString("image")));
            }
            return new Restaurant(obj.optInt("restoID"), obj.optDouble("latitude"),
                    obj.optDouble("longitude"), obj.optDouble("distance"),
                    obj.optBoolean("isNewResto"), obj.optString("name"), obj.optString("description"),
                    obj.optInt("estimation"), obj.optString("imagePath"), obj.optString("imagePath2"),
                    obj.optDouble("minimalPrice"), obj.optDouble("deliveryPrice", obj.optDouble("deliveryCost")),
                    obj.optString("mobile"), obj.optDouble("rate"), obj.optString("state"),
                    obj.optInt("withPromotion"), obj.optString("openTime"), obj.optString("openDay"),
                    obj.optString("beginMorning"), obj.optString("endMorning"), obj.optString("beginNight"), obj.optString("endNight"),
                    obj.optBoolean("tomorrow_open"),
                    obj.optInt("reviewsNumber"), S, P
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Restaurant> sort(ArrayList<Restaurant> restaurants, Comparator<? super Restaurant> c) {
        ArrayList<Restaurant> sort_restaurants = new ArrayList<>(restaurants);
        //Collections.sort(sort_restaurants, (e1, e2) -> e1.getRate() > e2.getRate() ? -1 : 1);
        Collections.sort(sort_restaurants, c);
        return sort_restaurants;
    }

    public static ArrayList<Restaurant> sortByTime(ArrayList<Restaurant> restaurants) {
        ArrayList<Restaurant> sort_restaurants = new ArrayList<>(restaurants);

        boolean sort = false;
        Restaurant temp;

        while (!sort) {
            for (int i = 0; i < sort_restaurants.size() - 1; i++) {
                if (sort_restaurants.get(i).getEstimatedTime() > sort_restaurants.get(i + 1).getEstimatedTime()) {
                    temp = sort_restaurants.get(i);
                    sort_restaurants.set(i, sort_restaurants.get(i + 1));
                    sort_restaurants.set(i + 1, temp);
                    sort = true;
                }
            }
        }

        return sort_restaurants;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public Boolean getNew() {
        return isNew;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public String getImage() {
        return image;
    }

    public String getBackground() {
        return background;
    }

    public double getMinimalPrice() {
        return minimalPrice;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public String getPhone() {
        return phone;
    }

    public double getRate() {
        return rate;
    }

    public String getState() {
        return state;
    }

    public int getWithPromotion() {
        return withPromotion;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getOpenDay() {
        return openDay;
    }

    public String getBeginMorning() {
        return beginMorning;
    }

    public String getEndMorning() {
        return endMorning;
    }

    public String getBeginNight() {
        return beginNight;
    }

    public String getEndNight() {
        return endNight;
    }

    public int getNbrAvis() {
        return nbrAvis;
    }

    public ArrayList<Speciality> getSpecialities() {
        return specialities;
    }

    public ArrayList<PaymentMode> getPaymentModes() {
        return paymentModes;
    }

    @Override
    public String toString() {
        return "ID: " + id + " - Name: " + name + " - Estimation: " + estimatedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isOpenTomorrow() {
        return isOpenTomorrow;
    }

    // Creates a parcel to be passed through Intents
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(distance);
        parcel.writeByte((byte) (isNew ? 1 : 0));
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(estimatedTime);
        parcel.writeString(image);
        parcel.writeString(background);
        parcel.writeDouble(minimalPrice);
        parcel.writeDouble(deliveryCost);
        parcel.writeString(phone);
        parcel.writeDouble(rate);
        parcel.writeString(state);
        parcel.writeInt(withPromotion);
        parcel.writeString(openTime);
        parcel.writeString(openDay);
        parcel.writeString(beginMorning);
        parcel.writeString(endMorning);
        parcel.writeString(beginNight);
        parcel.writeString(endNight);
        parcel.writeByte((byte) (isOpenTomorrow ? 1 : 0));
        parcel.writeInt(nbrAvis);
        parcel.writeList(specialities);
        parcel.writeList(paymentModes);
    }

    public boolean isOpen(String stringTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date currentTime = simpleDateFormat.parse(stringTime);
            return ((currentTime.after(simpleDateFormat.parse(beginMorning)) && currentTime.before(simpleDateFormat.parse(endMorning))) ||
                    (currentTime.after(simpleDateFormat.parse(beginNight)) && currentTime.before(simpleDateFormat.parse(endNight))));
        } catch (Exception e) {
            return false;
        }
    }

    public String getClosingTime() {
        if (endNight != null && !endNight.equals(""))
            return endNight;
        else return endMorning;
    }
}

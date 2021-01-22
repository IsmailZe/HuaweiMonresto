package com.monresto.acidlabs.monresto.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Address implements Parcelable {
    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
    private int id = -1;
    private double lat;
    private double lon;
    private String emplacement;
    private String adresse;
    private String rue;
    private String rueTransversalle;
    private String appartement;
    private String postalCode;
    private int zoneID;
    private int cityID;
    private String municipality;
    private boolean isDefault = false;

    public Address() {

    }

    public Address(int id, double lat, double lon, String emplacement, String adresse, String rue, String rueTransversalle, String appartement, String postalCode, int zoneID, int cityID, String municipality, boolean isDefault) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.emplacement = emplacement;
        this.adresse = adresse;
        this.rue = rue;
        this.appartement = appartement;
        this.postalCode = postalCode;
        this.zoneID = zoneID;
        this.cityID = cityID;
        this.municipality = municipality;
        this.rueTransversalle = rueTransversalle;
        this.isDefault = isDefault;
    }

    public Address(double lat, double lon, String emplacement, String adresse, String rue, String rueTransversalle, String appartement, String postalCode, int zoneID, int cityID, String municipality) {
        this.lat = lat;
        this.lon = lon;
        this.emplacement = emplacement;
        this.adresse = adresse;
        this.rue = rue;
        this.rueTransversalle = rueTransversalle;
        this.appartement = appartement;
        this.postalCode = postalCode;
        this.zoneID = zoneID;
        this.cityID = cityID;
        this.municipality = municipality;
    }

    protected Address(Parcel in) {
        id = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        emplacement = in.readString();
        adresse = in.readString();
        rue = in.readString();
        rueTransversalle = in.readString();
        appartement = in.readString();
        postalCode = in.readString();
        zoneID = in.readInt();
        cityID = in.readInt();
        municipality = in.readString();
        isDefault = in.readByte() != 0;
    }

    public static ArrayList<Address> makeListFromJson(JSONArray array) {
        System.out.println("array = [" + array.toString() + "]");
        ArrayList<Address> addresses = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                addresses.add(new Address(obj.optInt("adressID"), obj.optDouble("latitude"), obj.optDouble("longitude"),
                        obj.optString("emplacement"), obj.optString("adresse"), obj.optString("rue"),
                        obj.optString("rueTransversalle"), obj.optString("appartement"), obj.optString("codePostale"),
                        obj.optInt("zoneID"), obj.optInt("cityID"), obj.optString("municipalite"), obj.optInt("is_default") == 1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return addresses;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            if (id != -1)
                obj.put("adressID", id);
            obj.put("latitude", String.valueOf(lat));
            obj.put("longitude", String.valueOf(lon));
            obj.put("emplacement", emplacement);
            obj.put("adresse", adresse);
            obj.put("rue", rue);
            obj.put("rueTransversalle", rueTransversalle);
            obj.put("appartement", appartement);
            obj.put("codePostale", String.valueOf(postalCode));
            obj.put("zoneID", String.valueOf(zoneID));
            obj.put("zoneID", String.valueOf(cityID));
            obj.put("municipalite", municipality);
            obj.put("is_default", isDefault ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getRueTransversalle() {
        return rueTransversalle;
    }

    public void setRueTransversalle(String rueTransversalle) {
        this.rueTransversalle = rueTransversalle;
    }

    public String getAppartement() {
        return appartement;
    }

    public void setAppartement(String appartement) {
        this.appartement = appartement;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(emplacement);
        parcel.writeString(adresse);
        parcel.writeString(rue);
        parcel.writeString(rueTransversalle);
        parcel.writeString(appartement);
        parcel.writeString(postalCode);
        parcel.writeInt(zoneID);
        parcel.writeInt(cityID);
        parcel.writeString(municipality);
        parcel.writeByte((byte) (isDefault ? 1 : 0));
    }
}

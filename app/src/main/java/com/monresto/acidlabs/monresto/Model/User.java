package com.monresto.acidlabs.monresto.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private static User instance;

    private int id;
    private String login;
    private String password;
    private String password_confirm;
    private String email;
    private String fname;
    private String lname;
    private String civility;
    private String phone;
    private String mobile;
    private String comment;
    private ArrayList<Address> addresses;
    private Address selectedAddress;

    public User(int id, String email, String fname, String lname, String mobile) {
        this.id = id;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
    }

    public User(int id, String login, String email, String fname, String lname, String civility, String phone, String mobile, String comment, ArrayList<Address> addresses) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.civility = civility;
        this.phone = phone;
        this.mobile = mobile;
        this.comment = comment;
        this.addresses = addresses;
    }

    public static User getInstance() {
        /*if(instance==null){
            instance = new User(57587, "tester@az.er", "Cool", "Tester");
        }*/
        return instance;
    }

    public static User setInstance(User user) {
        instance = user;
        return instance;
    }

    public static JSONObject registerJson(String login, String password, String password_confirm, String email, String fname, String lname,
                                          String civility, String phone, String mobile, String comment, ArrayList<Address> addresses) {

        JSONObject obj = new JSONObject();
        JSONArray addressesArray = new JSONArray();
        for (Address A : addresses) {
            addressesArray.put(A.toJson());
        }

        try {
            obj.put("login", login);
            obj.put("password", password);
            obj.put("confirmPassword", password_confirm);
            obj.put("firstName", fname);
            obj.put("lastName", lname);
            obj.put("civility", String.valueOf(civility));
            obj.put("email", email);
            obj.put("phone", phone);
            obj.put("mobile", mobile);
            obj.put("commentaire", comment);
            obj.put("Address", addressesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject profileJson(User user) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("userID", user.id);
            obj.put("firstName", user.fname);
            obj.put("lastName", user.lname);
            obj.put("email", user.email);
            obj.put("mobile", user.mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirm() {
        return password_confirm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<Address> getAddresses() {
        if (addresses == null)
            addresses = new ArrayList<>();
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    @Override
    public String toString() {
        return id + " - " + email + " " + fname + " " + lname;
    }
}

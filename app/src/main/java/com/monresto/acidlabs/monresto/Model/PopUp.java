package com.monresto.acidlabs.monresto.Model;

import android.graphics.drawable.Drawable;

public class PopUp {

    private Drawable popUpImg;
    private String popUpText;
    private boolean isPay;

    public PopUp(Drawable popUpImg, String popUpText, boolean isPay) {
        this.popUpImg = popUpImg;
        this.popUpText = popUpText;
        this.isPay = isPay;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public Drawable getPopUpImg() {
        return popUpImg;
    }

    public void setPopUpImg(Drawable popUpImg) {
        this.popUpImg = popUpImg;
    }

    public String getPopUpText() {
        return popUpText;
    }

    public void setPopUpText(String popUpText) {
        this.popUpText = popUpText;
    }
}

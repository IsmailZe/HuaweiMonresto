package com.monresto.acidlabs.monresto.Model;

public class PopupInfo {
    private static PopupInfo instance;
    private boolean showed;

    public static PopupInfo getInstance() {
        if (instance == null)
            instance = new PopupInfo();
        return instance;
    }

    public static void setInstance(PopupInfo instance) {
        PopupInfo.instance = instance;
    }

    public boolean isShowed() {
        return showed;
    }

    public void setShowed(boolean showed) {
        this.showed = showed;
    }
}

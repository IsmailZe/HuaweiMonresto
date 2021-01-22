package com.monresto.acidlabs.monresto.UI.Homepage;

public class HomeItem {

    public String name;
    public String indication;
    public String description;
    public String icon;
    public String notif;
    public String serviceType;
    public String clickToAction;
    public int status;

    public HomeItem(String name, String indication, String description, String icon, String notif, String serviceType, String clickToAction, int status) {
        this.name = name;
        this.indication = indication;
        this.description = description;
        this.icon = icon;
        this.notif = notif;
        this.serviceType = serviceType;
        this.clickToAction = clickToAction;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getIndication() {
        return indication;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getNotif() {
        return notif;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getClickToAction() {
        return clickToAction;
    }

    public int getStatus() {
        return status;
    }
}

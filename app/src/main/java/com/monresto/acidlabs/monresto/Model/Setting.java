package com.monresto.acidlabs.monresto.Model;

public class Setting {

    private String id;
    private String label;
    private String valeur;
    private String status;

    public Setting(String id, String label, String valeur, String status) {
        this.id = id;
        this.label = label;
        this.valeur = valeur;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

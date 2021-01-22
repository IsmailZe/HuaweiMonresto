package com.monresto.acidlabs.monresto.Model;

import com.monresto.acidlabs.monresto.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart implements Serializable {
    private static ShoppingCart instance;
    private HashMap<Dish, Options> items;
    private double deliveryCost;
    private int restoID;
    private double minCartTotal;

    private ShoppingCart() {
        items = new HashMap<>();
        restoID = -1;
    }

    public static ShoppingCart getInstance() {
        if (instance == null)
            instance = new ShoppingCart();
        return instance;
    }

    public static void setInstance(ShoppingCart shoppingCart) {
        instance = shoppingCart;
    }

    public static ShoppingCart createInstance() {
        return new ShoppingCart();
    }

    public Map<Dish, Options> getItems() {
        return items;
    }

    public int getCount() {
        return items.size();
    }

    public void addToCart(Dish dish) {
        addToCart(dish, 1, null, null);
    }

    public double getCartSubTotal() {
        double dishPrice = 0, subTotal = 0;
        try {
            if (getItems() != null) {
                try {
                    for (Map.Entry<Dish, Options> entry : getItems().entrySet()) {
                        Dish cle = entry.getKey();
                        Options valeur = entry.getValue();
                        dishPrice = 0;

                        try {
                            if (valeur.getDimension() != null && valeur.getDimension().getPrice() != 0) {
                                dishPrice += valeur.getDimension().getPrice();
                            } else {
                                dishPrice += cle.getPrice();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            for (Dish.Component component : valeur.getComponents()) {
                                for (Dish.Option option : component.getOptions()) {
                                    dishPrice += option.getPrice();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dishPrice *= valeur.quantity;
                        subTotal += dishPrice;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (subTotal < 0) {
            subTotal = 0;
            items.clear();
        }
        return subTotal;
    }

    public double getCartDelivery() {
        if (!ShoppingCart.getInstance().isEmpty() && Semsem.getInstance().getRestaurants() != null && Semsem.getInstance().findRestaurant(ShoppingCart.getInstance().getRestoID()) != null) {
            return Semsem.getInstance().findRestaurant(ShoppingCart.getInstance().getRestoID()).getDeliveryCost();
        }
        if (ShoppingCart.getInstance() != null && !ShoppingCart.getInstance().isEmpty()) {
            return deliveryCost;
        }
        return 0;
    }

    public boolean addToCart(Dish dish, int quantity, Dish.Option dimension, ArrayList<Dish.Component> components) {
        return addToCart(dish, quantity, dimension, components, "");
    }

    public boolean addToCart(Dish dish, int quantity, Dish.Option dimension, ArrayList<Dish.Component> components, String comment) {
        if (restoID == -1) {
            restoID = dish.getRestoID();
            if (Semsem.getInstance().getRestaurants() != null && !Semsem.getInstance().getRestaurants().isEmpty())
                for (Restaurant R : Semsem.getInstance().getRestaurants()) {
                    if (R.getId() == restoID)
                        minCartTotal = R.getMinimalPrice();
                }
        } else if (dish.getRestoID() != restoID) {
            return false;
        }
        Dish addedDish;
        try {
            addedDish = dish.clone();
            StringBuilder compCode = new StringBuilder();
            if (components != null)
                for (Dish.Component c : components) {
                    for (Dish.Option o : c.getOptions())
                        compCode.append(o.getId());
                }
            addedDish.setOptionsHash(Utilities.md5("" + (dimension != null ? String.valueOf(dimension.getId()) : "") + compCode));
            if (items.containsKey(addedDish)) {
                items.get(addedDish).quantity += quantity;
            } else {
                items.put(addedDish, new Options(quantity, dimension, components, comment));
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void removeFromCart(Dish dish) {
        items.remove(dish);
        if (items.isEmpty())
            restoID = -1;
    }

    public int getCurrentRestaurantID() {
        try {
            if (getItems() != null)
                return getItems().keySet().iterator().next().getRestoID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public JSONArray getOrdersJson() {
        JSONArray orders = new JSONArray();
        JSONObject actualItem;
        for (Map.Entry<Dish, Options> item : items.entrySet()) {
            Dish dish = item.getKey();
            Options options = item.getValue();

            actualItem = new JSONObject();
            try {
                actualItem.put("dishID", dish.getId());
                actualItem.put("quantity", options.quantity);
                actualItem.put("dimensionID", options.dimension != null ? String.valueOf(options.dimension.getId()) : "");
                actualItem.put("comment", options.comment);

                if (options.components != null) {
                    JSONArray componentsJson = new JSONArray();

                    JSONObject actualComponent;
                    for (Dish.Component component : options.components) {
                        actualComponent = new JSONObject();
                        actualComponent.put("componentID", component.getId());
                        JSONArray optionsJson = new JSONArray();
                        for (Dish.Option option : component.getOptions()) {
                            JSONObject optionObject = new JSONObject();
                            optionObject.put("optionID", option.getId());
                            optionsJson.put(optionObject);
                        }
                        actualComponent.put("Options", optionsJson);
                        componentsJson.put(actualComponent);
                    }
                    actualItem.put("Components", componentsJson);
                }
                orders.put(actualItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return orders;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        /*Iterator<Map.Entry<Dish, Options>> it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Dish, Options> pair = it.next();
            if (pair.getKey().getRestoID()!=0) it.remove();
        }*/
        items.clear();
        restoID = -1;
    }

    public double getMinCartTotal() {
        return minCartTotal;
    }

    public int getRestoID() {
        return restoID;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public class Options implements Serializable {
        private int quantity;
        private Dish.Option dimension;
        private ArrayList<Dish.Component> components;
        private String comment;

        Options(int quantity, Dish.Option dimension, ArrayList<Dish.Component> components, String comment) {
            this.quantity = quantity;
            this.dimension = dimension;
            this.components = components;
            this.comment = comment;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Dish.Option getDimension() {
            return dimension;
        }

        public ArrayList<Dish.Component> getComponents() {
            return components;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null && obj instanceof Options && ((Options) obj).quantity == this.quantity && ((Options) obj).dimension == this.dimension && ((Options) obj).components == this.components);
        }
    }
}

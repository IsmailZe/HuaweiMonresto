package com.monresto.acidlabs.monresto;

import com.facebook.appevents.AppEventsLogger;

public class Config {
    public static final int REQUEST_CODE_MAP_INFO = 0;
    public static final int REQUEST_PLACE_PICKER = 1;
    public static final int REQUEST_CODE_CHECKOUT = 2;
    public static final int REQUEST_CODE_ADRESS_SELECT = 3;
    public static final int REQUEST_CODE_ASK_FOR_LOCATION = 4;
    public static final int REQUEST_CODE_POSITION_SELECT = 5;
    public static final int REQUEST_CODE_FILTER_SELECT = 6;
    public static final int REQUEST_CODE_ADDRESS_PICK_1 = 7;
    public static final int REQUEST_CODE_ADDRESS_PICK_2 = 8;
    public static final int REQUEST_CODE_PAYMENT = 9;
    public static final String server = "https://www.monresto.net/ws/v3/";
    public static final String acidlabsServer = "https://monresto.acidlabs.co/";
    public static final String sharedKey = "&aAs6w@GVbW52!qo$lm";
    public static AppEventsLogger logger;


}

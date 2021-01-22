package com.monresto.acidlabs.monresto.Service.Homepage;

import com.monresto.acidlabs.monresto.Model.HomepageConfig;
import com.monresto.acidlabs.monresto.Model.HomepageDish;
import com.monresto.acidlabs.monresto.Model.HomepageEvent;
import com.monresto.acidlabs.monresto.Model.Setting;
import com.monresto.acidlabs.monresto.UI.Homepage.HomeItem;

import java.util.ArrayList;

public interface HomepageAsyncResponse {
    void onHomepageConfigReceived(HomepageConfig config);

    void onHomepageEventsReceived(ArrayList<HomepageEvent> events);

    void onHomepageDishesReceived(ArrayList<HomepageDish> dishes);

    void onHomepageSettingsReceived(ArrayList<Setting> settings);

    void onHomepageError(boolean b);

    void onHomeLoaded(ArrayList<HomeItem> listHomeElements, ArrayList<HomeItem> gridHomeElements);
}

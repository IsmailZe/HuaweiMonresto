package com.monresto.acidlabs.monresto;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

import static com.facebook.FacebookSdk.setAdvertiserIDCollectionEnabled;
import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;

public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        setAutoLogAppEventsEnabled(true);
        setAdvertiserIDCollectionEnabled(true);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        Config.logger = AppEventsLogger.newLogger(this);

    }


}

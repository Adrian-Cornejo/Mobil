package com.example.simulascore;

import android.app.Application;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class ApplicationClass extends Application {

    // NOTE: Replace the below with your own ONESIGNAL_APP_ID
    private static final String ONESIGNAL_APP_ID = "c3a65265-1671-45a7-b300-19c373a19d25";

    @Override
    public void onCreate() {
        super.onCreate();

        // Verbose Logging set to help debug issues, remove before releasing your app.
       // OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App com.example.simulascore.MessagesA to prompt instead.
        OneSignal.getNotifications().requestPermission(false, Continue.none());
        //See Android SDK Setup for details https://documentation.onesignal.com/docs/android-sdk-setup
        OneSignal.initWithContext(this);
       // OneSignal.setAppId(ONESIGNAL_APP_ID);




    }
}

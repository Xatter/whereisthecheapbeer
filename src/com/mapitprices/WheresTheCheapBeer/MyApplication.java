package com.mapitprices.WheresTheCheapBeer;

import android.app.Application;
import android.os.Build;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/8/11
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
@ReportsCrashes(formKey = "dHRnSnU0WWFlc1JHT0k2OW1YS0ZTNHc6MQ")
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        if (!"google_sdk".equals(Build.PRODUCT)) {
            ACRA.init(this);
        }

        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.startNewSession("UA-22338335-2", getApplicationContext());
        tracker.trackEvent(
                "Application", //Category
                "Start",       //Action
                "started",     //Label
                0              // Value
        );
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onTerminate() {
        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.stopSession();
        super.onTerminate();    //To change body of overridden methods use File | Settings | File Templates.
    }
}

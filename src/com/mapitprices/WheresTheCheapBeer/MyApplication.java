package com.mapitprices.WheresTheCheapBeer;

import android.app.Application;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/8/11
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
@ReportsCrashes(formKey = "dHRnSnU0WWFlc1JHT0k2OW1YS0ZTNHc6MQ",
                mode = ReportingInteractionMode.TOAST,
                forceCloseDialogAfterToast = false,
                resToastText = R.string.crash_toast_text)
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();

        if (!Constants.DEBUGMODE) {
            ACRA.init(this);
            tracker.startNewSession("UA-22338335-2", getApplicationContext());

            tracker.trackEvent(
                    "Application", //Category
                    "Start",       //Action
                    "started",     //Label
                    0              // Value
            );

        } else {
            tracker.startNewSession("", getApplicationContext());
        }

        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onTerminate() {
        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.stopSession();
        super.onTerminate();    //To change body of overridden methods use File | Settings | File Templates.
    }
}

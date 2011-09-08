package com.mapitprices.WheresTheCheapBeer;

import android.app.Application;
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
        ACRA.init(this);
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }
}

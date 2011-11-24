package com.mapitprices.WheresTheCheapBeer;

import com.mapitprices.Model.Foursquare.Location;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 9/18/11
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    public static final boolean DEBUGMODE = false;
    public static final boolean USE_GPS = true;
    public static final int MIN_DISTANCE = 100; // in meters
    public static final int MIN_TIME = 120000; // 2 minutes in ms
    public static final int APIVERSION = 2;
    public static final int RC_NEW_PRICE = 7;
    public static final android.location.Location DEFAULT_LOCATION = new android.location.Location("MANUAL");

    static {
        DEFAULT_LOCATION.setLongitude(40.75);
        DEFAULT_LOCATION.setLatitude(-73.98);
    }
}

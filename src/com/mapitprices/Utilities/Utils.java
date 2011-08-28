package com.mapitprices.Utilities;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import org.apache.commons.logging.Log;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/20/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static List<NameValuePair> locationToNameValuePair(Location loc) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        if (loc == null) {
            nameValuePairs.add(new BasicNameValuePair("Lat", "40.75"));
            nameValuePairs.add(new BasicNameValuePair("Lng", "-73.98"));
        } else {
            nameValuePairs.add(new BasicNameValuePair("Lat", Double.toString(loc.getLatitude())));
            nameValuePairs.add(new BasicNameValuePair("Lng", Double.toString(loc.getLongitude())));
        }

        return nameValuePairs;
    }

    public static boolean validate_or_rescan_upc(Activity context, String result) {
        int len = result.length();
        if (len != 8 &&
                len != 12 &&
                len != 13 &&
                len != 14) {
            //not enough digits, rescan
            Toast toast = Toast.makeText(context, "Scan didn't get all the digits, please try again.", 2000);
            toast.show();
            IntentIntegrator.initiateScan(context);
            return false;
        }

        return true;
    }

    public static GeoPoint LocationToGeoPoint(Location loc)
    {
        return Utils.LocationToGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static GeoPoint LocationToGeoPoint(double latitude, double longitude)
    {
        return new GeoPoint((int)(latitude * 1e6),
                            (int)(longitude * 1e6));
    }

    public static Location registerListener(Context context, LocationListener listener)
    {
                 // Define a set of criteria used to select a location provider.
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        LocationManager mlocManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        String provider = mlocManager.getBestProvider(criteria, true);

        if (provider != null) {
            Location location = mlocManager.getLastKnownLocation(provider);

            mlocManager.requestLocationUpdates(provider, MapItPricesServer.MIN_TIME,
                    MapItPricesServer.MIN_DISTANCE, listener);

            return location;
        }

        return null;
    }

    public static void unregisterListener(Context context, LocationListener listener)
    {
        LocationManager mlocManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (listener != null) {
            mlocManager.removeUpdates(listener);
        }
    }
}

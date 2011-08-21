package com.mapitprices.Utilities;

import android.location.Location;
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
}

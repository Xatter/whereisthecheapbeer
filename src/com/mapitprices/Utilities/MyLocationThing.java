package com.mapitprices.Utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/25/11
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyLocationThing implements LocationListener {
    private final LocationManager mLocationManager;

    private static Location mLocation;
    private boolean mMyLocationEnabled;
    private LinkedList<Runnable> mRunOnFirstFix = new LinkedList<Runnable>();

    private long mLocationUpdateMinTime = 0;
    private float mLocationUpdateMinDistance = 0.0f;


    public MyLocationThing(final Context ctx) {
        mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLastFix() {
        return mLocation;
    }

    public void disableMyLocation() {
        mLocationManager.removeUpdates(this);
        mMyLocationEnabled = false;
    }

    public boolean enableMyLocation() {
        if (!mMyLocationEnabled) {
            for (final String provider : mLocationManager.getAllProviders()) {
                mLocationManager.requestLocationUpdates(provider, mLocationUpdateMinTime, mLocationUpdateMinDistance, this);
            }
        }

        return mMyLocationEnabled = true;
    }

    /**
     * Return a GeoPoint of the last known location, or null if not known.
     */
    public GeoPoint getMyLocation() {
        if (mLocation == null) {
            return null;
        } else {
            return Utils.LocationToGeoPoint(mLocation);
        }
    }

    public boolean runOnFirstFix(Runnable runnable) {
        if (mMyLocationEnabled) {
            runnable.run();
            return true;
        } else {
            mRunOnFirstFix.addLast(runnable);
            return false;
        }
    }

    // Listener impl
    public void onLocationChanged(final Location location) {
        if (isBetterLocation(location, mLocation)) {
            mLocation = location;
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE) {
            for (Runnable runnable : mRunOnFirstFix) {
                runnable.run();
            }
            mRunOnFirstFix.clear();
        }
    }

    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Determines whether one Location reading is better than the current Location fix  * @param location  The new Location that you want to evaluate  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}

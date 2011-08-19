package com.mapitprices.wheresthecheapbeer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.RestClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/17/11
 * Time: 2:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class NearbyItemsActivity extends Activity {
    private ProgressDialog _progressDialog;
    private Location _currentLocation;

    private final LocationListener currentListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            _progressDialog.show();
            new GetLocationTask().execute(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby prices...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        _progressDialog = progressDialog;
    }

    private Collection<Item> getitems(Location loc) {
        List<NameValuePair> nameValuePairs = locationToNameValuePair(loc);

        String result = RestClient.ExecuteCommand("http://10.0.2.2:61418/Mobile/GetItems", nameValuePairs);

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Item>>() {
        }.getType();
        Collection<Item> items = gson.fromJson(result, collectionType);
        return items;
    }

    private List<NameValuePair> locationToNameValuePair(Location loc)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        nameValuePairs.add(new BasicNameValuePair("Lat", Double.toString(loc.getLatitude())));
        nameValuePairs.add(new BasicNameValuePair("Lng", Double.toString(loc.getLongitude())));

        return nameValuePairs;
    }

    private void registerListener() {
        // Define a set of criteria used to select a location provider.
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = mlocManager.getBestProvider(criteria, true);

        if (provider != null) {
            Location location = mlocManager.getLastKnownLocation(provider);
            _progressDialog.show();
            new GetLocationTask().execute(location);

            mlocManager.requestLocationUpdates(provider, MIN_TIME,
                    MIN_DISTANCE, currentListener);
        }
    }

    private static final int MIN_DISTANCE = 200; // in meters
    private static final int MIN_TIME = 300000; // 5 minutes in ms

    private void unregisterListener() {
        LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (currentListener != null) {
            mlocManager.removeUpdates(currentListener);
        }
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Item>> {
        @Override
        protected Collection<Item> doInBackground(Location... params) {
            Location loc = params[0];
            return getitems(loc);
        }

        @Override
        protected void onPostExecute(Collection<Item> result) {
            _progressDialog.dismiss();
        }
    }
}
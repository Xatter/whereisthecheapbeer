package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.StoreResultAdapter;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectStoreActivity extends ListActivity {
    Store[] _stores;
    private ProgressDialog _progressDialog;
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
    private Location _currentLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stores_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby stores...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        _progressDialog = progressDialog;
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
            _currentLocation = location;
            _progressDialog.show();
            new GetLocationTask().execute(location);

            mlocManager.requestLocationUpdates(provider, MapItPricesServer.MIN_TIME,
                    MapItPricesServer.MIN_DISTANCE, currentListener);
        }
    }

    @Override
    protected void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerListener();
    }

    @Override
    protected void onStop() {
        unregisterListener();
        super.onStop();
    }

    private void unregisterListener() {
        LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (currentListener != null) {
            mlocManager.removeUpdates(currentListener);
        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("store", _stores[position]);
        setResult(RESULT_OK, data);
        finish();
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Store>> {
        @Override
        protected Collection<Store> doInBackground(Location... params) {
            Location loc = params[0];
            return MapItPricesServer.getStoresFromServer(loc);
        }

        @Override
        protected void onPostExecute(Collection<Store> result) {
            _progressDialog.dismiss();
            _stores = result.toArray(new Store[0]);

            if (result != null) {
                ArrayAdapter<Store> adaptor = new StoreResultAdapter(SelectStoreActivity.this, R.id.item_row_name, _stores);
                setListAdapter(adaptor);
            }
        }
    }

}
package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewStoreActivity extends Activity {
    private List<Store> _stores = new ArrayList<Store>();
    private Location _currentLocation;

    private final LocationListener currentListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            _currentLocation = location;
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
        setContentView(R.layout.store_editor);
    }

    public void saveStore(View v)
    {
        if(_currentLocation != null)
        {
            Store s = new Store();
            s.setLocation(_currentLocation);

            EditText et = (EditText) findViewById(R.id.store_name);
            s.setName(et.getText().toString());

            Store returnedStore = MapItPricesServer.createNewStore(s);
            if(returnedStore != null)
            {
                Intent data = new Intent();
                data.putExtra("store", returnedStore);
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else
        {
            Toast.makeText(this, "Couldn't get location of store", Toast.LENGTH_SHORT)
                    .show();
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

            mlocManager.requestLocationUpdates(provider, MapItPricesServer.MIN_TIME,
                    MapItPricesServer.MIN_DISTANCE, currentListener);
        }
    }

    private void unregisterListener() {
        LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (currentListener != null) {
            mlocManager.removeUpdates(currentListener);
        }
    }
}
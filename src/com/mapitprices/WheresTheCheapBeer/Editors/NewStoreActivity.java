package com.mapitprices.WheresTheCheapBeer.Editors;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
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
    GoogleAnalyticsTracker tracker;

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
        tracker = GoogleAnalyticsTracker.getInstance();
    }

    public void saveStore(View v) {
        Store s = new Store();
        EditText et = (EditText) findViewById(R.id.store_name);
        s.setName(et.getText().toString());

        if (_currentLocation != null) {
            s.setLocation(_currentLocation);
        } else {
            Toast.makeText(this, "Couldn't get location of store", Toast.LENGTH_SHORT)
                    .show();
        }

        Store returnedStore = MapItPricesServer.createNewStore(s);

        if (returnedStore != null) {
            Intent data = new Intent();
            data.putExtra("store", returnedStore);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, "Store didn't save for some reason", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    protected void onPause() {
        Utils.unregisterListener(this, currentListener);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onStop() {
        Utils.unregisterListener(this, currentListener);
        super.onStop();
    }
}
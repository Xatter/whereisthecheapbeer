package com.mapitprices.WhereIsTheCheapBeer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/17/11
 * Time: 2:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class NearbyItemsActivity extends ListActivity {
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
        setContentView(R.layout.items_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby prices...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        _progressDialog = progressDialog;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_list_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_price:
                Intent i = new Intent().setClass(NearbyItemsActivity.this,ReportPriceActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_scan_barcode:
                IntentIntegrator.initiateScan(NearbyItemsActivity.this);
                return true;
            case R.id.menu_map_items:
                return true;
            case R.id.menu_item_refresh:
                _progressDialog.show();
                new GetLocationTask().execute(_currentLocation);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
	    if (scanResult != null) {
	    	String result = scanResult.getContents();
	    	if (result != null)
	    	{
                int len = result.length();
                if (len != 8 ||
                        len != 12 ||
                        len != 13 ||
                        len != 14)
                {
                  //not enough digits, rescan
                    Toast toast = Toast.makeText(this,"Scan didn't get all the digits, please try again.", 2000);
                    toast.show();
                    IntentIntegrator.initiateScan(NearbyItemsActivity.this);
                }

				Bundle bundle = new Bundle();
				bundle.putString("barcode", result);

				Intent searchResults = new Intent().setClass(this, BarCodeScanItemActivity.class);
				searchResults.putExtras(bundle);
				startActivity(searchResults);
	    	}
	    }
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
            return MapItPricesServer.getNearbyPrices(loc);
        }

        @Override
        protected void onPostExecute(Collection<Item> result) {
            _progressDialog.dismiss();

            if (result != null) {
                ArrayAdapter<Item> adaptor = new ItemResultAdapter(NearbyItemsActivity.this, R.id.item_row_name, result.toArray(new Item[0]));
                setListAdapter(adaptor);
            }
        }
    }


}
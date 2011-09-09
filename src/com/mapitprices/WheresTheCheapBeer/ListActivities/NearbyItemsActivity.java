package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;
import com.mapitprices.WheresTheCheapBeer.Editors.ReportPriceActivity;
import com.mapitprices.WheresTheCheapBeer.BarCodeScanItemActivity;
import com.mapitprices.WheresTheCheapBeer.MapActivities.BeerMapActivity;
import com.mapitprices.WheresTheCheapBeer.SearchActivity;
import com.mapitprices.WheresTheCheapBeer.SettingsActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/17/11
 * Time: 2:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class NearbyItemsActivity extends ListActivity {
    private Location _currentLocation;
    ArrayList<Item> mCachedItems = new ArrayList<Item>();

                                    GoogleAnalyticsTracker tracker;

    private final LocationListener currentListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            new GetLocationTask().execute(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private ArrayAdapter<Item> mAdaptor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        mAdaptor = new ItemResultAdapter(NearbyItemsActivity.this, R.id.item_row_name, mCachedItems);
        setListAdapter(mAdaptor);

        if (mCachedItems.size() == 0) {
            new GetLocationTask().execute(_currentLocation);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent().setClass(this, BeerMapActivity.class);
        i.putExtra("item", mCachedItems.get(position));
        startActivity(i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_price_list_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.menu_new_price:
                i = new Intent().setClass(this, ReportPriceActivity.class);
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_scan_barcode:
                IntentIntegrator.initiateScan(this);
                return true;
            case R.id.menu_map_items:
                Intent mapIntent = new Intent().setClass(this, BeerMapActivity.class);
                startActivity(mapIntent);
                return true;
            case R.id.menu_item_refresh:
                new GetLocationTask().execute(_currentLocation);
                return true;
            case R.id.menu_item_settings:
                i = new Intent().setClass(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (result != null) {
                if (Utils.validate_or_rescan_upc(this, result)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("upc", result);

                    Intent searchResults = new Intent().setClass(this, BarCodeScanItemActivity.class);
                    searchResults.putExtras(bundle);
                    startActivity(searchResults);
                }
            }
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            Item i = data.getParcelableExtra("item");
            mCachedItems.add(i);
            mAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.ITEM_SEARCH, true);
        appData.putParcelableArrayList("items", mCachedItems);
        startSearch(null, false, appData, false);
        return true;
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Item>> {
        ProgressDialog _progressDialog;

        GetLocationTask()
        {
            _progressDialog = Utils.createProgressDialog(NearbyItemsActivity.this,"Getting nearby prices...");
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected Collection<Item> doInBackground(Location... params) {
            Location loc = params[0];
            _currentLocation = loc;
            return MapItPricesServer.getNearbyPrices(loc, tracker);
        }

        @Override
        protected void onPostExecute(Collection<Item> result) {
            _progressDialog.dismiss();

            if (result != null) {
                mCachedItems.clear();
                mCachedItems.addAll(result);

                mAdaptor.notifyDataSetChanged();
            }
        }
    }


}
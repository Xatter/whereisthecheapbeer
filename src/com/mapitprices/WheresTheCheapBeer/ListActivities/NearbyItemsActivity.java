package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.MyLocationThing;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.BarCodeScanItemActivity;
import com.mapitprices.WheresTheCheapBeer.Editors.ReportPriceActivity;
import com.mapitprices.WheresTheCheapBeer.MapActivities.BeerMapActivity;
import com.mapitprices.WheresTheCheapBeer.R;
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

    ArrayList<Item> mCachedItems = new ArrayList<Item>();

    private static final int RC_NEW_PRICE = 0;
    private static final int RC_UPDATE_ITEM_PRICE = 1;

    private GoogleAnalyticsTracker tracker;

    private MyLocationThing mLocationThing;
    private ArrayAdapter<Item> mAdaptor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        tracker = GoogleAnalyticsTracker.getInstance();
        mLocationThing = new MyLocationThing(this);
        mLocationThing.enableMyLocation();

        mAdaptor = new ItemResultAdapter(NearbyItemsActivity.this, R.id.item_row_name, mCachedItems);
        setListAdapter(mAdaptor);

        if (mCachedItems.size() == 0) {
            new GetLocationTask().execute(mLocationThing.getLastFix());
        }

        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent().setClass(this, BeerMapActivity.class);
        Item clickedItem = mCachedItems.get(position);
        i.putExtra("item", clickedItem);
        tracker.trackEvent("Click", "Item", clickedItem.getName(), clickedItem.getItemID());
        startActivity(i);
    }

    @Override
    protected void onPause() {
        mLocationThing.disableMyLocation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationThing.enableMyLocation();
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
                startActivityForResult(i, RC_NEW_PRICE);
                return true;
            case R.id.menu_scan_barcode:
                IntentIntegrator.initiateScan(this);
                return true;
            case R.id.menu_item_refresh:
                new GetLocationTask().execute(mLocationThing.getLastFix());
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_selected_item_price:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Item storeItem = mCachedItems.get(info.position);

                Intent i = new Intent().setClass(this, ReportPriceActivity.class);
                i.putExtra("item", storeItem);
                startActivityForResult(i, RC_UPDATE_ITEM_PRICE);
                break;
        }
        return true;
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
        } else if (requestCode == RC_NEW_PRICE && resultCode == RESULT_OK) {
            Item i = data.getParcelableExtra("item");
            mCachedItems.add(i);
            mAdaptor.notifyDataSetChanged();
        } else if (requestCode == RC_UPDATE_ITEM_PRICE && resultCode == RESULT_OK) {
            Item updatedItem = data.getParcelableExtra("item");

            for (Item item : mCachedItems) {
                if (item.getItemID() == updatedItem.getItemID()) {
                    item.setPrice(updatedItem.getPrice());
                    item.setUser(User.getInstance());
                    mAdaptor.notifyDataSetChanged();
                    break;
                }
            }
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

        GetLocationTask() {
            _progressDialog = Utils.createProgressDialog(NearbyItemsActivity.this, "Getting nearby prices...");
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected Collection<Item> doInBackground(Location... params) {
            Location loc = params[0];
            return MapItPricesServer.getNearbyPrices(loc);
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
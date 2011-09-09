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
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.StoreResultAdapter;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;
import com.mapitprices.WheresTheCheapBeer.Editors.NewStoreActivity;
import com.mapitprices.WheresTheCheapBeer.SearchActivity;
import com.mapitprices.WheresTheCheapBeer.SettingsActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/17/11
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class NearbyStoresActivity extends ListActivity {
    private Location mCurrentLocation;
    private ArrayList<Store> mStoresCache = new ArrayList<Store>();
    private ArrayAdapter<Store> mAdapter;

    private final LocationListener mCurrentListener = new LocationListener() {

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

    GoogleAnalyticsTracker tracker;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stores_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        mAdapter = new StoreResultAdapter(NearbyStoresActivity.this, R.id.item_row_name, mStoresCache);
        setListAdapter(mAdapter);

        if(mStoresCache.size() == 0)
        {
            new GetLocationTask().execute(mCurrentLocation);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.store_list_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_store:
                Intent i = new Intent().setClass(this, NewStoreActivity.class);
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_store_refresh:
                new GetLocationTask().execute(mCurrentLocation);
                return true;
            case R.id.menu_item_settings:
                i = new Intent().setClass(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Store s = data.getParcelableExtra("store");
            mStoresCache.add(s);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent().setClass(this, StoreItemsActivity.class);
        i.putExtra("store", mStoresCache.get(position));
        startActivity(i);
    }

    @Override
    protected void onPause() {
        Utils.unregisterListener(this, mCurrentListener);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurrentLocation = Utils.registerListener(this, mCurrentListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentLocation = Utils.registerListener(this, mCurrentListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentLocation = Utils.registerListener(this, mCurrentListener);
    }

    @Override
    protected void onStop() {
        Utils.unregisterListener(this, mCurrentListener);
        super.onStop();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.STORE_SEARCH, true);
        appData.putParcelableArrayList("stores", mStoresCache);
        startSearch(null, false, appData, false);
        return true;
    }


    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Store>> {
        ProgressDialog mProgressDialog;
        GetLocationTask()
        {
            mProgressDialog = Utils.createProgressDialog(NearbyStoresActivity.this, "Getting nearby stores...");
        }
        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Collection<Store> doInBackground(Location... params) {
            Location loc = params[0];
            mCurrentLocation = loc;
            return MapItPricesServer.getNearbyStoresWithPricesFromServer(loc, tracker);
        }

        @Override
        protected void onPostExecute(Collection<Store> result) {

            if (result != null) {
                mStoresCache.clear();
                mStoresCache.addAll(result);

                mAdapter.notifyDataSetChanged();
            }

            mProgressDialog.dismiss();
        }
    }
}
package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Foursquare.FoursquareResponse;
import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.FoursquareServer;
import com.mapitprices.Utilities.MyLocationThing;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.Utilities.VenueResultAdapter;
import com.mapitprices.WheresTheCheapBeer.R;
import com.mapitprices.WheresTheCheapBeer.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectStoreActivity extends ListActivity {
    public static final int ADD_STORE_REQUEST = 0;
    public static final int STORE_SEARCH = 1;
    List<Venue> _stores = new ArrayList<Venue>();
    GoogleAnalyticsTracker tracker;
    private MyLocationThing mLocationThing;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_store_layout);

        tracker = GoogleAnalyticsTracker.getInstance();
        mLocationThing = new MyLocationThing(this);

        if (mLocationThing.getLastFix() == null) {
            mLocationThing.runOnFirstFix(new Runnable() {
                public void run() {
                    new GetLocationTask().execute(mLocationThing.getLastFix());
                }
            });
        } else {
            new GetLocationTask().execute(mLocationThing.getLastFix());
        }

        mLocationThing.enableMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.store_list_option_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_STORE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Store store = data.getParcelableExtra("store");
                if (store != null) {
                    Intent returnData = new Intent();
                    returnData.putExtra("store", store);
                    setResult(RESULT_OK, returnData);
                    finish();
                }
            }
        } else if (requestCode == STORE_SEARCH && resultCode == RESULT_OK) {
            Venue venue = data.getParcelableExtra("venue");
            Intent returnData = new Intent();
            returnData.putExtra("venue", venue);
            setResult(RESULT_OK, returnData);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_store_refresh:
                new GetLocationTask().execute(mLocationThing.getLastFix());
                break;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                new SearchVenuesTask().execute(query);
            }
        }

    }

    private Venue[] doVenueSearch(String query) {

        android.location.Location location = mLocationThing.getLastFix();

        tracker.trackEvent(
                "Search",
                "StoreSearch",
                query,
                0);

        if (location != null) {
            FoursquareResponse response = FoursquareServer.getVenues(location.getLatitude(), location.getLongitude(), query);

            if (response.meta.code.equals("200")) {
                return response.response.venues;
            } else if (response.meta.code.equals("500")) {
                Toast.makeText(this, response.meta.errorType, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Can't get locations near you without a GPS fix. Please enable GPS and try again.", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.STORE_SELECT_SEARCH, true);
        startSearch(null, false, appData, false);
        return true;
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("venue", _stores.get(position));
        setResult(RESULT_OK, data);
        finish();
    }

    private class SearchVenuesTask extends AsyncTask<String, Void, Venue[]> {
        private ProgressDialog mProgressDialog;

        SearchVenuesTask() {
            mProgressDialog = Utils.createProgressDialog(SelectStoreActivity.this, "Searching for venue...");
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Venue[] doInBackground(String... params) {
            String query = params[0];
            return doVenueSearch(query);

        }

        @Override
        protected void onPostExecute(Venue[] results) {
            mProgressDialog.dismiss();
            if (results != null) {
                _stores.clear();
                _stores.addAll(Arrays.asList(results));

                ArrayAdapter<Venue> adaptor = new VenueResultAdapter(SelectStoreActivity.this, R.id.item_row_name, _stores);
                setListAdapter(adaptor);
            }
        }
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Venue[]> {
        private ProgressDialog _progressDialog;

        GetLocationTask() {
            _progressDialog = Utils.createProgressDialog(SelectStoreActivity.this, "Retrieving nearby stores...");
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected Venue[] doInBackground(Location... params) {
            Location loc = params[0];
            if (loc != null) {
                return FoursquareServer.getVenues(loc.getLatitude(), loc.getLongitude());
            } else {
                return FoursquareServer.getVenues(40.75, -73.98);
            }
        }

        @Override
        protected void onPostExecute(Venue[] result) {
            _progressDialog.dismiss();

            if (result != null) {
                _stores.clear();
                _stores.addAll(Arrays.asList(result));

                ArrayAdapter<Venue> adaptor = new VenueResultAdapter(SelectStoreActivity.this, R.id.item_row_name, _stores);
                setListAdapter(adaptor);
            }
        }
    }

}
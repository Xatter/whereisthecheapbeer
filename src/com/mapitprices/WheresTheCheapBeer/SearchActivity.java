package com.mapitprices.WheresTheCheapBeer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Foursquare.FoursquareResponse;
import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.FoursquareServer;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MyLocationThing;
import com.mapitprices.Utilities.VenueResultAdapter;
import com.mapitprices.WheresTheCheapBeer.Editors.ReportPriceActivity;
import com.mapitprices.WheresTheCheapBeer.ListActivities.StoreItemsActivity;
import com.mapitprices.WheresTheCheapBeer.MapActivities.BeerMapActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/27/11
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends ListActivity {
    public static final String ITEM_SEARCH = "itemsearch";
    public static final String STORE_SEARCH = "storesearch";
    public static final String ITEM_SELECT_SEARCH = "selecteditemsearch";
    public static final String STORE_SELECT_SEARCH = "storeselectsearch";

    private ArrayList<Item> mItemResult;
    private boolean isItemSearch = false;
    private boolean isStoreSearch = false;
    private boolean isItemSelectSearch = false;
    private boolean isStoreSelectSearch = false;
    private GoogleAnalyticsTracker tracker;
    private Venue[] mVenues;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = GoogleAnalyticsTracker.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
                if (appData != null) {
                    isItemSearch = appData.getBoolean(ITEM_SEARCH);
                    isItemSelectSearch = appData.getBoolean(ITEM_SELECT_SEARCH);
                    isStoreSearch = appData.getBoolean(STORE_SEARCH);
                    isStoreSelectSearch = appData.getBoolean(STORE_SELECT_SEARCH);

                    if (isItemSearch || isItemSelectSearch) {
                        ArrayList<Item> items = appData.getParcelableArrayList("items");
                        doItemSearch(items, query);
                    } else if (isStoreSearch || isStoreSelectSearch) {
                        doVenueSearch(query);
                    }
                }
            }
        }
    }

    private void doVenueSearch(String query) {
        MyLocationThing myLocationThing = new MyLocationThing(this);

        android.location.Location location = myLocationThing.getLastFix();
        tracker.trackEvent(
                "Search",
                "StoreSearch",
                query,
                0);

        FoursquareResponse response = FoursquareServer.getVenues(location.getLatitude(), location.getLongitude(), query);

        if (response.meta.code.equals("200")) {
            mVenues = response.response.venues;
            List<Venue> venuesList = Arrays.asList(mVenues);

            setContentView(R.layout.stores_layout);
            ArrayAdapter<Venue> adapter = new VenueResultAdapter(this, R.id.item_list_seperator_text, venuesList);
            setListAdapter(adapter);
        } else if (response.meta.code.equals("500")) {
            Toast.makeText(this, response.meta.errorType, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (isItemSearch) {
            Intent i = new Intent().setClass(this, BeerMapActivity.class);
            i.putExtra("item", mItemResult.get(position));
            startActivity(i);
        } else if (isItemSelectSearch) {
            Intent i = new Intent();
            i.putExtra("item", mItemResult.get(position));
            setResult(RESULT_OK, i);
            finish();
        } else if (isStoreSearch) {
            Intent i = new Intent().setClass(this, StoreItemsActivity.class);
            i.putExtra("venue", mVenues[position]);
            startActivity(i);
        } else if (isStoreSelectSearch) {
            Intent data = new Intent().setClass(this, ReportPriceActivity.class);
            data.putExtra("venue", mVenues[position]);
            startActivity(data);
            finish();
        }
    }

    private void doItemSearch(List<Item> items, String query) {
        String q = query.toUpperCase();
        mItemResult = new ArrayList<Item>();

        for (Item item : items) {
            String name = item.getName().toUpperCase();
            String brand = item.getBrand();
            if (brand != null) {
                brand = brand.toUpperCase();
                if (name.startsWith(q) ||
                        brand.startsWith(q)) {
                    mItemResult.add(item);
                }
            } else if (name.startsWith(q)) {
                mItemResult.add(item);
            }
        }

        tracker.trackEvent(
                "Search",
                "ItemSearch",
                query,
                mItemResult.size());

        setContentView(R.layout.items_layout);
        ArrayAdapter<Item> adapter = new ItemResultAdapter(this, R.id.add_item, mItemResult);
        setListAdapter(adapter);
    }
}
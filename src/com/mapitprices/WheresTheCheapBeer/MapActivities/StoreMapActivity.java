package com.mapitprices.WheresTheCheapBeer.MapActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.*;
import com.mapitprices.WheresTheCheapBeer.Editors.ReportPriceActivity;
import com.mapitprices.WheresTheCheapBeer.ListActivities.StoreItemsActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/8/11
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreMapActivity extends MapActivity {
    MapView mapView;
    List<Overlay> mapOverlays;
    Drawable drawable;
    StoreItemizedOverlay itemizedOverlay;
    MyLocationOverlay myLocationOverlay;
    GoogleAnalyticsTracker tracker;

    private ArrayList<Store> mStoresCache = new ArrayList<Store>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        mapView = (MapView) findViewById(R.id.mapview);

        mapOverlays = mapView.getOverlays();

        myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.enableMyLocation();

        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                new GetLocationTask().execute(myLocationOverlay.getLastFix());
            }
        });

        mapOverlays.add(myLocationOverlay);

        drawable = this.getResources().getDrawable(R.drawable.mapmarker);
        itemizedOverlay = new StoreItemizedOverlay(drawable, this);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onPause() {
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableCompass();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.STORE_SEARCH, true);
        appData.putParcelableArrayList("stores", mStoresCache);
        startSearch(null, false, appData, false);
        return true;
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
                startActivityForResult(i, Constants.RC_NEW_PRICE);
                return true;
            case R.id.menu_scan_barcode:
                IntentIntegrator.initiateScan(this);
                return true;
            case R.id.menu_item_refresh:
                new GetLocationTask().execute(myLocationOverlay.getLastFix());
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
        } else if (requestCode == Constants.RC_NEW_PRICE && resultCode == RESULT_OK) {
            new GetLocationTask().execute(myLocationOverlay.getLastFix());
        }
    }


    private class StoreItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
        private Context mContext;

        public StoreItemizedOverlay(Drawable drawable, Context context) {
            super(boundCenterBottom(drawable));
            mContext = context;
        }

        public void Clear() {
            mapOverlays.clear();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        @Override
        protected boolean onTap(int i) {
            Intent data = new Intent().setClass(StoreMapActivity.this, StoreItemsActivity.class);
            Store clickedStore = mStoresCache.get(i);

            data.putExtra("store", clickedStore);
            tracker.trackEvent("Click", "Store", clickedStore.getName(), clickedStore.getID());

            startActivity(data);

            return true;
        }

        @Override
        public int size() {
            return mOverlays.size();
        }

        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Store>> {
        ProgressDialog mProgressDialog;

        GetLocationTask() {
            //mProgressDialog = Utils.createProgressDialog(StoreMapActivity.this, "Getting nearby stores...");
        }

        @Override
        protected void onPreExecute() {
            //mProgressDialog.show();
        }

        @Override
        protected Collection<Store> doInBackground(Location... params) {
            Location loc = params[0];
            return MapItPricesServer.getNearbyStoresWithPricesFromServer(loc);
        }

        @Override
        protected void onPostExecute(Collection<Store> result) {

            if (result != null) {
                mStoresCache.clear();
                mStoresCache.addAll(result);
                itemizedOverlay.Clear();
                mapOverlays.clear();
                mapOverlays.add(myLocationOverlay);

                for (Store store : mStoresCache) {
                    GeoPoint point = Utils.LocationToGeoPoint(store.getLatitude(), store.getLongitude());
                    OverlayItem overlayitem = new OverlayItem(point, store.getName(), "");
                    itemizedOverlay.addOverlay(overlayitem);

                    mapOverlays.add(itemizedOverlay);
                }

                MapController controller = mapView.getController();
                GeoPoint currentLocationPoint = myLocationOverlay.getMyLocation();
                if (currentLocationPoint != null) {
                    controller.setCenter(currentLocationPoint);
                }
                controller.setZoom(18);
                mapView.setBuiltInZoomControls(true);
            }

            //mProgressDialog.dismiss();
        }
    }
}
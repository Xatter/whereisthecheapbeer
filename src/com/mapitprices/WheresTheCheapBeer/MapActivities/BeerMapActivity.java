package com.mapitprices.WheresTheCheapBeer.MapActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.*;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Responses.MapItResponse;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.Editors.ReportPriceActivity;
import com.mapitprices.WheresTheCheapBeer.ListActivities.StoreItemsActivity;
import com.mapitprices.WheresTheCheapBeer.R;
import com.mapitprices.WheresTheCheapBeer.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/24/11
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeerMapActivity extends MapActivity {
    MapView mapView;
    List<Overlay> mapOverlays;
    Drawable drawable;
    StoreItemizedOverlay itemizedOverlay;
    private Item _item;
    private Store _store;

    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.mapmarker);
        itemizedOverlay = new StoreItemizedOverlay(drawable, this);

        Intent intent = getIntent();
        if (intent != null) {
            _item = intent.getParcelableExtra("item");
            if (_item != null) {
                MapItResponse response = MapItPricesServer.getStore(_item);
                if (response != null && response.Meta.Code.startsWith("20")) {
                    _store = response.Response.store;
                }

                if (_store != null) {
                    GeoPoint point;
                    OverlayItem overlayitem;

                    point = Utils.LocationToGeoPoint(_store.getLatitude(), _store.getLongitude());

                    String snippet = _item.getName() + ", " + _item.getSize() + " - " + _item.getPrice();
                    overlayitem = new OverlayItem(point, _store.getName(), snippet);
                    itemizedOverlay.addOverlay(overlayitem);

                    mapOverlays.add(itemizedOverlay);
                    MapController controller = mapView.getController();

                    controller.setCenter(point);
                    controller.setZoom(18);
                    mapView.setBuiltInZoomControls(true);
                }
            } else {
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_update_price:
                Intent i = new Intent().setClass(this, ReportPriceActivity.class);
                i.putExtra("item", _item);
                i.putExtra("store", _store);
                startActivity(i);
                break;
            case R.id.menu_item_settings:
                i = new Intent().setClass(this, SettingsActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private class StoreItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
        private Context mContext;

        public StoreItemizedOverlay(Drawable drawable, Context context) {
            super(boundCenterBottom(drawable));
            mContext = context;
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);
        }

        @Override
        protected boolean onTap(int i) {
            Intent data = new Intent().setClass(BeerMapActivity.this, StoreItemsActivity.class);
            Store clickedStore = _store;

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
}
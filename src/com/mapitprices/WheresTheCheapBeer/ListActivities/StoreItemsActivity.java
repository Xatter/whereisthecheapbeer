package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class StoreItemsActivity extends ListActivity {
    Store _store;

    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_items_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        Intent triggerIntent = getIntent();
        if (triggerIntent != null) {
            Bundle b = triggerIntent.getExtras();
            Store store = b.getParcelable("store");

            // Populate item info
            TextView tvName = (TextView) findViewById(R.id.store_row_name);
            tvName.setText(store.getName());

            TextView tvDistance = (TextView) findViewById(R.id.store_row_distance);
            DecimalFormat formatter = new DecimalFormat("#.## mi");
            String distanceString = formatter.format(store.getDistance());
            tvDistance.setText(distanceString);

            TextView tvAddress = (TextView) findViewById(R.id.store_address);
            tvAddress.setText(store.getAddress().getStreet());

            _store = store;

            new GetItemsTask().execute();
        }
    }

    private class GetItemsTask extends AsyncTask<String, Void, Collection<Item>> {
        private ProgressDialog _progressDialog;

        GetItemsTask() {
            _progressDialog = Utils.createProgressDialog(StoreItemsActivity.this, "Getting items at that store...");
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected Collection<Item> doInBackground(String... strings) {
            return MapItPricesServer.getItemsFromServer(_store.getID(), tracker);
        }

        @Override
        protected void onPostExecute(Collection<Item> items) {
            _progressDialog.dismiss();

            List<Item> itemList = new ArrayList<Item>(items);

            if (items != null) {
                ArrayAdapter<Item> adapter = new ItemResultAdapter(StoreItemsActivity.this, R.id.item_row_name, itemList);
                setListAdapter(adapter);
            }
        }
    }
}
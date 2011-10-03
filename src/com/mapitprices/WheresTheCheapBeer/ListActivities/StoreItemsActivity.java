package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Responses.MapItResponse;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;

import java.text.DecimalFormat;
import java.util.Arrays;
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
            DecimalFormat formatter = new DecimalFormat("#.## meters");
            String distanceString = formatter.format(store.getDistance());
            tvDistance.setText(distanceString);

            TextView tvAddress = (TextView) findViewById(R.id.store_address);
            tvAddress.setText(store.getAddress().getStreet());

            _store = store;

            new GetItemsTask().execute();
        }
    }

    private class GetItemsTask extends AsyncTask<String, Void, MapItResponse> {
        private ProgressDialog _progressDialog;

        GetItemsTask() {
            _progressDialog = Utils.createProgressDialog(StoreItemsActivity.this, "Getting items at that store...");
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected MapItResponse doInBackground(String... strings) {
            return MapItPricesServer.getItemsFromServer(_store.getID());
        }

        @Override
        protected void onPostExecute(MapItResponse response) {
            _progressDialog.dismiss();

            if (response != null) {

                if (response.Meta.Code.startsWith("20")) {
                    List<Item> itemList = Arrays.asList(response.Response.items);
                    ArrayAdapter<Item> adapter = new ItemResultAdapter(StoreItemsActivity.this, R.id.item_row_name, itemList);
                    setListAdapter(adapter);
                } else {
                    Toast.makeText(StoreItemsActivity.this, response.Meta.ErrorMessage, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
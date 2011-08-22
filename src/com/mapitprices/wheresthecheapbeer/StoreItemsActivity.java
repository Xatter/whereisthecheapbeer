package com.mapitprices.WheresTheCheapBeer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class StoreItemsActivity extends ListActivity {
    Store _store;
    private ProgressDialog _progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent triggerIntent = getIntent();
        if (triggerIntent != null) {
            Bundle b = triggerIntent.getExtras();
            _store = b.getParcelable("store");

            setContentView(R.layout.items_layout);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Getting items at that store...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            _progressDialog = progressDialog;
            _progressDialog.show();

            new GetItemsTask().execute();
        }
    }

    private class GetItemsTask extends AsyncTask<String, Void, Collection<Item>> {
        @Override
        protected Collection<Item> doInBackground(String... strings) {
            return MapItPricesServer.getItemsFromServer(_store.getID());
        }

        @Override
        protected void onPostExecute(Collection<Item> items) {
            _progressDialog.dismiss();

            if (items != null) {
                ArrayAdapter<Item> adapter = new ItemResultAdapter(StoreItemsActivity.this, R.id.item_row_name, items.toArray(new Item[0]));
                setListAdapter(adapter);
            }
        }
    }
}
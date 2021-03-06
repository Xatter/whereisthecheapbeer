package com.mapitprices.WheresTheCheapBeer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.MapActivities.BeerMapActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/21/11
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BarCodeScanItemActivity extends ListActivity {

    private List<Item> mCachedItems;
    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby prices...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);


        Intent i = getIntent();
        Bundle b = i.getExtras();
        String barcode = b.getString("upc");

        progressDialog.show();
        Collection<Item> result = MapItPricesServer.getItemsByBarCode(barcode);
        progressDialog.cancel();

        mCachedItems = new ArrayList<Item>(result);

        ItemResultAdapter adapter = new ItemResultAdapter(this, R.id.item_row_name, mCachedItems);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent().setClass(this, BeerMapActivity.class);
        i.putExtra("item", mCachedItems.get(position));
        startActivity(i);
    }


}
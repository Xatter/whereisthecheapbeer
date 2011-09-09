package com.mapitprices.WheresTheCheapBeer.ListActivities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.Editors.NewItemActivity;
import com.mapitprices.WheresTheCheapBeer.R;
import com.mapitprices.WheresTheCheapBeer.SearchActivity;
import com.mapitprices.WheresTheCheapBeer.SettingsActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectItemActivity extends ListActivity {
    ArrayList<Item> _items = new ArrayList<Item>();
    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = GoogleAnalyticsTracker.getInstance();
        setContentView(R.layout.select_item_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting available items...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);

        progressDialog.show();
        Collection<Item> result = MapItPricesServer.getAllItems(tracker);
        progressDialog.cancel();

        if (result != null) {
            _items.clear();
            _items.addAll(result);

            ArrayAdapter<Item> adapter = new ItemResultAdapter(this, android.R.layout.simple_list_item_1, _items);
            setListAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_list_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.add_item:
                 i = new Intent().setClass(this, NewItemActivity.class);
                startActivityForResult(i, 7);
                return true;
            case R.id.scan_barcode:
                IntentIntegrator.initiateScan(this);
                return true;
            case R.id.menu_item_settings:
                i = new Intent().setClass(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (result != null) {
                if (Utils.validate_or_rescan_upc(this, result)) {
//                    _filteredItems.clear();
//
//                    int arraySize = _items.size();
//                    for (int i = 0; i < arraySize; i++) {
//                        if (_items.get(i).getUPC() == result) {
//                            _filteredItems.add(_items.get(i));
//                            break;
//                        }
//                    }
//
//                    ArrayAdapter<Item> adapter = new ItemResultAdapter(this, android.R.layout.simple_list_item_1, _filteredItems);
//                    setListAdapter(adapter);
                }
            }
        } else if (requestCode == 7 && resultCode == RESULT_OK) {
            Item i = data.getParcelableExtra("item");
            _items.add(i);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("item", _items.get(position));
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.ITEM_SELECT_SEARCH, true);
        appData.putParcelableArrayList("items", _items);
        startSearch(null, false, appData, false);
        return true;
    }

}
package com.mapitprices.WhereIsTheCheapBeer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListAllDistinctItemsActivity extends ListActivity {
    List<Item> _items = new ArrayList<Item>();
    List<Item> _filteredItems = new ArrayList<Item>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                _filteredItems.clear();
                String query = intent.getStringExtra(SearchManager.QUERY);
            }
        }

        Collection<Item> result = MapItPricesServer.getAllItems();

        if (result != null) {
            _items.clear();
            _items.addAll(result);

            ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, result.toArray(new Item[0]));
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
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent i = new Intent().setClass(this, NewItemActivity.class);
                startActivityForResult(i, 7);
                return true;
            case R.id.scan_barcode:
                IntentIntegrator.initiateScan(this);
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
                    _filteredItems.clear();

                    int arraySize = _items.size();
                    for (int i = 0; i < arraySize; i++) {
                        if (_items.get(i).getUPC() == result) {
                            _filteredItems.add(_items.get(i));
                            break;
                        }
                    }

                    ArrayAdapter<Item> adapter = new ItemResultAdapter(this, android.R.layout.simple_list_item_1, _filteredItems);
                    setListAdapter(adapter);
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
}
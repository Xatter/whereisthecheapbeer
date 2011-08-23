package com.mapitprices.WhereIsTheCheapBeer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListAllDistinctItemsActivity extends ListActivity {
    Item[] _items;
    Collection<Item> _filteredItems;

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
            _items = result.toArray(new Item[0]);

            ArrayAdapter<Item> adaptor = new ItemResultAdapter(this, R.id.item_row_name, _items);
            setListAdapter(adaptor);
        }
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
	    	if (result != null)
	    	{
                int len = result.length();
                if (len != 8 ||
                        len != 12 ||
                        len != 13 ||
                        len != 14)
                {
                  //not enough digits, rescan
                    Toast toast = Toast.makeText(this,"Scan didn't get all the digits, please try again.", 2000);
                    toast.show();
                    IntentIntegrator.initiateScan(this);
                }

                _filteredItems.clear();

                for(int i = 0;i<_items.length;i++)
                {
                    if (_items[i].getUPC() == result)
                    {
                        _filteredItems.add(_items[i]);
                        break;
                    }
                }

                ItemResultAdapter adapter = new ItemResultAdapter(this, R.id.item_row_name, _filteredItems.toArray(new Item[0]));
                setListAdapter(adapter);
	    	}
	    }
        else if (requestCode == 7 && resultCode == RESULT_OK)
        {
            setResult(RESULT_OK, data);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("item", _items[position]);
        setResult(RESULT_OK, data);
        finish();
    }
}
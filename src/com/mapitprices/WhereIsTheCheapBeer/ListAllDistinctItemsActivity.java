package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        if (intent != null)
        {
            if (Intent.ACTION_SEARCH.equals(intent.getAction()))
            {
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("item", _items[position]);
        setResult(RESULT_OK, data);
        finish();
    }


}
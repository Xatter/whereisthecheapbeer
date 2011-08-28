package com.mapitprices.WhereIsTheCheapBeer;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.StoreResultAdapter;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/27/11
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends ListActivity {
    public static final String ITEM_SEARCH = "itemsearch";
    public static final String STORE_SEARCH = "storesearch";
    public static final String ITEM_SELECT_SEARCH = "selecteditemsearch";

    private ArrayList<Item> mItemResult;
    private boolean isItemSearch = false;
    private ArrayList<Store> mStoreResult;
    private boolean isStoreSearch = false;
    private boolean isItemSelectSearch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);

                Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
                if (appData != null) {
                    isItemSearch = appData.getBoolean(ITEM_SEARCH);
                    isItemSelectSearch = appData.getBoolean(ITEM_SELECT_SEARCH);
                    isStoreSearch = appData.getBoolean(STORE_SEARCH);

                    if (isItemSearch || isItemSelectSearch) {
                        ArrayList<Item> items = appData.getParcelableArrayList("items");
                        doItemSearch(items, query);
                    }
                    else if (isStoreSearch)
                    {
                        ArrayList<Store> stores = appData.getParcelableArrayList("stores");
                        doStoreSearch(stores, query);
                    }
                }
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (isItemSearch) {
            Intent i = new Intent().setClass(this, BeerMapActivity.class);
            i.putExtra("item", mItemResult.get(position));
            startActivity(i);
        }
        else if (isItemSelectSearch)
        {
            Intent i = new Intent();
            i.putExtra("item", mItemResult.get(position));
            setResult(RESULT_OK, i);
            finish();
        }
        else if (isStoreSearch)
        {
            Intent i = new Intent().setClass(this, StoreItemsActivity.class);
            i.putExtra("store", mStoreResult.get(position));
            startActivity(i);
        }
    }

    private void doItemSearch(List<Item> items, String query) {
        String q = query.toUpperCase();
        mItemResult = new ArrayList<Item>();

        for (Item item : items) {
            String name = item.getName().toUpperCase();
            String brand = item.getBrand().toUpperCase();

            if (name.startsWith(q) ||
                    brand.startsWith(q)) {
                mItemResult.add(item);
            }
        }

        setContentView(R.layout.items_layout);
        ArrayAdapter<Item> adapter = new ItemResultAdapter(this, R.id.add_item, mItemResult);
        setListAdapter(adapter);
    }

    private void doStoreSearch(List<Store> stores, String query)
    {
        String q = query.toUpperCase();
        mStoreResult = new ArrayList<Store>();
        for(Store store : stores)
        {
            String name = store.getName().toUpperCase();
            if(name.contains(q))
            {
                mStoreResult.add(store);
            }
        }

        setContentView(R.layout.stores_layout);
        ArrayAdapter<Store> adapter = new StoreResultAdapter(this, R.id.add_item, mStoreResult);
        setListAdapter(adapter);
    }
}
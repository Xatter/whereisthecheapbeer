package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.ItemResultAdapter;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby prices...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);


        Intent i = getIntent();
        Bundle b = i.getExtras();
        String barcode = b.getString("barcode");
        //new GetLocationTask().execute(searchText);

        progressDialog.show();
        Collection<Item> result = MapItPricesServer.getItemsByBarCode(barcode);
        progressDialog.cancel();

        List<Item> items = new ArrayList<Item>(result);

        ItemResultAdapter adapter = new ItemResultAdapter(this, R.id.item_row_name, items);
        setListAdapter(adapter);
    }
}
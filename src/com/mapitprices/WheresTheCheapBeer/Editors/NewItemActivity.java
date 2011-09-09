package com.mapitprices.WheresTheCheapBeer.Editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mapitprices.Model.Item;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewItemActivity extends Activity {
    Item _item;
    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.trackEvent("NewItem","NewItem","Started",0);
        setContentView(R.layout.item_editor);
        _item = new Item();

        Spinner sizes = (Spinner) findViewById(R.id.new_item_size_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sizes.setAdapter(adapter);
        sizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                String selectedSize = parent.getItemAtPosition(pos).toString();
                _item.setSize(selectedSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    public void onSaveItem(View v) {
        EditText et = (EditText) findViewById(R.id.new_item_name);
        _item.setName(et.getText().toString());

        et = (EditText) findViewById(R.id.new_item_brand);
        _item.setBrand(et.getText().toString());

        et = (EditText) findViewById(R.id.new_item_upc);
        _item.setUPC(et.getText().toString());

        Item returned = MapItPricesServer.createNewItem(_item, tracker);

        if (returned != null) {
            Intent i = new Intent();
            i.putExtra("item", returned);
            setResult(RESULT_OK,i);
            finish();
        } else {
            // maybe do a toast message
            Toast.makeText(this,"Adding item failed", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        }
    }

    public void onScanBarcode(View v) {
        IntentIntegrator.initiateScan(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            if (result != null) {
                if (Utils.validate_or_rescan_upc(this, result)) ;
                {
                    EditText et = (EditText) findViewById(R.id.new_item_upc);
                    et.setText(result);
                }
            }
        }
    }
}
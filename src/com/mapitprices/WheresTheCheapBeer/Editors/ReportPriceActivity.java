package com.mapitprices.WheresTheCheapBeer.Editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.ListActivities.SelectItemActivity;
import com.mapitprices.WheresTheCheapBeer.ListActivities.SelectStoreActivity;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/17/11
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportPriceActivity extends Activity {
    Store _store;
    Item _item;

    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_price_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        tracker.trackEvent(
                "ReportPrice",
                "Started",
                "started",
                0);

        Intent i = getIntent();
        if (i != null) {
            _item = i.getParcelableExtra("item");
            if (_item == null) {
                selectItem();
            } else {
                _store = i.getParcelableExtra("store");
            }
        }

    }

    private void selectItem() {
        tracker.trackEvent(
                "ReportPrice",
                "ItemSelection",
                "started",
                0);

        Intent i = new Intent().setClass(this, SelectItemActivity.class);
        startActivityForResult(i, 0);
    }

    private void selectStore() {
        tracker.trackEvent(
                "ReportPrice",
                "StoreSelection",
                "started",
                0);
        Intent i;
        i = new Intent().setClass(this, SelectStoreActivity.class);
        startActivityForResult(i, 1);
    }


    public void sharePrice(View v) {
        EditText newPriceControl = (EditText) this.findViewById(R.id.report_price_price);
        String priceText = newPriceControl.getText().toString();
        Double newPrice = -1.0;
        boolean abort = false;

        tracker.trackEvent(
                "ReportPrice",
                "SharePrice",
                "clicked",
                0);

        try {
            newPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "I don't understand what you typed in the price field.",Toast.LENGTH_LONG).show();
            abort = true;
            tracker.trackEvent(
                    "ReportPrice",
                    "PriceFailed",
                    priceText,
                    0);
        }

        if (!abort) {
            EditText quantity = (EditText) findViewById(R.id.report_price_quantity);

            if (quantity.getText().toString().length() != 0) {
                Integer q = 1;
                try {
                    q = Integer.parseInt(quantity.getText().toString());
                    abort = true;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "I don't understand what you typed in the quantity field.", Toast.LENGTH_LONG).show();
                    tracker.trackEvent(
                            "ReportPrice",
                            "QuantityFailed",
                            quantity.getText().toString(),
                            0);
                }

                _item.setQuantity(q);
            } else {
                _item.setQuantity(1);
            }

            if (!abort) {
                boolean success = MapItPricesServer.ReportPrice(_item, _store, newPrice, tracker);
                if (success) {
                    tracker.trackEvent(
                            "ReportPrice",
                            "ReportedToServer",
                            "Success",
                            0);

                    Intent data = new Intent();
                    data.putExtra("item", _item);
                    setResult(RESULT_OK, data);
                    finish();
                }
                else
                {
                    tracker.trackEvent(
                            "ReportPrice",
                            "ReportedToServer",
                            "Failed",
                            0);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    _item = b.getParcelable("item");

                    tracker.trackEvent(
                            "ReportPrice",
                            "ItemSelection",
                            "ItemSelected",
                            _item.getID());


                    setItemText();
                    selectStore();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    _store = b.getParcelable("store");

                    tracker.trackEvent(
                            "ReportPrice",
                            "StoreSelection",
                            "StoreSelected",
                            _store.getID());

                    setStoreText();
                }
                break;
            default:
                break;
        }
    }

    private void setStoreText() {
        TextView tv = (TextView) findViewById(R.id.report_price_store);
        tv.setText(_store.getName());
    }

    private void setItemText() {
        TextView tv = (TextView) findViewById(R.id.report_price_item);
        tv.setText(_item.getName());

        if (_item.getQuantity() > 0) {
            EditText quantity = (EditText) findViewById(R.id.report_price_quantity);
            quantity.setText(Integer.toString(_item.getQuantity()));
        }
    }
}
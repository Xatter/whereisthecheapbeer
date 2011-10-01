package com.mapitprices.WheresTheCheapBeer.Editors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Responses.MapItResponse;
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
    public static final int RC_SELECT_ITEM = 0;
    public static final int RC_SELECT_STORE = 1;

    Venue mVenue;
    Store mStore;
    Item mItem;

    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_price_layout);

        tracker = GoogleAnalyticsTracker.getInstance();

        tracker.trackEvent(
                "ReportPrice",
                "Started",
                "started",
                RC_SELECT_ITEM);

        EditText selectItemControl = (EditText) findViewById(R.id.report_price_item);
        selectItemControl.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                selectItem();
                return true;
            }
        });

        EditText selectStoreControl = (EditText) findViewById(R.id.report_price_store);
        selectStoreControl.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                selectStore();
                return true;
            }
        });

        Intent i = getIntent();
        if (i != null) {
            mItem = i.getParcelableExtra("item");
            mStore = i.getParcelableExtra("store");
            mVenue = i.getParcelableExtra("venue");

            if (mItem == null) {
                selectItem();
            } else {
                setItem(mItem);
                if (mVenue != null) {
                    setStore(mVenue);
                } else if (mStore != null) {
                    setStore(mStore);
                } else {
                    mStore = MapItPricesServer.getStore(mItem);
                    setStore(mStore);
                }
            }
        }
    }

    private void setItem(Item item) {
        mItem = item;
        EditText tv = (EditText) findViewById(R.id.report_price_item);
        EditText quantity = (EditText) findViewById(R.id.report_price_quantity);

        if (item != null) {
            tv.setText(item.getName());

            if (item.getQuantity() > RC_SELECT_ITEM) {
                quantity.setText(Integer.toString(item.getQuantity()));
            }
        } else {
            tv.setText("");
            quantity.setText("");
        }
    }

    private void setStore(Store store) {
        mStore = store;
        if (store != null) {
            setStore(store.getName());
        } else {
            setStore("");
        }
    }

    private void setStore(String name) {
        EditText tv = (EditText) findViewById(R.id.report_price_store);
        tv.setText(name);
    }

    private void setStore(Venue store) {
        mVenue = store;
        if (store != null) {
            setStore(mVenue.name);
        } else {
            setStore("");
        }


    }

    private void selectItem() {
        Intent i = new Intent().setClass(this, SelectItemActivity.class);
        startActivityForResult(i, RC_SELECT_ITEM);
    }

    private void selectStore() {
        Intent i = new Intent().setClass(this, SelectStoreActivity.class);
        startActivityForResult(i, RC_SELECT_STORE);
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
                RC_SELECT_ITEM);

        try {
            newPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "I don't understand what you typed in the price field.", Toast.LENGTH_LONG).show();
            abort = true;
            tracker.trackEvent(
                    "ReportPrice",
                    "PriceFailed",
                    priceText,
                    RC_SELECT_ITEM);
        }

        if (!abort) {
            EditText quantity = (EditText) findViewById(R.id.report_price_quantity);

            if (quantity.getText().toString().length() != RC_SELECT_ITEM) {
                Integer q = 1;
                try {
                    q = Integer.parseInt(quantity.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "I don't understand what you typed in the quantity field.", Toast.LENGTH_LONG).show();
                    abort = true;
                    tracker.trackEvent(
                            "ReportPrice",
                            "QuantityFailed",
                            quantity.getText().toString(),
                            RC_SELECT_ITEM);
                }

                mItem.setQuantity(q);
            } else {
                mItem.setQuantity(1);
            }

            if (!abort) {
                MapItResponse response = MapItPricesServer.ReportPrice2(mItem, mVenue, newPrice);
                if (response.Meta.Code.startsWith("20")) {
                    mItem.setPrice(newPrice);

                    tracker.trackEvent(
                            "ReportPrice",
                            "ReportedToServer",
                            "Success",
                            RC_SELECT_ITEM);

                    Intent data = new Intent();
                    data.putExtra("item", mItem);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    tracker.trackEvent(
                            "ReportPrice",
                            "ReportedToServer",
                            "Failed",
                            RC_SELECT_ITEM);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SELECT_ITEM:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    mItem = b.getParcelable("item");
                    setItem(mItem);

                    tracker.trackEvent(
                            "ReportPrice",
                            "ItemSelected",
                            mItem.getName(),
                            mItem.getItemId());

                    if (mVenue == null && mStore == null) {
                        selectStore();
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    mVenue = b.getParcelable("venue");
                    setStore(mVenue);

                    tracker.trackEvent(
                            "ReportPrice",
                            "StoreSelected",
                            mVenue.name,
                            0);
                }
                break;
            default:
                break;
        }
    }

}
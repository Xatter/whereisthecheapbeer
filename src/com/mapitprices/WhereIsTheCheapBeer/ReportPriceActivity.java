package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_price_layout);

        Intent i = getIntent();
        if(i != null)
        {
            _item = i.getParcelableExtra("item");
            if(_item == null)
            {
                selectItem();
            }
            else
            {
                _store = i.getParcelableExtra("store");
            }
        }

    }

    private void selectItem() {
        Intent i = new Intent().setClass(this, ListAllDistinctItemsActivity.class);
        startActivityForResult(i, 0);
    }

    private void selectStore() {
        Intent i;
        i = new Intent().setClass(this, SelectStoreActivity.class);
        startActivityForResult(i, 1);
    }


    public void sharePrice(View v)
    {
        EditText newPriceControl = (EditText)this.findViewById(R.id.report_price_price);
		Double newPrice = Double.parseDouble(newPriceControl.getText().toString());

        EditText quantity = (EditText)findViewById(R.id.report_price_quantity);
        Integer q = Integer.parseInt(quantity.getText().toString());
        _item.setQuantity(q);

        boolean success = MapItPricesServer.ReportPrice(_item, _store, newPrice);
        if(success)
        {
            Intent data = new Intent();
            data.putExtra("item", _item);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    _item = b.getParcelable("item");

                    setItemText();
                    selectStore();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    _store = b.getParcelable("store");
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

        if(_item.getQuantity() > 0)
        {
            EditText quantity = (EditText) findViewById(R.id.report_price_quantity);
            quantity.setText(Integer.toString(_item.getQuantity()));
        }
    }
}
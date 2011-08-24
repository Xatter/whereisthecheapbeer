package com.mapitprices.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mapitprices.Model.Item;
import com.mapitprices.WheresTheCheapBeer.R;

import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ItemResultAdapter extends ArrayAdapter<Item> {
    public Item[] _items;
    private Context _context;

    public ItemResultAdapter(Context context, int textViewResourceId, Item[] objects) {
        super(context, textViewResourceId, objects);
        _items = objects;
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_row_layout, null);
        }

        Item i = _items[position];
        TextView tvName = (TextView) v.findViewById(R.id.item_row_name);
        tvName.setText(i.toString());

        TextView tvPrice = (TextView) v.findViewById(R.id.item_row_price);
        NumberFormat currencyFormatter;
        currencyFormatter = NumberFormat.getCurrencyInstance();
        String formattedPrice = currencyFormatter.format(i.getPrice());
        tvPrice.setText(formattedPrice);

        return v;
    }
}

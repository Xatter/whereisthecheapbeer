package com.mapitprices.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mapitprices.Model.Store;
import com.mapitprices.WheresTheCheapBeer.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */

public class StoreResultAdapter extends ArrayAdapter<Store> {
    public List<Store> _stores;
    private Context _context;

    public StoreResultAdapter(Context context, int textViewResourceId, List<Store> objects) {
        super(context, textViewResourceId, objects);
        _stores = objects;
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.store_row_layout, null);
        }

        Store i = _stores.get(position);
        TextView tvName = (TextView) v.findViewById(R.id.store_row_name);
        tvName.setText(i.getName());

        TextView tvDistance = (TextView) v.findViewById(R.id.store_row_distance);
        DecimalFormat formatter = new DecimalFormat("#.## meters");
        String distanceString = formatter.format(i.getDistance());
        tvDistance.setText(distanceString);

        TextView tvAddress = (TextView) v.findViewById(R.id.store_address);
        tvAddress.setText(i.getAddress().getStreet());

        return v;
    }
}
package com.mapitprices.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Foursquare.VenueCategory;
import com.mapitprices.WheresTheCheapBeer.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */

public class VenueResultAdapter extends ArrayAdapter<Venue> {
    public List<Venue> _stores;
    private Context _context;
    public static HashMap<String, Bitmap> _cache = new HashMap<String, Bitmap>();

    public VenueResultAdapter(Context context, int textViewResourceId, List<Venue> objects) {
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

        Venue i = _stores.get(position);

        ImageView iconView = (ImageView) v.findViewById(R.id.store_icon);
        for (VenueCategory category : i.categories) {
            if (category.primary) {
                URL newurl = null;
                try {
                    String iconURL = category.icon.getIconURL();
                    if (_cache.containsKey(iconURL)) {
                        iconView.setImageBitmap(_cache.get(iconURL));
                    } else {
                        newurl = new URL(category.icon.getIconURL());
                        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        _cache.put(iconURL, mIcon_val);
                        iconView.setImageBitmap(mIcon_val);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                break;
            }
        }

        TextView tvName = (TextView) v.findViewById(R.id.store_row_name);
        tvName.setText(i.name);

        TextView tvDistance = (TextView) v.findViewById(R.id.store_row_distance);
        DecimalFormat formatter = new DecimalFormat("#.## meters");
        String distanceString = formatter.format(i.location.distance);
        tvDistance.setText(distanceString);

        TextView tvAddress = (TextView) v.findViewById(R.id.store_address);
        tvAddress.setText(i.location.address);

        return v;
    }
}
package com.mapitprices.WhereIsTheCheapBeer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mapitprices.Model.Store;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.StoreResultAdapter;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 8/22/11
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectStoreActivity extends ListActivity {
    List<Store> _stores = new ArrayList<Store>();

    private ProgressDialog _progressDialog;
    private final LocationListener currentListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            _progressDialog.show();
            new GetLocationTask().execute(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private Location _currentLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_store_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Getting nearby stores...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        _progressDialog = progressDialog;

        progressDialog.show();
        new GetLocationTask().execute(_currentLocation);
    }

    @Override
    protected void onPause() {
        Utils.unregisterListener(this, currentListener);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _currentLocation = Utils.registerListener(this, currentListener);
    }

    @Override
    protected void onStop() {
        Utils.unregisterListener(this, currentListener);
        super.onStop();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("store", _stores.get(position));
        setResult(RESULT_OK, data);
        finish();
    }

    private class GetLocationTask extends AsyncTask<Location, Void, Collection<Store>> {
        @Override
        protected Collection<Store> doInBackground(Location... params) {
            Location loc = params[0];
            _currentLocation = loc;
            return MapItPricesServer.getAllNearbyStoresFromServer(loc);
        }

        @Override
        protected void onPostExecute(Collection<Store> result) {
            _progressDialog.dismiss();
            _stores.clear();
            _stores.addAll(result);

            if (result != null) {
                ArrayAdapter<Store> adaptor = new StoreResultAdapter(SelectStoreActivity.this, R.id.item_row_name, _stores);
                setListAdapter(adaptor);
            }
        }
    }

}
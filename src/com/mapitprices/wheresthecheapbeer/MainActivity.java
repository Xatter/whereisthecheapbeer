package com.mapitprices.WheresTheCheapBeer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Add first tab
        intent = new Intent().setClass(this, NearbyItemsActivity.class);
        spec = tabHost.newTabSpec("blah").setIndicator(
                "Nearby Prices",
                res.getDrawable(android.R.drawable.ic_menu_mylocation))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, NearbyStoresActivity.class);
        spec = tabHost.newTabSpec("statelookup").setIndicator("Stores")
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    public void launchMap(View v) {
        Intent i = new Intent();
    }

    public void launchReportPrice(View v) {
        Intent reportPriceIntent = new Intent().setClass(MainActivity.this, ReportPriceActivity.class);
        startActivity(reportPriceIntent);
    }

    public void launchItemSearch(View v) {

    }
}

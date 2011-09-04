package com.mapitprices.WhereIsTheCheapBeer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/3/11
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeScreenActivity extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Add first tab
        intent = new Intent().setClass(this, NearbyItemsActivity.class);
        //spec = tabHost.newTabSpec("blah").setIndicator(
//                "Nearby Prices",
//                res.getDrawable(android.R.drawable.ic_menu_mylocation))
//                .setContent(intent);

        spec = tabHost.newTabSpec("items").setIndicator("Beers")
                    .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, NearbyStoresActivity.class);
        spec = tabHost.newTabSpec("stores").setIndicator("Stores")
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

//    public void launchReportPrice(View v) {
//        Intent reportPriceIntent = new Intent().setClass(MainActivity.this, ReportPriceActivity.class);
//        startActivity(reportPriceIntent);
//    }
}

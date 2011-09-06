package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
        String token = settings.getString("SessionToken", "");

        if(token != null && !token.isEmpty())
        {
            User u = MapItPricesServer.login(token);

            Intent i;
            if(u.getSessionToken() != null)
            {
                User.getInstance().setUsername(u.getUsername());
                User.getInstance().setEmail(u.getEmail());
                User.getInstance().setSessionToken(u.getSessionToken());

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SessionToken",User.getInstance().getSessionToken());
                editor.commit();

                i = new Intent().setClass(this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }
        }

        setContentView(R.layout.main_layout);
    }

    public void signup(View v)
    {
        Intent i = new Intent().setClass(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    public void signin(View v)
    {
        Intent i = new Intent().setClass(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

//    public void launchReportPrice(View v) {
//        Intent reportPriceIntent = new Intent().setClass(MainActivity.this, ReportPriceActivity.class);
//        startActivity(reportPriceIntent);
//    }

}

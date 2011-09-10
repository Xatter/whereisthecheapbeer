package com.mapitprices.WheresTheCheapBeer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.mapitprices.Model.User;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/5/11
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        TextView tv = (TextView) findViewById(R.id.username);
        tv.setText(User.getInstance().getUsername());
    }

    public void logout(View v) {
        SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("SessionToken", "");
        editor.commit();

        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
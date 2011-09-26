package com.mapitprices.WheresTheCheapBeer.LoginActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.HomeScreenActivity;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/3/11
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SignUpActivity extends Activity {
    GoogleAnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        tracker = GoogleAnalyticsTracker.getInstance();
    }

    public void createUser(View v) {
        EditText et = (EditText) findViewById(R.id.email);
        String email = et.getText().toString();

        et = (EditText) findViewById(R.id.password);
        String password = et.getText().toString();

        et = (EditText) findViewById(R.id.username);
        String username = et.getText().toString();

        if (email != null && email.length() != 0
                && password != null && password.length() != 0) {
            User.getInstance().setEmail(email);
            User.getInstance().setUsername(username);

            ProgressDialog pd = Utils.createProgressDialog(this, "Creating your account...");
            pd.show();
            User returned = MapItPricesServer.createNewUser(User.getInstance(), password);
            pd.dismiss();

            if (returned != null) {
                User.getInstance().setSessionToken(returned.getSessionToken());

                SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SessionToken", User.getInstance().getSessionToken());
                editor.putString("User.Email", User.getInstance().getEmail());
                editor.commit();

                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent().setClass(this, HomeScreenActivity.class);
                startActivity(i);
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
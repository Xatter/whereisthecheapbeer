package com.mapitprices.WheresTheCheapBeer.LoginActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Responses.MapItResponse;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.FoursquareServer;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.HomeScreenActivity;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/2/11
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {

    GoogleAnalyticsTracker tracker;

    private static final int RC_FOURSQUARE_SIGNUP = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        tracker = GoogleAnalyticsTracker.getInstance();
    }

    public void signin(View v) {
        EditText et = (EditText) findViewById(R.id.email);
        String email = et.getText().toString();

        et = (EditText) findViewById(R.id.password);
        String password = et.getText().toString();

        if (email != null && email.length() != 0
                && password != null && password.length() != 0) {
            new LoginTask().execute(email, password);
        }
    }

    public void foursquare_signin(View v) {
        tracker.trackEvent("Click", "Foursquare_Login", "", 0);
        Intent i = new Intent().setClass(this, ActivityWebView.class);
        startActivityForResult(i, RC_FOURSQUARE_SIGNUP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_FOURSQUARE_SIGNUP) {
            if (resultCode == RESULT_OK) {
                com.mapitprices.Model.Foursquare.User fqUser = FoursquareServer.getUserInfo();
                if (fqUser != null) {
                    User user = User.getInstance();

                    user.setUsername(fqUser.firstName + " " + fqUser.lastName);
                    user.setEmail(fqUser.contact.email);

                    MapItResponse response = MapItPricesServer.FoursquareLogin();
                    if (response.Meta.Code.equals("200")) {
                        User returnedUser = response.Response.user;
                        user.setID(returnedUser.getID());
                        user.setUsername(returnedUser.getUsername());

                        Intent i = new Intent().setClass(this, HomeScreenActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    // not authenticated
                }
            }
        }
    }

    private class LoginTask extends AsyncTask<String, Void, MapItResponse> {
        ProgressDialog mProgressDialog;

        LoginTask() {
            mProgressDialog = Utils.createProgressDialog(LoginActivity.this, "Logging in...");
        }

        @Override
        protected MapItResponse doInBackground(String... strings) {
            return MapItPricesServer.login(strings[0], strings[1]);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(MapItResponse response) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response.Meta.Code.equals("200")) {
                User user = response.Response.user;
                Intent i;
                if (user != null && user.getSessionToken() != null) {
                    User.getInstance().setUsername(user.getUsername());
                    User.getInstance().setEmail(user.getEmail());
                    User.getInstance().setSessionToken(user.getSessionToken());

                    SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("SessionToken", User.getInstance().getSessionToken());
                    editor.putString("User.Email", User.getInstance().getEmail());
                    editor.commit();

                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    i = new Intent().setClass(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(i);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else {
                Toast.makeText(LoginActivity.this, response.Meta.ErrorMessage, Toast.LENGTH_LONG).show();

                // Invalid login, clear password field.
                EditText et = (EditText) findViewById(R.id.password);
                et.setText("");
                et.requestFocus();
            }
        }

    }

}
package com.mapitprices.WheresTheCheapBeer.LoginActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.mapitprices.Model.Responses.MapItResponse;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.FoursquareServer;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.HomeScreenActivity;
import com.mapitprices.WheresTheCheapBeer.R;


public class MainActivity extends Activity {
    public static final int RC_FOURSQUARE_SIGNUP = 0;
    public static final int RC_LOGIN = 1;
    public static final int RC_SIGNUP = 2;

    private ProgressDialog _progressDialog;
    GoogleAnalyticsTracker tracker;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = GoogleAnalyticsTracker.getInstance();

        SharedPreferences settings = getSharedPreferences("BeerPreferences", RC_FOURSQUARE_SIGNUP);
        String token = settings.getString("SessionToken", "");

        if (token != null && token.length() != RC_FOURSQUARE_SIGNUP) {
            new LoginTask().execute(token);
        } else {
            setContentView(R.layout.main_layout);
        }
    }

    public void signup(View v) {
        tracker.trackEvent("Click", "Signup", "", 0);
        Intent i = new Intent().setClass(this, SignUpActivity.class);
        startActivityForResult(i, RC_SIGNUP);
    }

    public void signin(View v) {
        tracker.trackEvent("Click", "Login", "", 0);
        Intent i = new Intent().setClass(this, LoginActivity.class);
        startActivityForResult(i, RC_LOGIN);
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
        } else if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == RC_SIGNUP) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private class LoginTask extends AsyncTask<String, Void, MapItResponse> {
        ProgressDialog mProgressDialog;

        LoginTask() {
            mProgressDialog = Utils.createProgressDialog(MainActivity.this, "Logging in...");
        }

        @Override
        protected MapItResponse doInBackground(String... strings) {
            return MapItPricesServer.login(strings[0]);
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

            if (response != null && response.Meta.Code.equals("200")) {
                User user = response.Response.user;

                Intent i;
                if (user != null && user.getSessionToken() != null) {
                    User.getInstance().setUsername(user.getUsername());
                    User.getInstance().setEmail(user.getEmail());
                    User.getInstance().setSessionToken(user.getSessionToken());

                    SharedPreferences settings = getSharedPreferences("BeerPreferences", RC_FOURSQUARE_SIGNUP);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("SessionToken", User.getInstance().getSessionToken());
                    editor.putString("User.Email", User.getInstance().getEmail());
                    editor.commit();

                    i = new Intent().setClass(MainActivity.this, HomeScreenActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    setContentView(R.layout.main_layout);
                }
            } else {
                setContentView(R.layout.main_layout);
            }
        }
    }
}

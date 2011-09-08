package com.mapitprices.WheresTheCheapBeer.LoginActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.Utilities.Utils;
import com.mapitprices.WheresTheCheapBeer.HomeScreenActivity;
import com.mapitprices.WheresTheCheapBeer.R;


public class MainActivity extends Activity {
    private ProgressDialog _progressDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
        String token = settings.getString("SessionToken", "");

        if (token != null && token.length() != 0) {


            new LoginTask().execute(token);
        } else {
            setContentView(R.layout.main_layout);
        }
    }

    public void signup(View v) {
        Intent i = new Intent().setClass(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    public void signin(View v) {
        Intent i = new Intent().setClass(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

//    public void launchReportPrice(View v) {
//        Intent reportPriceIntent = new Intent().setClass(MainActivity.this, ReportPriceActivity.class);
//        startActivity(reportPriceIntent);
//    }


    private class LoginTask extends AsyncTask<String, Void, User> {
        ProgressDialog mProgressDialog;

        LoginTask() {
            mProgressDialog = Utils.createProgressDialog(MainActivity.this, "Logging in...");
        }

        @Override
        protected User doInBackground(String... strings) {
            return MapItPricesServer.login(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(User user) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
               e.printStackTrace();
            }

            Intent i;
            if (user != null && user.getSessionToken() != null) {
                User.getInstance().setUsername(user.getUsername());
                User.getInstance().setEmail(user.getEmail());
                User.getInstance().setSessionToken(user.getSessionToken());

                SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SessionToken", User.getInstance().getSessionToken());
                editor.commit();

                i = new Intent().setClass(MainActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            } else {
                setContentView(R.layout.main_layout);
            }
        }

    }
}

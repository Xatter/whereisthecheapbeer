package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.MapItPricesServer;
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
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Logging in...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            _progressDialog = progressDialog;

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

        @Override
        protected User doInBackground(String... strings) {
            return MapItPricesServer.login(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            _progressDialog.show();
        }

        @Override
        protected void onPostExecute(User user) {
            _progressDialog.cancel();

            Intent i;
            if (user.getSessionToken() != null) {
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

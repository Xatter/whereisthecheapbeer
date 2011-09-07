package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.mapitprices.Model.User;
import com.mapitprices.Utilities.MapItPricesServer;
import com.mapitprices.WheresTheCheapBeer.R;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/2/11
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {
    private ProgressDialog _progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging in...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        _progressDialog = progressDialog;
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

        private class LoginTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... strings) {
            return MapItPricesServer.login(strings[0], strings[1]);
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

                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                i = new Intent().setClass(LoginActivity.this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            } else {
                setContentView(R.layout.main_layout);
            }
        }

    }

}
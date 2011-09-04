package com.mapitprices.WhereIsTheCheapBeer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
    }

    public void signin(View v) {
        EditText et = (EditText) findViewById(R.id.email);
        String email = et.getText().toString();

        et = (EditText) findViewById(R.id.password);
        String password = et.getText().toString();

        if (email != null && !email.isEmpty()
                && password != null && !password.isEmpty()) {

            User returned = MapItPricesServer.login(email, password);

            if(returned != null)
            {
                User.getInstance().setSessionToken(returned.getSessionToken());

                SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SessionToken",User.getInstance().getSessionToken());

                //Commit the fucking changes
                editor.commit();

                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent().setClass(this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }

        }

    }

}
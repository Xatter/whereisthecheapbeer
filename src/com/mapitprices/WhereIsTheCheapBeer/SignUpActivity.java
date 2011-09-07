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
 * Date: 9/3/11
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SignUpActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
    }

    public void createUser(View v)
    {
        EditText et = (EditText)findViewById(R.id.email);
        String email = et.getText().toString();

        et = (EditText) findViewById(R.id.password);
        String password = et.getText().toString();

        et = (EditText) findViewById(R.id.username);
        String username = et.getText().toString();

        if(email != null && email.length() != 0
                && password != null && password.length() != 0)
        {
            User.getInstance().setEmail(email);
            User.getInstance().setUsername(username);
            User returned = MapItPricesServer.createNewUser(User.getInstance(), password);

            if(returned != null)
            {
                User.getInstance().setSessionToken(returned.getSessionToken());

                SharedPreferences settings = getSharedPreferences("BeerPreferences", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("SessionToken",User.getInstance().getSessionToken());

                // Commit the edits!
                editor.commit();

                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent().setClass(this, HomeScreenActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
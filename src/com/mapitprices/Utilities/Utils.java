package com.mapitprices.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.mapitprices.WheresTheCheapBeer.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/20/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static List<NameValuePair> locationToNameValuePair(Location loc) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        if (loc == null) {
            nameValuePairs.add(new BasicNameValuePair("Lat", "40.75"));
            nameValuePairs.add(new BasicNameValuePair("Lng", "-73.98"));
        } else {
            nameValuePairs.add(new BasicNameValuePair("Lat", Double.toString(loc.getLatitude())));
            nameValuePairs.add(new BasicNameValuePair("Lng", Double.toString(loc.getLongitude())));
        }

        return nameValuePairs;
    }

    public static boolean validate_or_rescan_upc(Activity context, String result) {
        int len = result.length();
        if (len != 8 &&
                len != 12 &&
                len != 13 &&
                len != 14) {
            //not enough digits, rescan
            Toast toast = Toast.makeText(context, "Scan didn't get all the digits, please try again.", 2000);
            toast.show();
            IntentIntegrator.initiateScan(context);
            return false;
        }

        return true;
    }

    public static GeoPoint LocationToGeoPoint(Location loc) {
        if (loc == null) {
            return Utils.LocationToGeoPoint(40.75, -73.98); // NYC by default
        }

        return Utils.LocationToGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static GeoPoint LocationToGeoPoint(double latitude, double longitude) {
        return new GeoPoint((int) (latitude * 1e6),
                (int) (longitude * 1e6));
    }

    public static byte[] getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] salt = "2dzfQi1xA5nY3H5k5ecR".getBytes("UTF-8");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        return digest.digest(password.getBytes("UTF-8"));
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.MapItDialog);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        return progressDialog;
    }
}

package com.mapitprices.Utilities;

import android.location.Location;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Store;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapItPricesServer {

    //public static final String SERVER_URL = "http://www.mapitprices.com/Beer/";
    public static final String SERVER_URL = "http://10.0.2.2/Beer/";

    public static final int MIN_DISTANCE = 200; // in meters
    public static final int MIN_TIME = 300000; // 5 minutes in ms

    public static Collection<Item> getItemsFromServer(int storeid) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("StoreID", Integer.toString((storeid))));

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetAllItemsAtStore", nameValuePairs);

        return jsonResultToItemCollection(result);
    }

    public static Collection<Item> getItemsFromServer(Location loc) {
        List<NameValuePair> nameValuePairs = Utils.locationToNameValuePair(loc);

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetItems", nameValuePairs);

        return jsonResultToItemCollection(result);
    }

    public static Collection<Store> getStoresFromServer(Location loc) {
        List<NameValuePair> nameValuePairs = Utils.locationToNameValuePair(loc);

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetStores", nameValuePairs);

        return jsonResultToStoreCollection(result);
    }

    private static Collection<Store> jsonResultToStoreCollection(String result) {
        if (result != null && result.length() != 0) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Store>>() {
            }.getType();
            return gson.fromJson(result, collectionType);
        }

        return null;
    }

        private static Collection<Item> jsonResultToItemCollection(String result) {
        if (result != null && result.length() != 0) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Item>>() {
            }.getType();
            return gson.fromJson(result, collectionType);
        }

        return null;
    }

    public static Collection<Item> getItemsByBarCode(String barcode) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("barcode", barcode));

        String result = RestClient.ExecuteCommand(SERVER_URL + "ItemsFromBarcode", nameValuePairs);
        return jsonResultToItemCollection(result);
    }
}

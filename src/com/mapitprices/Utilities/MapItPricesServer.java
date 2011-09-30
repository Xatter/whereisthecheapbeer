package com.mapitprices.Utilities;

import android.location.Location;
import android.os.Build;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mapitprices.Compatibility.Base64;
import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Item;
import com.mapitprices.Model.Requests.ReportPriceRequest;
import com.mapitprices.Model.Responses.MapItResponse;
import com.mapitprices.Model.Store;
import com.mapitprices.Model.User;
import com.mapitprices.WheresTheCheapBeer.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/21/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapItPricesServer {
    //public static String SERVER_URL = "http://10.0.2.2:61418/Beer/"; //Emulator localhost
    //public static final String SERVER_URL = "http://172.16.210.128//Beer/"; //Mac Laptop windows IP

    public static String SERVER_URL = "http://www.mapitprices.com/Beer/";

    static {
        if (Constants.DEBUGMODE && "google_sdk".equals(Build.PRODUCT)) {
            SERVER_URL = "http://10.0.2.2:61418/Beer/"; //Emulator localhost
        } else {
            SERVER_URL = "http://www.mapitprices.com/Beer/";
        }
    }


    public static Collection<Item> getItemsFromServer(int storeid) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "getItemsFromServer", "StoreID", storeid);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("StoreID", Integer.toString((storeid))));

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetAllItemsAtStore", nameValuePairs);

        return jsonResultToItemCollection(result);
    }

    public static Collection<Item> getNearbyPrices(Location loc) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "getNearbyPrices", "", 0);
        List<NameValuePair> nameValuePairs = Utils.locationToNameValuePair(loc);
        String result = RestClient.ExecuteCommand(SERVER_URL + "GetItemPrices", nameValuePairs);

        return jsonResultToItemCollection(result);
    }

    public static Collection<Store> getNearbyStoresWithPricesFromServer(Location loc) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "getNearbyStoresWithPricesFromServer", "", 0);
        List<NameValuePair> nameValuePairs = Utils.locationToNameValuePair(loc);

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetStores", nameValuePairs);

        return jsonResultToStoreCollection(result);
    }

    private static Collection<Store> jsonResultToStoreCollection(String result) {
        if (result != null && result.length() != 0) {
            Gson gson = createGson();
            Type collectionType = new TypeToken<Collection<Store>>() {
            }.getType();
            return gson.fromJson(result, collectionType);
        }

        return null;
    }

    private static Collection<Item> jsonResultToItemCollection(String result) {
        if (result != null && result.length() != 0) {

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DotNetGsonDateTimeDeserializer());
            Gson gson = builder.create();

            Type collectionType = new TypeToken<Collection<Item>>() {
            }.getType();
            return gson.fromJson(result, collectionType);
        }

        return null;
    }

    public static Collection<Item> getItemsByBarCode(String barcode) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "GetItemsByBarCode", barcode, 0);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("upc", barcode));

        String result = RestClient.ExecuteCommand(SERVER_URL + "GetItemPricesByUPC", nameValuePairs);
        return jsonResultToItemCollection(result);
    }

    public static Collection<Item> getAllItems(GoogleAnalyticsTracker tracker) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "GetAllItems", "", 0);
        String result = RestClient.ExecuteCommand(SERVER_URL + "GetAllItems");
        return jsonResultToItemCollection(result);
    }

    public static boolean ReportPrice(Item item, Store store, Double newPrice) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "ReportPrice", "Started", 0);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("itemid", Integer.toString(item.getItemID())));
        nameValuePairs.add(new BasicNameValuePair("storeid", Integer.toString(store.getID())));
        nameValuePairs.add(new BasicNameValuePair("price", Double.toString(newPrice)));
        nameValuePairs.add(new BasicNameValuePair("quantity", Integer.toString(item.getQuantity())));

        String result = RestClient.ExecuteCommand(SERVER_URL + "ReportPrice", nameValuePairs);
        return !result.equals("{}\n");
    }

    public static Item createNewItem(Item item) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "NewItem", item.getName(), 0);
        String result = RestClient.ExecuteCommand(SERVER_URL + "CreateItem", item.toNameValuePairs());
        try {
            Gson gson = createGson();
            return gson.fromJson(result, Item.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Store createNewStore(Store s) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "NewStore", s.getName(), 0);
        String result = RestClient.ExecuteCommand(SERVER_URL + "CreateStore", s.toNameValuePairs());

        if (result.equals("{}\n"))
            return null;

        try {
            Gson gson = createGson();
            return gson.fromJson(result, Store.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DotNetGsonDateTimeDeserializer());
        Gson gson = builder.create();
        return gson;
    }

    public static Store getStore(Item i) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "GetStore", "StoreID", i.getStoreID());
        List<NameValuePair> values = new ArrayList<NameValuePair>(1);
        values.add(new BasicNameValuePair("storeid", Integer.toString(i.getStoreID())));
        String result = RestClient.ExecuteCommand(SERVER_URL + "GetStore", values);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DotNetGsonDateTimeDeserializer());

        Gson gson = builder.create();

        try {
            return gson.fromJson(result, Store.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User createNewUser(User i, String password) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "CreateUser", "newuser", 0);
        List<NameValuePair> values = new ArrayList<NameValuePair>(2);
        values.add(new BasicNameValuePair("email", i.getEmail()));
        values.add(new BasicNameValuePair("username", i.getUsername()));
        try {
            String hashedPassword = Base64.encodeToString(Utils.getHash(password), Base64.DEFAULT);
            values.add(new BasicNameValuePair("password", hashedPassword));
            String result = RestClient.ExecuteCommand(SERVER_URL + "CreateUser", values);
            Gson gson = createGson();
            return gson.fromJson(result, User.class);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JsonIOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static MapItResponse login(String username, String password) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "Login", "Email/Password", 0);

        JSONObject values = new JSONObject();

        try {
            values.put("APIVersion", Constants.APIVERSION);
            values.put("email", username);

            String hashedPassword = Base64.encodeToString(Utils.getHash(password), Base64.DEFAULT);
            values.put("password", hashedPassword);
            String result = RestClient.ExecuteJSONCommand(SERVER_URL + "Login2", values);

            Gson gson = createGson();
            MapItResponse response = gson.fromJson(result, MapItResponse.class);

            return response;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JsonIOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static MapItResponse login(String sessionToken) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "Login", sessionToken, 0);
        JSONObject values = new JSONObject();

        try {
            values.put("APIVersion", Constants.APIVERSION);
            values.put("SessionToken", sessionToken);

            String result = RestClient.ExecuteJSONCommand(SERVER_URL + "Login2", values);
            Gson gson = createGson();
            MapItResponse response = gson.fromJson(result, MapItResponse.class);
            return response;
        } catch (JsonParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static Collection<Store> getAllNearbyStoresFromServer(Location loc) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "getAllNearbyStoresFromServer", "", 0);
        List<NameValuePair> nameValuePairs = Utils.locationToNameValuePair(loc);
        String result = RestClient.ExecuteCommand(SERVER_URL + "GetNearbyStores", nameValuePairs);
        return jsonResultToStoreCollection(result);
    }

    public static MapItResponse FoursquareLogin() {
        User user = User.getInstance();
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "FoursquareLogin", user.getUsername(), 0);
        JSONObject values = new JSONObject();

        try {
            values = user.ToJSON();
            values.put("APIVersion", Constants.APIVERSION);
            String result = RestClient.ExecuteJSONCommand(SERVER_URL + "FoursquareLogin", values);

            Gson gson = createGson();
            MapItResponse response = gson.fromJson(result, MapItResponse.class);

            return response;
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static MapItResponse ReportPrice2(Item item, Venue store, Double newPrice) {
        GoogleAnalyticsTracker.getInstance().trackEvent("ServerCall", "ReportPrice2", "[" + item.getName() + "][" + store.name + "]", 0);

        ReportPriceRequest request = new ReportPriceRequest();
        request.item = item;
        request.store = store;
        request.newprice = newPrice;

        try {
            Gson gson = createGson();
            String jsonString = gson.toJson(request);
            JSONObject data = new JSONObject(jsonString);
            String result = RestClient.ExecuteJSONCommand(SERVER_URL + "ReportPrice2", data);
            MapItResponse response = gson.fromJson(result, MapItResponse.class);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}

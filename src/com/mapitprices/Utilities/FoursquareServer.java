package com.mapitprices.Utilities;

import com.google.gson.Gson;
import com.mapitprices.Model.Foursquare.FoursquareResponse;
import com.mapitprices.Model.Foursquare.User;
import com.mapitprices.Model.Foursquare.Venue;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 9/18/11
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class FoursquareServer {

    public final static String FoursquareURL = "https://api.foursquare.com/v2/";

    public static User getUserInfo() {
        String requestURL = FoursquareURL + "/users/self?" +
                "oauth_token=" + com.mapitprices.Model.User.getInstance().getFoursquareToken();

        String result = RestClient.ExecuteGetCommand(requestURL);
        Gson gson = new Gson();
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);

        if (response.meta.code == "200") {
            return response.response.user;
        } else {

            return null;
        }
    }

    public static Venue[] getVenues(double lat, double lng) {
        String requestURL = FoursquareURL +
                "/users/self?" +
                "ll=" + Double.toString(lat) + "," + Double.toString(lng) +
                "&oauth_token=" + com.mapitprices.Model.User.getInstance().getFoursquareToken();

        String result = RestClient.ExecuteGetCommand(requestURL);

        Gson gson = new Gson();
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);

        if (response.meta.code == "200") {
            return response.response.venues;
        } else {
            return null;
        }
    }
}

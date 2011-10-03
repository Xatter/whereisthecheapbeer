package com.mapitprices.Utilities;

import android.location.Location;
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

    public final static String FoursquareURL = "https://api.foursquare.com/v2";

    public static User getUserInfo() {
        String requestURL = FoursquareURL + "/users/self?" +
                "oauth_token=" + com.mapitprices.Model.User.getInstance().getFoursquareToken();

        String result = RestClient.ExecuteGetCommand(requestURL);
        Gson gson = new Gson();
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);

        if (response.meta.code.equals("200")) {
            return response.response.user;
        } else {
            return null;
        }
    }

    public static Venue[] getVenuesOauth(double lat, double lng) {
        String requestURL = FoursquareURL +
                "/venues/search?" +
                "ll=" + Double.toString(lat) + "," + Double.toString(lng) +
                "&oauth_token=" + com.mapitprices.Model.User.getInstance().getFoursquareToken();

        String result = RestClient.ExecuteGetCommand(requestURL);

        Gson gson = new Gson();
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);

        if (response.meta.code.equals("200")) {
            return response.response.venues;
        } else {
            return null;
        }
    }

    public static Venue[] getVenues(double lat, double lng) {
        String requestURL = FoursquareURL +
                "/venues/search?" +
                "ll=" + Double.toString(lat) + "," + Double.toString(lng) +
                "&client_id=V3DOEGUQF250ZTEFXUO24TUX4XND0YY5UN0F1L23R54B22QO" +
                "&client_secret=32D1HGRL51TBIQRNBAO35A5JPLHWYDSQXLLAYFO5M0RUPHL5" +
//                "&categoryId=4d4b7105d754a06376d81259" +
                "&v=20110925";

        String result = RestClient.ExecuteGetCommand(requestURL);

        Gson gson = new Gson();
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);

        if (response.meta.code.equals("200")) {
            return response.response.venues;
        } else {
            return null;
        }
    }

    public static FoursquareResponse Checkin(Location location, String venueid) {
        String requestURL = FoursquareURL +
                "/checkins/add?" +
                "venueId=" + venueid +
                "&oauth_token=" + com.mapitprices.Model.User.getInstance().getFoursquareToken();

        if (location != null) {
            requestURL += "&ll=" + location.getLatitude() + "," + location.getLongitude();
        }

        Gson gson = new Gson();
        String result = RestClient.ExecuteCommand(requestURL);
        FoursquareResponse response = gson.fromJson(result, FoursquareResponse.class);
        return response;
    }
}

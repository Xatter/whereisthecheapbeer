package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/2/11
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */

public class User implements Parcelable {
    int ID;
    String Username;
    String Email;
    String SessionToken;
    String FoursquareToken;

    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private User() {

    }

    public User(Parcel in) {
        ID = in.readInt();
        Username = in.readString();
        Email = in.readString();
        SessionToken = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Username);
        parcel.writeString(Email);
        parcel.writeString(SessionToken);
    }

    public String getEmail() {
        return Email;
    }

    public String getSessionToken() {
        return SessionToken;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSessionToken(String sessionToken) {
        SessionToken = sessionToken;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public void setFoursquareToken(String token) {
        FoursquareToken = token;
    }

    public String getFoursquareToken() {
        return FoursquareToken;
    }

    public List<NameValuePair> ToNameValuePairs() {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("username", Username));
        pairs.add(new BasicNameValuePair("email", Email));
        pairs.add(new BasicNameValuePair("foursquaretoken", FoursquareToken));
        pairs.add(new BasicNameValuePair("sessiontoken", SessionToken));

        return pairs;
    }

    public void setID(int id) {
        ID = id;
    }

    public int getID() {
        return ID;
    }
}

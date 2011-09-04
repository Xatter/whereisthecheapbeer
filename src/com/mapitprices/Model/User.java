package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/2/11
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */

public class User implements Parcelable {
    int ID;
    String Email;
    String SessionToken;

    private static User ourInstance = new User();

    public static User getInstance()
    {
        return ourInstance;
    }

    private User()
    {

    }

    public User(Parcel in)
    {
        ID = in.readInt();
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
        parcel.writeString(Email);
        parcel.writeString(SessionToken);
    }

    public String getEmail() {
        return Email;
    }

    public String getSessionToken()
    {
        return SessionToken;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSessionToken(String sessionToken) {
        SessionToken = sessionToken;
    }
}

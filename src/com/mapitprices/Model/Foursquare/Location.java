package com.mapitprices.Model.Foursquare;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 9/18/11
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Location implements Parcelable {
    public String address;
    public String crossStreet;
    public double lat;
    public double lng;
    public double distance;
    public String postalCode;
    public String city;
    public String state;

    public Location() {

    }

    public Location(Parcel in) {
        address = in.readString();
        crossStreet = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        distance = in.readDouble();
        postalCode = in.readString();
        city = in.readString();
        state = in.readString();
    }

    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);

        if (crossStreet != null) {
            parcel.writeString(crossStreet);
        } else {
            parcel.writeString("");
        }

        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeDouble(distance);
        parcel.writeString(postalCode);
        parcel.writeString(city);
        parcel.writeString(state);
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}

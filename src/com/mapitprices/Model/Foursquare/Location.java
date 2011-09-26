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
public class Location  implements Parcelable{
    public String address;
    public String crossStreet;
    public double lat;
    public double lng;
    public double distance;
    public String postalCode;
    public String city;
    public String state;

    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(crossStreet);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeDouble(distance);
        parcel.writeString(postalCode);
        parcel.writeString(city);
        parcel.writeString(state);
    }
}

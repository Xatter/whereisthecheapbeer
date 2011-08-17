package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    String _street;
    String _city;
    String _state;
    String _zip;

    public Address(String street, String city, String state, String zip) {
        _street = street;
        _city = city;
        _state = state;
        _zip = zip;
    }

    public Address(Parcel in) {
        _street = in.readString();
        _city = in.readString();
        _state = in.readString();
        _zip = in.readString();
    }

    public String getStreet() {
        return _street;
    }

    public String getCity() {
        return _city;
    }

    public String getState() {
        return _state;
    }

    public String getZip() {
        return _zip;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}

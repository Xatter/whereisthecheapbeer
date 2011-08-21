package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    String Street;
    String City;
    String State;
    String Zip;

    public Address(String street, String city, String state, String zip) {
        Street = street;
        City = city;
        State = state;
        Zip = zip;
    }

    public Address(Parcel in) {
        Street = in.readString();
        City = in.readString();
        State = in.readString();
        Zip = in.readString();
    }

    public String getStreet() {
        return Street;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getZip() {
        return Zip;
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

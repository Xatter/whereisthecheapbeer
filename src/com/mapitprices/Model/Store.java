package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Store implements Parcelable {
    int _id = -1;
    String _name;
    Address _address;
    GPS _gps;

    public Store(String name, Address address) {
        _name = name;
        _address = address;
    }

    public Store(int id, String name, Address address, GPS gps) {
        _id = id;
        _name = name;
        _address = address;
        _gps = gps;
    }

    public Store(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _address = in.readParcelable(Address.class.getClassLoader());
        _gps = in.readParcelable(GPS.class.getClassLoader());
    }

    public int getID() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Address getAddress() {
        return _address;
    }

    public GPS getGPS() {
        return _gps;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeParcelable(_address, flags);
        dest.writeParcelable(_gps, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        public Store[] newArray(int size) {
            return new Store[size];
        }
    };
}

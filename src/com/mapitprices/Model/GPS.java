package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GPS implements Parcelable {
    double _longitude;
    double _latitude;

    public GPS(double lng, double lat) {
        _longitude = lng;
        _latitude = lat;
    }

    public GPS(Parcel in) {
        _longitude = in.readDouble();
        _latitude = in.readDouble();
    }

    public GPS(String gps) {
        _longitude = Double.parseDouble(gps.split(",")[0]);
        _latitude = Double.parseDouble(gps.split(",")[1]);
    }

    public double getLongitude() {
        return _longitude;
    }

    public double getLatitude() {
        return _latitude;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(_longitude);
        dest.writeDouble(_latitude);
    }

    public static final Parcelable.Creator<GPS> CREATOR = new Parcelable.Creator<GPS>() {

        public GPS createFromParcel(Parcel source) {
            return new GPS(source);
        }

        public GPS[] newArray(int size) {
            return new GPS[size];
        }

    };
}

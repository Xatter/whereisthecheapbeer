package com.mapitprices.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Store implements Parcelable {
    int ID = -1;
    String Name;
    Address Address;
    double Latitude;
    double Longitude;
    public float Distance;
    public String FoursquareVenueID;
    public Location Location;

    public Store() {

    }

    public Store(String name, Address address) {
        Name = name;
        Address = address;
    }

    public Store(int id, String name, Address address, GPS gps) {
        ID = id;
        Name = name;
        Address = address;
    }

    public Store(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        Address = in.readParcelable(Address.class.getClassLoader());
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        Distance = in.readFloat();
        Location = in.readParcelable(android.location.Location.class.getClassLoader());
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public Address getAddress() {
        return Address;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeParcelable(Address, flags);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeFloat(Distance);
        dest.writeParcelable(Location, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    public double getDistance() {
        return Distance;
    }

    public void setName(String s) {
        Name = s;
    }

    public List<NameValuePair> toNameValuePairs() {
        List<NameValuePair> values = new ArrayList<NameValuePair>(3);

        values.add(new BasicNameValuePair("Name", Name));
        values.add(new BasicNameValuePair("Latitude", Double.toString(Latitude)));
        values.add(new BasicNameValuePair("Longitude", Double.toString(Longitude)));

        return values;
    }

    public void setLocation(Location currentLocation) {
        Latitude = currentLocation.getLatitude();
        Longitude = currentLocation.getLongitude();
        Location = currentLocation;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public Location getLocation() {
        if (this.Location == null)
        {
            this.Location = new Location("");
        }

        this.Location.setLatitude(this.Latitude);
        this.Location.setLongitude(this.Longitude);

        return this.Location;
    }
}

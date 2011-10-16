package com.mapitprices.Model.Foursquare;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 9/18/11
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Venue implements Parcelable {
    public String id;
    public String name;
    public Location location;
    public VenueCategory[] categories;
    public boolean verified;

    public Venue() {

    }

    public Venue(Parcel in) {
        id = in.readString();
        name = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        categories = in.createTypedArray(VenueCategory.CREATOR);
    }

    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeParcelable(location, 0);
        parcel.writeParcelableArray(categories, 0);
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
}

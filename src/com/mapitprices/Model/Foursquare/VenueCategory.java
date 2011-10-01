package com.mapitprices.Model.Foursquare;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: xatter
 * Date: 9/18/11
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class VenueCategory implements Parcelable{
    public String id;
    public String name;
    public String pluralName;
    public String shortName;
    public String icon;
    public String[] parents;
    public boolean primary;

    public VenueCategory()
    {

    }

    public VenueCategory(Parcel in) {
        id = in.readString();
        name = in.readString();
        pluralName = in.readString();
        shortName = in.readString();
        icon = in.readString();
        //parents = in.createStringArray();
    }

    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(pluralName);
        parcel.writeString(shortName);
        parcel.writeString(icon);
        //parcel.writeStringArray(parents);
    }

    public static final Creator<VenueCategory> CREATOR = new Creator<VenueCategory>(){
        public VenueCategory createFromParcel(Parcel in) {
            return new VenueCategory(in);
        }

        public VenueCategory[] newArray(int size){
            return new VenueCategory[size];
        }
    };
}
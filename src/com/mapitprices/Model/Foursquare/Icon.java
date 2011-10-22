package com.mapitprices.Model.Foursquare;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 10/22/11
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Icon implements Parcelable {
    public String prefix;
    public int[] sizes;
    public String name;

    public String getIconURL() {
        String url = prefix + sizes[0] + name;
        return url;
    }

    public Icon() {

    }

    public Icon(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static final Creator<Icon> CREATOR = new Creator<Icon>() {
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };

}

package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    int ID;
    String Name;
    String _upc;
    String Size;
    String Brand;
    double Price;
    int StoreID;

    public Item() {

    }

    public Item(String name) {
        this(-1, name, "", "");
    }

    public Item(String name, String size) {
        this(-1, name, "", size);
    }

    public Item(String name, String size, String upc) {
        this(-1, name, upc, size);
    }

    public Item(int id, String name, String upc) {
        this(id, name, upc, "");
    }

    public Item(int id, String name, String upc, String size) {
        ID = id;
        Name = name;
        _upc = upc;
        Size = size;
    }

    public Item(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        _upc = in.readString();
        Size = in.readString();
        Brand = in.readString();
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getUPC() {
        return _upc;
    }

    public String getSize() {
        return Size;
    }

    public String getBrand() {
        return Brand;
    }

    public double getPrice() {
        return Price;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeString(_upc);
        dest.writeString(Size);
        dest.writeString(Brand);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}

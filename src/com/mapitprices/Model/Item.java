package com.mapitprices.Model;

import android.os.Parcel;
import android.os.Parcelable;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// Currently this object is overloaded as both the representation of an Item AND a StoreItem
// from the database's perspective.
public class Item implements Parcelable {
    int ItemId;
    String Name;
    String UPC;
    String Size;
    String Brand;
    double Price;
    int StoreId;
    int Quantity;
    Date LastUpdated;
    User User;

    public Item() {

    }

    public Item(Parcel in) {
        ItemId = in.readInt();
        Name = in.readString();
        UPC = in.readString();
        Size = in.readString();
        Brand = in.readString();
        Price = in.readDouble();
        Quantity = in.readInt();
        StoreId = in.readInt();
        User = in.readParcelable(User.class.getClassLoader());
        LastUpdated = (Date)in.readSerializable();
    }

    public int getItemId() {
        return ItemId;
    }

    public String getName() {
        return Name;
    }

    public String getUPC() {
        return UPC;
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
        dest.writeInt(ItemId);
        dest.writeString(Name);
        dest.writeString(UPC);
        dest.writeString(Size);
        dest.writeString(Brand);
        dest.writeDouble(Price);
        dest.writeInt(Quantity);
        dest.writeInt(StoreId);
        dest.writeParcelable(User, flags);
        dest.writeSerializable(LastUpdated);
    }

    public List<NameValuePair> toNameValuePairs() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
        nameValuePairs.add(new BasicNameValuePair("ItemId", Integer.toString(ItemId)));
        nameValuePairs.add(new BasicNameValuePair("Name", Name));
        nameValuePairs.add(new BasicNameValuePair("UPC", UPC));
        nameValuePairs.add(new BasicNameValuePair("Size", Size));
        nameValuePairs.add(new BasicNameValuePair("Brand", Brand));
        nameValuePairs.add(new BasicNameValuePair("Price", Double.toString(Price)));
        nameValuePairs.add(new BasicNameValuePair("Quantity", Integer.toString(Quantity)));

        return nameValuePairs;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public void setName(String text) {
        Name = text;
    }

    public void setBrand(String s) {
        Brand = s;
    }

    public void setUPC(String s) {
        UPC = s;
    }

    public void setSize(String selectedSize) {
        Size = selectedSize;
    }

    public void setQuantity(Integer q) {
        Quantity = q;
    }

    @Override
    public String toString() {
        if (Quantity > 0)
            return Name + ", " + Size + " x " + Quantity;
        else
            return Name + ", " + Size;
    }

    public int getQuantity() {
        return Quantity;
    }

    public int getStoreId() {
        return StoreId;
    }

    public String getLastUpdated() {
        if (LastUpdated != null) {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(LastUpdated);
        } else
            return "";
    }

    public User getUser() {
        return User;
    }

    public void setPrice(Double newPrice) {
        Price = newPrice;
    }

    public void setUser(User user) {
        User = user;
    }
}

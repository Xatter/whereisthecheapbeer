package com.mapitprices.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 8/27/11
 * Time: 12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapItDBAdapter {
    private static final String DATABASE_NAME = "mapitprices";
    private static final int DATABASE_VERSION = 1;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ITEMS (ID INTEGER, Name Text, UPC Text, Size Text, UserID Integer, Brand Text, Created Text, LastUpdated Text);");
            sb.append("CREATE TABLE STORES (ID INTEGER, Name Text, Latitude Real, Longitude Real, Street Text, City Text, State Text, Zip Text, UserID Integer, Created Text);");
            sb.append("CREATE TABLE STOREITEMS (ItemID INTEGER, StoreID INTEGER, Price REAL, Quantity INTEGER, Lastupdated Text, Created Text);");
            sqLiteDatabase.execSQL(sb.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDB;
    private final Context mContext;

    MapItDBAdapter(Context context) {
        mContext = context;
    }

    public MapItDBAdapter open() throws SQLException {
        //mDbHelper = new DatabaseHelper(mContext);
        //mDB = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


}

package com.mostafaabedi.parksmartapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Database and table information
    private static final String DATABASE_NAME = "SmartPark";
    private static final String TABLE_NAME = "Accounts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "PASSWORD";

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db)  // Handles table creation
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, "
                + COL_3 + " TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) // Handles upgrades for database
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Adds new locations to the database
    public void insertAccount(String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase(); // Variable to communicate with the database
        ContentValues values = new ContentValues(); // Used to hold the location data for each column of the table
        // Put each component of the location data with the associated column
        values.put(COL_2, email);
        values.put(COL_3, password);

        db.insert(TABLE_NAME, null, values); // insert the location record

        db.close();
    }

    // Updates existing location records in the database
    public void updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PASSWORD", newPassword);
        db.update("accounts", values, "EMAIL = ?", new String[]{email});
    }

    // Searches through all location records based on query
    public Cursor searchAccountRecords(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM accounts WHERE EMAIL = ?";
        return db.rawQuery(query, new String[]{email});
    }

    // Obtains a location record through its ID
    public Cursor getAccountRecord(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase(); // Variable to communicate with the database

        String query = "SELECT * FROM Accounts WHERE id=?";

        return db.rawQuery(query, new String[]{String.valueOf(id)}); // Perform query to obtain the location record
    }
}

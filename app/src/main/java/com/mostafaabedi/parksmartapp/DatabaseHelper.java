package com.mostafaabedi.parksmartapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * The {@code DatabaseHelper} class is responsible for managing the database operations
 * related to user accounts in the SmartPark application.
 * It handles table creation, upgrades, and CRUD operations on the accounts database.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    // Database and table information
    private static final String DATABASE_NAME = "SmartPark";
    private static final String TABLE_NAME = "Accounts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "PASSWORD";

    /**
     * Constructs a new {@code DatabaseHelper}.
     *
     * @param context the context of the calling activity.
     */
    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 2);
    }

    /**
     * Called when the database is created for the first time.
     * Creates the {@code Accounts} table if it does not already exist.
     *
     * @param db the database instance where the table will be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db)  // Handles table creation
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, "
                + COL_3 + " TEXT) ");
    }

    /**
     * Called when the database needs to be upgraded.
     * Drops the existing {@code Accounts} table and recreates it.
     *
     * @param db the database instance to be upgraded.
     * @param oldVersion the old database version.
     * @param newVersion the new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) // Handles upgrades for database
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Inserts a new account into the database.
     *
     * @param email    the email of the new account.
     * @param password the password of the new account.
     */
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

    /**
     * Updates the password of an existing account in the database.
     *
     * @param email       the email of the account whose password is to be updated.
     * @param newPassword the new password to set for the account.
     */
    public void updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PASSWORD", newPassword);
        db.update("accounts", values, "EMAIL = ?", new String[]{email});
    }

    /**
     * Searches for account records by email.
     *
     * @param email the email of the account to search for.
     * @return a {@code Cursor} object containing the results of the query.
     */
    public Cursor searchAccountRecords(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM accounts WHERE EMAIL = ?";
        return db.rawQuery(query, new String[]{email});
    }

    /**
     * Retrieves an account record by its ID.
     *
     * @param id the ID of the account to retrieve.
     * @return a {@code Cursor} object containing the results of the query.
     */
    public Cursor getAccountRecord(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase(); // Variable to communicate with the database
        String query = "SELECT * FROM Accounts WHERE id=?";
        return db.rawQuery(query, new String[]{String.valueOf(id)}); // Perform query to obtain the location record
    }
}

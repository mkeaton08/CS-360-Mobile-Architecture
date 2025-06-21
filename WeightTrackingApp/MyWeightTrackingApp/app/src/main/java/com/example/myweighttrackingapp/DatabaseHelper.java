package com.example.myweighttrackingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

// This class creates and manages the user login database
public class DatabaseHelper extends SQLiteOpenHelper {

    // Name and version of the database
    private static final String DATABASE_NAME = "userDB.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Constructor - sets up the database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This runs when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create user table
        String createUserTable = "CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT)";
        db.execSQL(createUserTable);

        // Create weight entry table
        String createWeightTable = "CREATE TABLE weights (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "date TEXT, " +
                "weight REAL)";

        db.execSQL(createWeightTable);
    }

    // This runs if the database is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Checks if the username and password are correct
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // Adds a new user to the database
    public boolean createUser(String username, String password) {
        if (checkUserExists(username)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Checks if a username is already taken
    private boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }
    // INSERT new weight entry
    public boolean addWeight(String username, String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("date", date);
        values.put("weight", weight);

        long result = db.insert("weights", null, values);
        db.close();
        return result != -1;
    }

    // READ all weight entries for a user
    public List<WeightEntry> getAllWeights(String username) {
        List<WeightEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("weights", null, "username=?",
                new String[]{username}, null, null, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));

                entries.add(new WeightEntry(id, username, date, weight));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return entries;
    }

    // UPDATE a weight entry by ID
    public boolean updateWeight(int id, String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("weight", weight);

        int rows = db.update("weights", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // DELETE a weight entry by ID
    public boolean deleteWeight(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("weights", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
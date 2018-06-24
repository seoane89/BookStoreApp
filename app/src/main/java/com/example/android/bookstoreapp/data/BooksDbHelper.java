package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BooksDbHelper extends SQLiteOpenHelper {
    private static final int VERSION_DATABASE = 1;
    private static final String NAME_DATABASE = "bookStore.db";

    private static final String SQL_CREATE_DATABASE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
            + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + BookEntry.COLUMN_PRODUCT_AUTHOR + " TEXT NOT NULL DEFAULT Unknown, "
            + BookEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.0, "
            + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + BookEntry.COLUMN_SUPPLIER_NAME + " INTEGER DEFAULT 0, "
            + BookEntry.COLUMN_SUPPLIER_NUMBER + " INTEGER DEFAULT 0);";

    private static final String SQL_DELETE_DATABASE = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    public BooksDbHelper(Context context) {
        super(context, NAME_DATABASE, null, VERSION_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DATABASE);
        Log.d("BooksDbHelper", SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

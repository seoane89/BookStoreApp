package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BooksDbHelper;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    //Temporary helper method to display the current entries in the database in a textview
    private void displayDatabaseInfo() {

        String[] projection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_AUTHOR,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER
        };
        //Perform a query on the provider using the ContentResolver.
        //Use the CONTENT_URI to access the book data
        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null, null);


        // Find listview to populate
        ListView listView = (ListView) findViewById(R.id.listview);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Setup cursor adapter
        BookCursorAdapter bookCursorAdapter = new BookCursorAdapter(this, cursor);
        listView.setAdapter(bookCursorAdapter);
    }

    private void insertBook() {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Name of the Wind");
        values.put(BookEntry.COLUMN_PRODUCT_AUTHOR, "Patrick Rothfuss");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, 18.00);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 2);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.SUPPLIER_ABV);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, BookEntry.SUPPLIER_ABV_NUMBER);

        // Insert a new row for Name of the wind into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access the book's data in the future.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


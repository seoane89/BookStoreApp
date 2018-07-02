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

        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            // _id -  product name - author - product price - product quantity - supplier name - supplier number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText(getString(R.string.book_table_contains) + cursor.getCount() + " books.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_PRODUCT_AUTHOR + " - " +
                    BookEntry.COLUMN_PRODUCT_PRICE + " - " +
                    BookEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_SUPPLIER_NUMBER + " - " + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierNumber = cursor.getInt(supplierNumberColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" +
                        currentID + " - " +
                        currentName + " - " +
                        currentAuthor + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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


package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static int EXISTING_PET_LOADER = 0;

    // EditText field to enter the book's Name
    private EditText mBookNameEditText;

    // EditText field to enter the book's Author
    private EditText mBookAuthorEditText;

    // EditText field to enter the book's Price
    private EditText mBookPriceEditText;

    // EditText field to enter the book's Quantity
    private EditText mBookQuantityEditText;

    // EditText field to enter the supplier Name
    private Spinner mBookSupplierNameSpinner;

    private int mSupplier = 0;
    private int mSupplierNumber = 0;

    private Uri mCurrentBookUri;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            //Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Insert book into database
                insertBook();
                //exit editor activity
                finish();
                return true;
            //Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //Do nothing for now
                return true;
            //Respond to a click on the "Up" arrow button
            case android.R.id.home:
                //Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity,
        //in order to figure out if we're creating a new pet or editing an existing one
        Intent intent = getIntent();
        Uri currentBookUri = intent.getData();
        mCurrentBookUri = currentBookUri;


        //If the intent DOES NOT contain a book content URI, then we know that we are creating a new book
        if (currentBookUri == null) {
            //This is a new book so change the app bar to display "Add a Book"
            setTitle(R.string.add_book);
        } else {
            setTitle(R.string.edit_book);
        }

        // Find all the views that we'll need to read user input from
        mBookNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mBookAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mBookPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mBookSupplierNameSpinner = (Spinner) findViewById(R.id.supplier_spinner);

        setupSpinner();

        //Kick off the loader
        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

    }

    // Setup the spinner that allows the user to select supplier from a list
    private void setupSpinner() {
        //Create adapter for spinner, the list options are from array
        //the spinner uses dropdown layout
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        // Specify the spinner dropdown layout
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBookSupplierNameSpinner.setAdapter(supplierSpinnerAdapter);

        mBookSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.orange_supplier))) {
                        mSupplier = BookEntry.SUPPLIER_ORANGE; //Orange Center
                        mSupplierNumber = BookEntry.SUPPLIER_ORANGE_NUMBER;
                    } else if (selection.equals(getString(R.string.hiron_supplier))) {
                        mSupplier = BookEntry.SUPPLIER_HIRON; // Hiron
                        mSupplierNumber = BookEntry.SUPPLIER_HIRON_NUMBER;
                    } else if (selection.equals(getString(R.string.abv_supplier))) {
                        mSupplier = BookEntry.SUPPLIER_ABV; // ABV
                        mSupplierNumber = BookEntry.SUPPLIER_ABV_NUMBER;
                    } else if (selection.equals(getString(R.string.elephant_supplier))) {
                        mSupplier = BookEntry.SUPPLIER_ELEPHANT; //Elephant
                        mSupplierNumber = BookEntry.SUPPLIER_ELEPHANT_NUMBER;
                    } else {
                        mSupplier = BookEntry.SUPPLIER_UNKNOWN; //Unknown
                        mSupplierNumber = BookEntry.SUPPLIER_UNKNOWN_NUMBER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Get the user input from editor and save the new pet into database
    private void insertBook() {
        String nameString = mBookNameEditText.getText().toString().trim();
        String authorString = mBookAuthorEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        float price = Float.parseFloat(priceString);
        String quantityString = mBookQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        //Create a ContentValues Object where column names are the keys
        // and book attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookEntry.COLUMN_PRODUCT_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplier);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, mSupplierNumber);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu
        //This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection including the columns we care about
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_AUTHOR,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER
        };

        // This Loader will execute the ContentProvider's query method on a background thread
        //Use the CONTENT_URI to access the pet data
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_AUTHOR);
            int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String currentName = data.getString(nameColumnIndex);
            String currentAuthor = data.getString(authorColumnIndex);
            float currentPrice = data.getFloat(priceColumnIndex);
            int currentQuantity = data.getInt(quantityColumnIndex);
            int currentSupplierName = data.getInt(supplierNameColumnIndex);
            int currentSupplierNumber = data.getInt(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(currentName);
            mBookAuthorEditText.setText(currentAuthor);
            mBookPriceEditText.setText(Float.toString(currentPrice));
            mBookQuantityEditText.setText(Integer.toString(currentQuantity));
            // Supplier is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (currentSupplierName) {
                case BookEntry.SUPPLIER_ORANGE:
                    mBookSupplierNameSpinner.setSelection(1);
                    break;
                case BookEntry.SUPPLIER_HIRON:
                    mBookSupplierNameSpinner.setSelection(2);
                    break;
                case BookEntry.SUPPLIER_ABV:
                    mBookSupplierNameSpinner.setSelection(3);
                    break;
                case BookEntry.SUPPLIER_ELEPHANT:
                    mBookSupplierNameSpinner.setSelection(4);
                    break;

                default:
                    mBookSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookNameEditText.setText(null);
        mBookAuthorEditText.setText(null);
        mBookPriceEditText.setText(null);
        mBookQuantityEditText.setText(null);
        mBookSupplierNameSpinner.setSelection(0);
    }
}
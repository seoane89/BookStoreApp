package com.example.android.bookstoreapp;

        import android.content.ContentValues;
        import android.database.sqlite.SQLiteDatabase;
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
        import com.example.android.bookstoreapp.data.BooksDbHelper;

public class EditorActivity extends AppCompatActivity {

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

        // Find all the views that we'll need to read user input from
        mBookNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mBookAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mBookPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mBookSupplierNameSpinner = (Spinner) findViewById(R.id.supplier_spinner);

        setupSpinner();

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
}
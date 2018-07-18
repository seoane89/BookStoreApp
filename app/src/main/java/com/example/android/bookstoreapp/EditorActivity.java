package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static int EXISTING_BOOK_LOADER = 0;

    // EditText field to enter the book's Name
    private EditText mBookNameEditText;

    // EditText field to enter the book's Author
    private EditText mBookAuthorEditText;

    // EditText field to enter the book's Price
    private EditText mBookPriceEditText;

    // EditText field to enter the book's Quantity
    private EditText mBookQuantityEditText;

    // Spinner field to select the supplier Name
    private Spinner mBookSupplierNameSpinner;

    public TextView supplierNumberTextView;

    private int mSupplier = 0;
    private int mSupplierNumber = 0;
    int currentSupplierNumber = 0;

    private Uri mCurrentBookUri;

    private boolean mBookHasChanged = false;

    Button incrementButton;
    Button decrementButton;
    int quantity = 0;
    String quantityString;
    Button orderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity,
        //in order to figure out if we're creating a new book or editing an existing one
        Intent intent = getIntent();
        Uri currentBookUri = intent.getData();
        mCurrentBookUri = currentBookUri;


        //If the intent DOES NOT contain a book content URI, then we know that we are creating a new book
        if (currentBookUri == null) {
            //This is a new book so change the app bar to display "Add a Book"
            setTitle(R.string.add_book);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_book);
            //Kick off the loader
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all the views that we'll need to read user input from
        mBookNameEditText = findViewById(R.id.edit_book_name);
        mBookAuthorEditText = findViewById(R.id.edit_book_author);
        mBookPriceEditText = findViewById(R.id.edit_book_price);
        mBookQuantityEditText = findViewById(R.id.edit_book_quantity);
        mBookSupplierNameSpinner = findViewById(R.id.supplier_spinner);
        supplierNumberTextView = findViewById(R.id.supplier_phone);

        setupSpinner();

        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookAuthorEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierNameSpinner.setOnTouchListener(mTouchListener);

        incrementButton = findViewById(R.id.increment_button);
        decrementButton = findViewById(R.id.decrement_button);
        orderButton = findViewById(R.id.order_button);

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityString = mBookQuantityEditText.getText().toString().trim();
                if (TextUtils.isEmpty(quantityString)) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityString);
                    quantity = quantity + 1;

                    quantityString = Integer.toString(quantity);
                    mBookQuantityEditText.setText(quantityString);
                }
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityString = mBookQuantityEditText.getText().toString().trim();
                if (TextUtils.isEmpty(quantityString)) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityString);
                }
                if (quantity > 0) {
                    quantity = quantity - 1;
                    quantityString = Integer.toString(quantity);
                    mBookQuantityEditText.setText(quantityString);
                } else {
                    Toast.makeText(EditorActivity.this, R.string.cant_less_than_zero, Toast.LENGTH_SHORT).show();
                }
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(Intent.ACTION_DIAL);
                orderIntent.setData(Uri.parse("tel:" + currentSupplierNumber));
                if (orderIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(orderIntent);
                }
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            //Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Insert book into database
                saveBook();
                //exit editor activity
                finish();
                return true;
            //Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                /// Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Respond to a click on the "Up" arrow button
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mBookHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

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
                    supplierNumberTextView.setText(String.valueOf(mSupplierNumber));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Get the user input from editor and save the new book into database
    private void saveBook() {
        String nameString = mBookNameEditText.getText().toString().trim();
        String authorString = mBookAuthorEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        String quantityString = Integer.toString(quantity);



        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                mSupplier == BookEntry.SUPPLIER_UNKNOWN) {
            return;
        }

        //Create a ContentValues Object where column names are the keys
        // and book attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookEntry.COLUMN_PRODUCT_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplier);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, currentSupplierNumber);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        double price = 0.00;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, price);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
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
        //Use the CONTENT_URI to access the book data
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int authorColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_AUTHOR);
            int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String currentName = data.getString(nameColumnIndex);
            String currentAuthor = data.getString(authorColumnIndex);
            double currentPrice = data.getDouble(priceColumnIndex);
            int currentQuantity = data.getInt(quantityColumnIndex);
            int currentSupplierName = data.getInt(supplierNameColumnIndex);
            currentSupplierNumber = data.getInt(supplierNumberColumnIndex);
            currentSupplierNumber = mSupplierNumber;

            // Update the views on the screen with the values from the database
            mBookNameEditText.setText(currentName);
            mBookAuthorEditText.setText(currentAuthor);
            mBookPriceEditText.setText(Double.toString(currentPrice));
            mBookQuantityEditText.setText(Integer.toString(currentQuantity));
            // Supplier is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (currentSupplierName) {
                case BookEntry.SUPPLIER_ORANGE:
                    mBookSupplierNameSpinner.setSelection(1);
                    currentSupplierNumber = BookEntry.SUPPLIER_ORANGE_NUMBER;
                    break;
                case BookEntry.SUPPLIER_HIRON:
                    mBookSupplierNameSpinner.setSelection(2);
                    currentSupplierNumber = BookEntry.SUPPLIER_HIRON_NUMBER;
                    break;
                case BookEntry.SUPPLIER_ABV:
                    mBookSupplierNameSpinner.setSelection(3);
                    currentSupplierNumber = BookEntry.SUPPLIER_ABV_NUMBER;
                    break;
                case BookEntry.SUPPLIER_ELEPHANT:
                    mBookSupplierNameSpinner.setSelection(4);
                    currentSupplierNumber = BookEntry.SUPPLIER_ELEPHANT_NUMBER;
                    break;

                default:
                    mBookSupplierNameSpinner.setSelection(0);
                    currentSupplierNumber = BookEntry.SUPPLIER_UNKNOWN_NUMBER;
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
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
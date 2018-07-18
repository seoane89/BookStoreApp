package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;


/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.book_name_text_view);
        TextView summaryTextView = (TextView) view.findViewById(R.id.book_author_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        ImageView saleImageView = (ImageView) view.findViewById(R.id.sale_button);
        View infoContainerView = (View) view.findViewById(R.id.book_info_container);
        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final int id = cursor.getInt(idColumnIndex);
        String bookName = cursor.getString(nameColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        double bookPrice = cursor.getDouble(priceColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        summaryTextView.setText(bookAuthor);
        priceTextView.setText(context.getResources().getString(R.string.price_string) + String.valueOf(bookPrice));
        quantityTextView.setText(context.getResources().getString(R.string.quantity_string) + String.valueOf(bookQuantity));

        infoContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(context, EditorActivity.class);
                //Form the content URI that represents the specific book that was clicked on
                //by appending the id

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                //Set the uri on the data field of the intent
                editorIntent.setData(currentBookUri);
                //Launch the EditorActivity
                context.startActivity(editorIntent);
            }
        });

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = bookQuantity;
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                if (quantity >= 1) {
                    quantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    int rowsAffected = context.getContentResolver().update(currentBookUri, values, null, null);
                    if (rowsAffected != 0) {
                        quantityTextView.setText(context.getResources().getString(R.string.quantity_string) + String.valueOf(bookQuantity));
                        Toast.makeText(context, R.string.successful_sale, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.sold_out, Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}
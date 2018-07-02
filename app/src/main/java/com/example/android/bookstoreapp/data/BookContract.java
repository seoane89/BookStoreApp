package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {


    //Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";
    //Base content Url
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Pets table path
    public static final String PATH_BOOKS = "books";

    public static abstract class BookEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;



        // Usng withAppendedPath() method we add the path to the base content Uri to create the complete content Uri
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // Define table name
        public static final String TABLE_NAME = "books";
        // Define table columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_AUTHOR = "author";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_NUMBER = "telephone";

        //Possible values for Supplier Name
        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_ORANGE = 1;
        public static final int SUPPLIER_HIRON = 2;
        public static final int SUPPLIER_ABV = 3;
        public static final int SUPPLIER_ELEPHANT = 4;

        //Values corresponding to suppliers for the phone numbers
        public static final int SUPPLIER_UNKNOWN_NUMBER = 0;
        public static final int SUPPLIER_ORANGE_NUMBER = 11111;
        public static final int SUPPLIER_HIRON_NUMBER = 22222;
        public static final int SUPPLIER_ABV_NUMBER = 33333;
        public static final int SUPPLIER_ELEPHANT_NUMBER = 44444;

        /**
         * Returns whether or not the given supplier is {@link #SUPPLIER_UNKNOWN}, {@link #SUPPLIER_ABV}, {@link #SUPPLIER_ELEPHANT}, {@link #SUPPLIER_ORANGE},
         * or {@link #SUPPLIER_HIRON}.
         */
        public static boolean isValidSupplier(int supplier) {
            if (supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_ORANGE || supplier == SUPPLIER_HIRON || supplier == SUPPLIER_ABV || supplier == SUPPLIER_ELEPHANT) {
                return true;
            }
            return false;
        }



    }

    //Prevents the BookContract class from being instantiated.
    private BookContract() {
    }

}

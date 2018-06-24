package com.example.android.bookstoreapp.data;

import android.provider.BaseColumns;

public final class BookContract {
    public static abstract class BookEntry implements BaseColumns{

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




    }
    //Prevents the BookContract class from being instantiated.
    private BookContract(){}

}

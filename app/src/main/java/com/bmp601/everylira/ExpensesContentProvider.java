package com.bmp601.everylira;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Objects;

public class ExpensesContentProvider extends ContentProvider {
    private MyDatabaseHelper dbHelper;

    private static final int ALL_EXPENSES = 1;
    private static final int SINGLE_EXPENSE = 2;
    private static final int ALL_EXPENSES_ITEMS = 3;
    private static final int ALL_EXPENSES_SERVICES_REPORT = 4;
    private static final int ALL_EXPENSES_SERVICES_COST = 5;
    private static final int ALL_EXPENSES_PAID_REPORT = 6;
    private static final int ALL_EXPENSES_PAID_COST = 7;
    private static final int ALL_EXPENSES_YEAR = 8;
    private static final int ALL_EXPENSES_YEAR_TOTAL = 9;
    private static final int ALL_EXPENSES_MONTH = 10;
    private static final int ALL_EXPENSES_MONTH_TOTAL = 11;
    private static final int ALL_EXPENSES_CATEGORY_REPORT = 12;
    private static final int ALL_EXPENSES_CATEGORY_TOTAL = 13;

    // AUTHORITY is the symbolic name of the provider
    private static final String AUTHORITY = "com.bmp601.everyLiraContentProviderExpenses";

    // Create content URIs from the authority by appending path to database table
    public static final Uri EXPENSES_URI =
            Uri.parse("content://" + AUTHORITY + "/expenses");
    public static final Uri EXPENSES_ITEMS_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesItems");
    public static final Uri EXPENSES_SERVICES_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesServicesReport");
    public static final Uri EXPENSES_SERVICES_COST_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesServicesCost");
    public static final Uri EXPENSES_PAID_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesPaidReport");
    public static final Uri EXPENSES_PAID_COST_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesPaidCost");
    public static final Uri EXPENSES_YEAR_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesYear");
    public static final Uri EXPENSES_YEAR_TOTAL_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesYearTotal");
    public static final Uri EXPENSES_MONTH_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesMonth");
    public static final Uri EXPENSES_MONTH_TOTAL_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesMonthTotal");
    public static final Uri EXPENSES_CATEGORY_REPORT_URI =
            Uri.parse("content://" + AUTHORITY + "/expensesCategoryReport");
    public static final Uri EXPENSES_CATEGORY_TOTAL =
            Uri.parse("content://" + AUTHORITY + "/expensesCategoryTotal");


    // A content URI pattern matches content URIs using wildcard characters:
    // *: Matches a string of any valid characters of any length.
    // #: Matches a string of numeric characters of any length.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "expenses", ALL_EXPENSES);
        uriMatcher.addURI(AUTHORITY, "expenses/#", SINGLE_EXPENSE);
        uriMatcher.addURI(AUTHORITY, "expensesItems", ALL_EXPENSES_ITEMS);
        uriMatcher.addURI(AUTHORITY, "expensesServicesReport", ALL_EXPENSES_SERVICES_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesServicesCost", ALL_EXPENSES_SERVICES_COST);
        uriMatcher.addURI(AUTHORITY, "expensesPaidReport", ALL_EXPENSES_PAID_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesPaidCost", ALL_EXPENSES_PAID_COST);
        uriMatcher.addURI(AUTHORITY, "expensesYear", ALL_EXPENSES_YEAR);
        uriMatcher.addURI(AUTHORITY, "expensesYearTotal", ALL_EXPENSES_YEAR_TOTAL);
        uriMatcher.addURI(AUTHORITY, "expensesMonth", ALL_EXPENSES_MONTH);
        uriMatcher.addURI(AUTHORITY, "expensesMonthTotal", ALL_EXPENSES_MONTH_TOTAL);
        uriMatcher.addURI(AUTHORITY, "expensesCategoryReport", ALL_EXPENSES_CATEGORY_REPORT);
        uriMatcher.addURI(AUTHORITY, "expensesCategoryTotal", ALL_EXPENSES_CATEGORY_TOTAL);
    }

    // System calls onCreate() when it starts up the provider.
    @Override
    public boolean onCreate() {
        // get access to the database helper
        dbHelper = new MyDatabaseHelper(getContext());
        return false;
    }

    // Return the MIME type corresponding to a content URI
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
            case ALL_EXPENSES_ITEMS:
            case ALL_EXPENSES_SERVICES_REPORT:
            case ALL_EXPENSES_SERVICES_COST:
            case ALL_EXPENSES_PAID_REPORT:
            case ALL_EXPENSES_PAID_COST:
            case ALL_EXPENSES_YEAR:
            case ALL_EXPENSES_YEAR_TOTAL:
            case ALL_EXPENSES_MONTH:
            case ALL_EXPENSES_MONTH_TOTAL:
            case ALL_EXPENSES_CATEGORY_REPORT:
            case ALL_EXPENSES_CATEGORY_TOTAL:
                return "vnd.android.cursor.dir/vnd.com.bmp601.everyLiraContentProvider.expenses";
            case SINGLE_EXPENSE:
                return "vnd.android.cursor.item/vnd.com.bmp601.everyLiraContentProvider.expenses";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    // The insert() method adds a new row to the appropriate table,
    // using the values in the ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = db.insert(ExpensesDB.EXPENSES_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(EXPENSES_URI + "/" + id);
    }

    // The query() method must return a Cursor object
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExpensesDB.EXPENSES_TABLE);
        String query;
        // selectColumns is the part that is shared between multiple queries
        String selectColumns = "SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE + ", "
                + ExpensesDB.EXPENSES_KEY_DATE + ", " + ExpensesDB.CATEGORIES_KEY_NAME
                + ", " + ExpensesDB.ITEMS_KEY_NAME;
        Cursor c;

        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(ExpensesDB.EXPENSES_KEY_ID + "=" + id);
                break;

            // Item name and category name are needed, for that tow inner joins are needed too
            // (since the item id and category id are known)
            // Uncategorized items also needed to be included, for that UNION ALL is used
            // And the query results are sorted based on the descending purchase date
            case ALL_EXPENSES_ITEMS:
                query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN "
                        + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID
                        + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE + " ON "
                        + ExpensesDB.EXPENSES_KEY_CATEGORY_ID
                        + " = Categories._id UNION ALL SELECT Expenses._id, "
                        + ExpensesDB.EXPENSES_KEY_PRICE + ", " + ExpensesDB.EXPENSES_KEY_DATE
                        + ", \"None\" AS categoryName, " + ExpensesDB.ITEMS_KEY_NAME + " FROM "
                        + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE
                        + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id WHERE "
                        + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = 0 ORDER BY "
                        + ExpensesDB.EXPENSES_KEY_DATE + " DESC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Expenses of a year
            // Item name and category name are needed, for that tow inner joins are needed too
            // (since the item id and category id are known), but with a condition of having a specific year
            // (passed used selectionArgs)
            // Uncategorized items also needed to be included with the same condition, for that UNION ALL is used
            // And the query results are sorted based on the descending purchase date
            case ALL_EXPENSES_YEAR:
                query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN "
                        + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID
                        + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE + " ON "
                        + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = Categories._id WHERE "
                        + ExpensesDB.EXPENSES_KEY_DATE + " BETWEEN '" + selectionArgs[0] + "-01-01' AND '"
                        + selectionArgs[0] + "-12-31' "
                        + "UNION ALL SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE + ", "
                        + ExpensesDB.EXPENSES_KEY_DATE + ", \"None\" AS " + ExpensesDB.CATEGORIES_KEY_NAME + ", "
                        + ExpensesDB.ITEMS_KEY_NAME + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN "
                        + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id WHERE "
                        + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = 0 AND " + ExpensesDB.EXPENSES_KEY_DATE
                        + " BETWEEN '" + selectionArgs[0] + "-01-01' AND '" + selectionArgs[0] + "-12-31' ORDER BY "
                        + ExpensesDB.EXPENSES_KEY_DATE + " DESC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Total expenses of a year
            // Sum of prices of expenses of a specific year
            // (Note that we need to replace ',' with empty character to get a proper sum value)
            case ALL_EXPENSES_YEAR_TOTAL:
                query = "SELECT Expenses._id, SUM(replace(" + ExpensesDB.EXPENSES_KEY_PRICE + ", ',', '')) as Total FROM "
                        + ExpensesDB.EXPENSES_TABLE + " WHERE " + ExpensesDB.EXPENSES_KEY_DATE + " BETWEEN '"
                        + selectionArgs[0] + "-01-01' AND '"
                        + selectionArgs[0] + "-12-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Expenses of a month
            // Partially the same logic in ALL_EXPENSES_YEAR, but in addition to specifying the month
            // (All months are considered to have 31 days)
            // And the query results are sorted based on the descending purchase date
            case ALL_EXPENSES_MONTH:
                query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE
                        + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE
                        + " ON " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = Categories._id WHERE " + ExpensesDB.EXPENSES_KEY_DATE
                        + " BETWEEN '" + selectionArgs[0] + "-" + selectionArgs[1] + "-01' AND '" + selectionArgs[0] + "-"
                        + selectionArgs[1] + "-31' " + "UNION ALL SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE
                        + ", " + ExpensesDB.EXPENSES_KEY_DATE + ", \"None\" AS " + ExpensesDB.CATEGORIES_KEY_NAME + ", " + ExpensesDB.ITEMS_KEY_NAME
                        + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID
                        + " = Items._id WHERE " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = 0 AND " + ExpensesDB.EXPENSES_KEY_DATE + " BETWEEN '"
                        + selectionArgs[0] + "-" + selectionArgs[1] + "-01' AND '" + selectionArgs[0] + "-" + selectionArgs[1] + "-31' ORDER BY "
                        + ExpensesDB.EXPENSES_KEY_DATE + " DESC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Total expenses of a month
            // Partially the same logic in ALL_EXPENSES_YEAR_TOTAL, but in addition to specifying the month
            case ALL_EXPENSES_MONTH_TOTAL:
                query = "SELECT Expenses._id, SUM(replace(" + ExpensesDB.EXPENSES_KEY_PRICE + ", ',', '')) as Total FROM " + ExpensesDB.EXPENSES_TABLE
                        + " WHERE " + ExpensesDB.EXPENSES_KEY_DATE + " BETWEEN '" + selectionArgs[0] + "-" + selectionArgs[1] + "-01' AND '"
                        + selectionArgs[0] + "-" + selectionArgs[1] + "-31'";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Expenses of a category
            case ALL_EXPENSES_CATEGORY_REPORT:
                // If the category selected is None (Since None is not actually a category in the Categories table)
                if (Objects.equals(selectionArgs[0], "0"))
                    query = "SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE + ", " + ExpensesDB.EXPENSES_KEY_DATE
                            + ", \"None\" as " + ExpensesDB.CATEGORIES_KEY_NAME + ", " + ExpensesDB.ITEMS_KEY_NAME + " FROM "
                            + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID
                            + " = Items._id WHERE " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = '0' ORDER BY "
                            + ExpensesDB.EXPENSES_KEY_DATE + " DESC";
                else
                    query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE + " ON "
                            + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE + " ON "
                            + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = Categories._id WHERE " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID
                            + " = '" + selectionArgs[0] + "' ORDER BY " + ExpensesDB.EXPENSES_KEY_DATE + " DESC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Total expenses of a category
            case ALL_EXPENSES_CATEGORY_TOTAL:
                query = "SELECT Expenses._id, SUM(replace(" + ExpensesDB.EXPENSES_KEY_PRICE + ", ',', '')) as Total FROM "
                        + ExpensesDB.EXPENSES_TABLE + " WHERE " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = " + selectionArgs[0];
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Services expenses
            case ALL_EXPENSES_SERVICES_REPORT:
                query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE
                        + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE
                        + " ON " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = Categories._id WHERE " + ExpensesDB.ITEMS_KEY_IS_SERVICE
                        + " = 1 UNION ALL SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE + ", " + ExpensesDB.EXPENSES_KEY_DATE
                        + ", \"None\" AS categoryName, " + ExpensesDB.ITEMS_KEY_NAME + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN "
                        + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id WHERE " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID
                        + " = 0 AND " + ExpensesDB.ITEMS_KEY_IS_SERVICE + " = 1";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Services total cost
            case ALL_EXPENSES_SERVICES_COST:
                query = "SELECT Expenses._id, SUM(replace(" + ExpensesDB.EXPENSES_KEY_PRICE + ", ',', '')) as Total FROM " + ExpensesDB.EXPENSES_TABLE
                        + " INNER JOIN " + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id WHERE "
                        + ExpensesDB.ITEMS_KEY_IS_SERVICE + " = 1";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Purchased items expenses
            case ALL_EXPENSES_PAID_REPORT:
                query = selectColumns + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN " + ExpensesDB.ITEMS_TABLE + " ON "
                        + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id INNER JOIN " + ExpensesDB.CATEGORIES_TABLE + " ON "
                        + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = Categories._id WHERE " + ExpensesDB.EXPENSES_KEY_PRICE +
                        " != 0 UNION ALL SELECT Expenses._id, " + ExpensesDB.EXPENSES_KEY_PRICE + ", " + ExpensesDB.EXPENSES_KEY_DATE + ", \"None\" AS "
                        + ExpensesDB.CATEGORIES_KEY_NAME + ", " + ExpensesDB.ITEMS_KEY_NAME + " FROM " + ExpensesDB.EXPENSES_TABLE + " INNER JOIN "
                        + ExpensesDB.ITEMS_TABLE + " ON " + ExpensesDB.EXPENSES_KEY_ITEM_ID + " = Items._id WHERE " + ExpensesDB.EXPENSES_KEY_PRICE +
                        " != 0 AND " + ExpensesDB.EXPENSES_KEY_CATEGORY_ID + " = 0 ORDER BY date DESC";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            // Purchased items total cost
            case ALL_EXPENSES_PAID_COST:
                query = "SELECT Expenses._id, SUM(replace(" + ExpensesDB.EXPENSES_KEY_PRICE + ", ',', '')) as Total FROM "
                        + ExpensesDB.EXPENSES_TABLE + " WHERE " + ExpensesDB.EXPENSES_KEY_PRICE + " != 0";
                c = dbHelper.getWritableDatabase().rawQuery(query, null);
                return c;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;
    }

    // The delete() method deletes rows based on the selection or if an id is provided
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                selection = ExpensesDB.EXPENSES_KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int deleteCount = db.delete(ExpensesDB.EXPENSES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    // The update method() is same as delete() which updates multiple rows
    // based on the selection or a single row if the row id is provided. The
    // update method returns the number of updated rows
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_EXPENSES:
                //do nothing
                break;
            case SINGLE_EXPENSE:
                String id = uri.getPathSegments().get(1);
                selection = ExpensesDB.EXPENSES_KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(ExpensesDB.EXPENSES_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}

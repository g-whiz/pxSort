package io.pxsort.pxsort.sorting.filter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_NAME;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.TABLE_FILTERS;

/**
 * Interface to the database used to keep track of user and default Filters.
 *
 * Created by George on 2016-01-25.
 */
public class FilterDB {

    private SQLiteDatabase filterDB;
    private FilterDBOpenHelper filterDBOpenHelper;

    public FilterDB(Context context) throws IOException {
        filterDBOpenHelper = new FilterDBOpenHelper(context);
    }


    /**
     * Opens a connection to the underlying database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        filterDB = filterDBOpenHelper.getWritableDatabase();
    }


    /**
     * Closes the connection to the underlying database.
     */
    public void close() {
        filterDBOpenHelper.close();
        filterDB.close();
    }


    /**
     * Adds filter to the Filter database.
     *
     * @param filter the Filter to add
     * @return the updated list of Filters in this FilterDB
     */
    public List<Filter> addFilter(Filter filter) {
        filterDB.insert(TABLE_FILTERS, null, filter.toContentValues());

        return getFilters();
    }


    /**
     * Deletes filter from the Filter database.
     *
     * @param filter the Filter to delete
     * @return the updated list of Filters in this FilterDB
     */
    public List<Filter> deleteFilter(Filter filter) {
        filterDB.delete(TABLE_FILTERS, "WHERE " + COL_NAME + "=?", new String[]{filter.name});

        return getFilters();
    }

    /**
     * Returns all of the Filters in this FilterDB.
     *
     * @return the List of Filters in this FilterDB
     */
    public List<Filter> getFilters() {
        List<Filter> filtersList = new ArrayList<>();

        Cursor filtersCursor = filterDB.query(TABLE_FILTERS, null, null, null,
                null, null, COL_NAME);
        filtersCursor.moveToFirst();

        while (!filtersCursor.isAfterLast()) {
            Filter filter = Filter.fromCursor(filtersCursor);
            filtersList.add(filter);
            filtersCursor.moveToNext();
        }

        filtersCursor.close();
        return filtersList;
    }

}

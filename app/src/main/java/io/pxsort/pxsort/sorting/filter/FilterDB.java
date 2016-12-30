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

    private SQLiteDatabase filterSQLiteDB;
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
        filterSQLiteDB = filterDBOpenHelper.getWritableDatabase();
    }


    /**
     * Closes the connection to the underlying database.
     */
    public void close() {
        filterDBOpenHelper.close();
        filterSQLiteDB.close();
    }


    /**
     * Adds filter to the Filter database.
     *
     * @param filter the Filter to add
     * @return true if filter was successfully added to the database.
     * false if a name conflict was detected.
     */
    public boolean addFilter(Filter filter) {
        if (getFilters().contains(filter)) {
            return false;
        }

        filterSQLiteDB.insert(TABLE_FILTERS, null, filter.toContentValues());
        return true;
    }


    /**
     * Deletes filter from the Filter database.
     *
     * @param filter the Filter to delete
     * @return true if a row corresponding to filter was deleted. false otherwise
     */
    public boolean deleteFilter(Filter filter) {
        int rowsDeleted =
                filterSQLiteDB.delete(TABLE_FILTERS, COL_NAME + "=?", new String[]{filter.name});

        // since Filter names are PRIMARY KEYs in the DB, rowsDeleted should be 1 at most
        return rowsDeleted > 0;
    }

    /**
     * Returns all of the Filters in this FilterDB.
     *
     * @return the List of Filters in this FilterDB
     */
    public List<Filter> getFilters() {
        List<Filter> filtersList = new ArrayList<>();

        Cursor filtersCursor = filterSQLiteDB.query(TABLE_FILTERS, null, null, null,
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

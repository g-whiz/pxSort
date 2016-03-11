package io.github.pxsort.filter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2016-01-25.
 */
public class FilterDB {

    private SQLiteDatabase filterDB;
    private FilterDBOpenHelper filterDBHelper;

    public FilterDB(Context context) {
        filterDBHelper = new FilterDBOpenHelper(context);
    }

    public void open() throws SQLException {
        filterDB = filterDBHelper.getWritableDatabase();
    }

    public void close() {
        filterDBHelper.close();
    }

    /*Need to implement these for the Filters editor.*/
//    public List<Filter> addFilter(Filter filter) {
//        //// TODO: 2016-01-26
//
//
//        return getAllFilters();
//    }
//
//    public List<Filter> deleteFilter(Filter filter) {
//        //// TODO: 2016-01-26
//
//        return getAllFilters();
//    }

    public List<Filter> getAllFilters() {
        List<Filter> filtersList = new ArrayList<>();

        Cursor filtersCursor = filterDB.query(FilterDBOpenHelper.TABLE_FILTERS, null, null, null,
                null, null, FilterDBOpenHelper.COLUMN_NAME);
        filtersCursor.moveToFirst();

        while (!filtersCursor.isAfterLast()) {
            Filter filter = cursorToFilter(filtersCursor);
            filtersList.add(filter);
            filtersCursor.moveToNext();
        }

        return filtersList;
    }

    private Filter cursorToFilter(Cursor cursor) {
        return new Filter(cursor.getString(0), cursor.getInt(1), cursor.getInt(2),
                cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),
                cursor.getInt(7) != 0);
    }
}

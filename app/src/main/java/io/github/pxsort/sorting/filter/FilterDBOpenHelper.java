package io.github.pxsort.sorting.filter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_ALGORITHM;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_1;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_2;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_3;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_4;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_TYPE;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_COMPONENT;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_IS_BUILT_IN;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_NAME;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_NUM_COLS;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_NUM_ROWS;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_ORDER;
import static io.github.pxsort.sorting.filter.FilterDBConstants.COL_PARTITION_TYPE;
import static io.github.pxsort.sorting.filter.FilterDBConstants.TABLE_FILTERS;

/**
 * Helper class for opening the Filter database.
 *
 * A read-only database containing the app's built-in filters, included in the app's assets, is
 * copied into the app's local data directory if no preexisting filter database is detected.
 *
 * Created by George on 2016-01-25.
 */
public class FilterDBOpenHelper extends SQLiteOpenHelper {

    public static final String TAG = FilterDBOpenHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "filters.sqlite";
    public static final int DATABASE_VERSION = 2;

    private static final String DEFAULTS_ASSET_NAME = "db/" + DATABASE_NAME;

    //sql statement to create the database
    public static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_FILTERS + "("
                    + COL_NAME + " TEXT PRIMARY KEY NOT NULL,"
                    + COL_IS_BUILT_IN + " INTEGER NOT NULL"

                    + COL_ALGORITHM + " INTEGER NOT NULL,"
                    + COL_COMPONENT + " INTEGER NOT NULL,"
                    + COL_ORDER + " INTEGER NOT NULL,"

                    + COL_COMBINE_TYPE + " INTEGER NOT NULL,"
                    + COL_COMBINE_FUNC_1 + " INTEGER NOT NULL,"
                    + COL_COMBINE_FUNC_2 + " INTEGER NOT NULL,"
                    + COL_COMBINE_FUNC_3 + " INTEGER NOT NULL,"
                    + COL_COMBINE_FUNC_4 + " INTEGER NOT NULL,"

                    + COL_PARTITION_TYPE + " INTEGER NOT NULL,"
                    + COL_NUM_ROWS + " INTEGER NOT NULL,"
                    + COL_NUM_COLS + " INTEGER NOT NULL," +
                    ");";

    private Context context;

    private List<Filter> defaultFilters;

    /**
     * Sole constructor.
     *
     * @param context
     */
    public FilterDBOpenHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        defaultFilters = getDefaultFilters(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, "Filter database created.");

        SQLiteDatabase defaultsDB;
        insertDefaults(db);
        Log.d(TAG, "Default filters copied from assets.");
    }

    private void insertDefaults(SQLiteDatabase filtersDB) {
        for (Filter filter : defaultFilters) {
            filtersDB.insert(TABLE_FILTERS, null, filter.toContentValues());
        }
    }


    private List<Filter> getDefaultFilters(Context context) throws IOException {
        List<Filter> defaultFilters = new ArrayList<>();

        //get all rows of the table, sorted by name
        Cursor defaultsCursor = getDefaultsDB(context)
                .query(TABLE_FILTERS, null, null, null, null, null, COL_NAME);
        defaultsCursor.moveToFirst();

        //copy each row into filtersDB
        while (!defaultsCursor.isAfterLast()) {
            Filter f = Filter.fromCursor(defaultsCursor);
            defaultFilters.add(f);

            defaultsCursor.moveToNext();
        }

        defaultsCursor.close();
        return defaultFilters;
    }


    private SQLiteDatabase getDefaultsDB(Context context) throws IOException {

        //Copy filter DB in assets to cache (can't open DB directly from assets).
        File defaultsDBTemp = new File(context.getCacheDir(), DATABASE_NAME);
        if (defaultsDBTemp.exists()) {
            if (!defaultsDBTemp.delete()) {
                throw new IOException();
            }
        }
        if (!defaultsDBTemp.createNewFile()) {
            throw new IOException();
        }

        InputStream defaultsDBAsset = context.getAssets().open(DEFAULTS_ASSET_NAME);
        OutputStream defaultsDBTempStream = new FileOutputStream(defaultsDBTemp);

        IOUtils.copy(defaultsDBAsset, defaultsDBTempStream);

        defaultsDBAsset.close();
        defaultsDBTempStream.close();

        return SQLiteDatabase.openOrCreateDatabase(defaultsDBTemp, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all existing data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTERS);
        onCreate(db);
    }
}

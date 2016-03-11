package io.github.pxsort.filter;

import android.content.ContentValues;
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

/**
 * Helper class for opening the Filter database.
 * <p/>
 * Created by George on 2016-01-25.
 */
public class FilterDBOpenHelper extends SQLiteOpenHelper {

    public static final String TAG = FilterDBOpenHelper.class.getSimpleName();

    public static final String TABLE_FILTERS = "filters";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COMPONENT = "component";
    public static final String COLUMN_ORDERING = "ordering";
    public static final String COLUMN_ZIP_METHOD = "zip_method";
    public static final String COLUMN_BASE_OP = "base_op";
    public static final String COLUMN_NUM_ROWS = "num_rows";
    public static final String COLUMN_NUM_COLS = "num_cols";
    public static final String COLUMN_IS_DEFAULT = "is_default";

    public static final String DATABASE_NAME = "filters.sqlite";
    public static final int DATABASE_VERSION = 1;

    private static final String DEFAULTS_ASSET_NAME = "db/" + DATABASE_NAME;

    //sql statement to create the database
    public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_FILTERS
            + "(" + COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL,"
            + COLUMN_COMPONENT + " INTEGER NOT NULL,"
            + COLUMN_ORDERING + " INTEGER NOT NULL,"
            + COLUMN_ZIP_METHOD + " INTEGER NOT NULL,"
            + COLUMN_BASE_OP + " INTEGER NOT NULL,"
            + COLUMN_NUM_ROWS + " INTEGER NOT NULL,"
            + COLUMN_NUM_COLS + " INTEGER NOT NULL,"
            + COLUMN_IS_DEFAULT + " INTEGER NOT NULL);";

    private Context context;

    /**
     * Sole constructor.
     *
     * @param context
     */
    public FilterDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, "Filter database created.");

        SQLiteDatabase defaultsDB;

        try {
            //get default filters from assets and copy to filtersDB
            defaultsDB = getDefaultsDB();
            copyDefaults(defaultsDB, db);
            Log.d(TAG, "Default filters copied from assets.");

        } catch (IOException e) {
            Log.e(TAG, "An error occurred while creating the temporary default" +
                    " filter database. No default filters were added.", e);
        }


    }

    private void copyDefaults(SQLiteDatabase defaultsDB, SQLiteDatabase filtersDB) {
        //get all rows of the table, sorted by name
        Cursor defaultFilters = defaultsDB.query(TABLE_FILTERS, null, null, null, null, null,
                COLUMN_NAME);
        defaultFilters.moveToFirst();

        //copy each row into filtersDB
        while (!defaultFilters.isAfterLast()) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME, defaultFilters.getString(0));
            values.put(COLUMN_COMPONENT, defaultFilters.getInt(1));
            values.put(COLUMN_ORDERING, defaultFilters.getInt(2));
            values.put(COLUMN_ZIP_METHOD, defaultFilters.getInt(3));
            values.put(COLUMN_BASE_OP, defaultFilters.getInt(4));
            values.put(COLUMN_NUM_ROWS, defaultFilters.getInt(5));
            values.put(COLUMN_NUM_COLS, defaultFilters.getInt(6));
            values.put(COLUMN_IS_DEFAULT, defaultFilters.getInt(7));

            filtersDB.insert(TABLE_FILTERS, null, values);

            defaultFilters.moveToNext();
        }

        defaultFilters.close();
    }

    private SQLiteDatabase getDefaultsDB() throws IOException {

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

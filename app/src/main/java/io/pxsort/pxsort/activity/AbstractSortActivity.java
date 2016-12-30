package io.pxsort.pxsort.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.FilterDB;

/**
 * An abstract Activity providing pixel-sorting functionality and access to the Filter database.
 * <p>
 * Created by George on 2016-08-18.
 */
public abstract class AbstractSortActivity extends AppCompatActivity {

    private static final String TAG = AbstractSortActivity.class.getSimpleName();

    private PixelSortingContext sortingContext;
    private FilterDB filterDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSortingContext();
        initFilterDB();
        filterDB.open();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (filterDB != null)
            filterDB.close();

        if (sortingContext != null)
            sortingContext.recycle();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        filterDB.open();
    }


    private void initSortingContext() {
        // First check if sortingContext is already initialized
        if (sortingContext == null) {
            Uri imageUri = getIntent().getParcelableExtra(MainActivity.IMAGE_URI);
            try {
                sortingContext = new PixelSortingContext(this, imageUri);
            } catch (IOException e) {
                Log.e(TAG, "Error: a problem occurred while initializing the " +
                        "PixelSortingContext for this Activity", e);
                NavUtils.navigateUpFromSameTask(this);
                finish();
            }
        }
    }


    private void initFilterDB() {
        try {
            filterDB = new FilterDB(this);

        } catch (IOException e) {
            Log.e(TAG, "A fatal error occurred while accessing the Filter database.", e);
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }
    }


    /**
     * Returns this AbstractSortActivity's PixelSortingContext.
     *
     * @return
     */
    protected PixelSortingContext getSortingContext() {
        return sortingContext;
    }


    /**
     * Returns this AbstractSortActivity's FilterDB.
     *
     * @return
     */
    protected FilterDB getFilterDB() {
        return filterDB;
    }
}

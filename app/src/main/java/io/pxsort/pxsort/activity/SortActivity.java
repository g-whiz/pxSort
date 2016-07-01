package io.pxsort.pxsort.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.Filter;
import io.pxsort.pxsort.sorting.filter.FilterDB;
import io.pxsort.pxsort.util.FilterAdapter;

/**
 * Activity for pixel sorting.
 *
 * Created by George on 2016-02-16.
 */
public class SortActivity extends AppCompatActivity implements
        FilterAdapter.OnFilterSelectedListener,
        PixelSortingContext.OnImageReadyListener,
        PixelSortingContext.OnImageSavedListener {

    private static final String TAG = SortActivity.class.getSimpleName();


    // TODO: These should be in resources
    private static final int COLOR_LOADING = 0x40000000;
    private static final int COLOR_LOADED = 0x00000000;

    private ImageView imagePreviewView;

    // TODO: this should be a setting
    private static final int PREVIEW_SIZE = 500;

    private Filter activeFilter;

    private PixelSortingContext sortingContext;

    // Reference used to cancel any running, unneeded task.
    private WeakReference<PixelSortingContext.BitmapWorkerTask> previewLoaderTaskRef;

    // true once onWindowFocusChanged has been called once
    private boolean setupDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDone = false;

        activeFilter = null;
        previewLoaderTaskRef = null;

        setContentView(R.layout.activity_sort);
        imagePreviewView = (ImageView) findViewById(R.id.sort_image_preview_view);

        List<Filter> filters = null;
        try {
            initSortingContext();

            filters = getFiltersFromDB();
        } catch (IOException e) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        FilterAdapter adapter = new FilterAdapter(sortingContext, filters, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filter_tile_recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }


    private void initSortingContext() throws IOException {
        Uri imageUri = getIntent().getParcelableExtra(MainActivity.IMAGE_URI);
        try {
            sortingContext = new PixelSortingContext(this, imageUri);
        } catch (IOException e) {
            Log.e(TAG, "Error: a problem occurred while initializing the " +
                    "PixelSortingContext for this Activity", e);
            throw e;
        }
    }


    private List<Filter> getFiltersFromDB() throws IOException {
        FilterDB filterDB;
        try {
            filterDB = new FilterDB(this);
        } catch (IOException e) {
            Log.e(TAG, "A fatal error occurred while accessing the Filter database.", e);
            throw e;
        }

        filterDB.open();
        List<Filter> filters = filterDB.getFilters();
        filterDB.close();

        return filters;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && !setupDone) {
            // redraw thumbnails now that they are measured
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filter_tile_recycler_view);
            recyclerView.getAdapter().notifyDataSetChanged();

            updatePreview();

            setupDone = true;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (imagePreviewView.getDrawable() instanceof BitmapDrawable) {
            // recycle the unneeded Bitmap
            Bitmap toRecycle = ((BitmapDrawable) imagePreviewView.getDrawable()).getBitmap();
            imagePreviewView.setImageResource(R.color.primary_dark);

            toRecycle.recycle();
        }

        if (sortingContext != null)
            sortingContext.recycle();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        // since the preview bitmap is recycled onStop(), we have to retrieve it onRestart()
        updatePreview();
    }

    @Override
    public void onFilterSelected(boolean isSelected, Filter filter) {
        if (isSelected) {
            this.activeFilter = filter;
        } else {
            this.activeFilter = null;
        }

        updatePreview();
    }


    private void updatePreview() {
        if (previewLoaderTaskRef != null && previewLoaderTaskRef.get() != null) {
            // A previously dispatched task is still running. Cancel it before creating a new one.
            previewLoaderTaskRef.get().cancel(true);
        }

        imagePreviewView.setColorFilter(COLOR_LOADING);

        if (activeFilter == null) {
            // no active filter: display the original image
            previewLoaderTaskRef = new WeakReference<>(
                    sortingContext.getOriginalImage(
                            PREVIEW_SIZE,
                            PREVIEW_SIZE,
                            this)
            );
        } else {
            previewLoaderTaskRef = new WeakReference<>(
                    sortingContext.getPixelSortedImage(
                            activeFilter,
                            PREVIEW_SIZE,
                            PREVIEW_SIZE,
                            this)
            );
        }
    }


    /**
     * Navigates up to MainActivity.
     *
     * @param view
     */
    public void back(View view) {
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }


    /**
     * Not implemented.
     *
     * @param view
     */
    public void toggleFilterStack(View view) {
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }


    /**
     * Saves the image in sortingContext sorted with activeFilter.
     *
     * @param view
     */
    public void saveSortedImage(View view) {
        if (activeFilter == null) {
            Toast.makeText(SortActivity.this, "Please select a filter.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Applying filter and saving.", Toast.LENGTH_LONG).show();
        sortingContext.savePixelSortedImage(getApplicationContext(), activeFilter, this);

        NavUtils.navigateUpFromSameTask(this);
        finish();
    }

    @Override
    public void onImageReady(boolean success, Bitmap bitmap) {
        imagePreviewView.setColorFilter(COLOR_LOADED);

        if (success) {
            if (imagePreviewView.getDrawable() instanceof BitmapDrawable) {
                // recycle the unneeded Bitmap
                ((BitmapDrawable) imagePreviewView.getDrawable()).getBitmap().recycle();
            }

            imagePreviewView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getApplicationContext(), "Error: preview could not be generated.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onImageSaved(boolean success) {
        if (success) {
            Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error: image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }

}

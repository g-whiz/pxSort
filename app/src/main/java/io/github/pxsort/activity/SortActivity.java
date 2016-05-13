package io.github.pxsort.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.github.pxsort.R;
import io.github.pxsort.filter.Filter;
import io.github.pxsort.sorting.PixelSort;
import io.github.pxsort.sorting.filter.FilterDB;
import io.github.pxsort.util.FilterAdapter;
import io.github.pxsort.util.OldMedia;

/**
 * Activity for pixel sorting.
 *
 * Created by George on 2016-02-16.
 */
public class SortActivity extends AppCompatActivity
        implements FilterAdapter.OnFilterSelectionListener {

    private static final String MSG_SORTING = "Applying filter to image.";
    private static final String TITLE_SORTING = "Please Wait";
    private static final String TAG = SortActivity.class.getSimpleName();

    private ImageView previewView;

    private FilterDB filterDB;
    private Filter currentFilter;
    private FilterAdapter adapter;

    private Bitmap src;
    private Bitmap scaledSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        previewView = (ImageView) findViewById(R.id.sort_preview_image_view);

        filterDB = new FilterDB(this);
        filterDB.open();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.filter_tile_recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new FilterAdapter(filterDB.getFilters(), this);
        mRecyclerView.setAdapter(adapter);

        Uri fileUri = getIntent().getParcelableExtra(MainActivity.FILE_URI);
        loadImageInBackground(fileUri);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && src != null && scaledSrc == null) {
            scaledSrc = scaleBitmap(src, previewView.getWidth(),
                    previewView.getHeight());
        }

        super.onWindowFocusChanged(hasFocus);
    }

    private void loadImageInBackground(Uri fileUri) {
        OldMedia.loadImageAsync(getContentResolver(), fileUri,
                new OldMedia.OnImageLoadedListener() {
                    @Override
                    public void onImageLoaded(Bitmap bm) {
                        Toast.makeText(SortActivity.this, "Image loaded!",
                                Toast.LENGTH_SHORT).show();
                        src = bm;
                        adapter.setThumbnailSrc(bm);

                        //Scale bitmap if views have been measured
                        if (hasWindowFocus()) {
                            scaledSrc = scaleBitmap(bm, previewView.getWidth(),
                                    previewView.getHeight());
                            onUpdatePreview();
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(SortActivity.this, "Something went wrong while loading!",
                                Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    }
                });
    }

    private Bitmap scaleBitmap(Bitmap src, int maxWidth, int maxHeight) {
        int sourceWidth = src.getWidth();
        int sourceHeight = src.getHeight();

        int dstWidth;
        int dstHeight;

        if (sourceWidth >= sourceHeight) {
            dstHeight = maxHeight;
            dstWidth = Math.round((float) sourceWidth
                    / (float) sourceHeight * (float) maxWidth);
        } else {
            dstHeight = Math.round((float) sourceHeight
                    / (float) sourceWidth * (float) maxHeight);
            dstWidth = maxWidth;
        }
        //// TODO: 2016-02-27 dstHeight is always 0. fix this
        return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        filterDB.close();
        if (src != null) {
            src.recycle();
        }
        if (scaledSrc != null) {
            scaledSrc.recycle();
        }
    }

    @Override
    public void onFilterSelection(boolean isSelected, Filter filter) {
        if (isSelected) {
            this.currentFilter = filter;
        } else {
            this.currentFilter = null;
        }

        onUpdatePreview();
    }

    private void onUpdatePreview() {
        if (scaledSrc == null) {
            Log.w(TAG, "Update Preview: No source image to draw.");
            return;
        }
        if (currentFilter == null) {
            previewView.setImageBitmap(scaledSrc);
        } else {
            Bitmap sortedScaledSrc = scaledSrc.copy(Bitmap.Config.ARGB_8888, true);

            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(MSG_SORTING)
                    .setTitle(TITLE_SORTING)
                    .setCancelable(false)
                    .create();
            alertDialog.show();

            PixelSort.applyFilterAsync(sortedScaledSrc, currentFilter,
                    new PixelSort.OnFilterAppliedListener() {
                        @Override
                        public void onFilterApplied(Bitmap bitmap) {
                            Drawable previewDrawable = previewView.getDrawable();
                            Bitmap oldBitmap = null;

                            if (previewDrawable instanceof BitmapDrawable) {
                                oldBitmap = ((BitmapDrawable) previewDrawable).getBitmap();
                            }

                            previewView.setImageBitmap(bitmap);
                            alertDialog.dismiss();

                            //Recycle old bitmap if unneeded
                            if (oldBitmap != null && oldBitmap != scaledSrc) {
                                oldBitmap.recycle();
                            }
                        }
                    });
        }
    }

    private void navigateToMainActivity() {
        Intent upIntent = new Intent(SortActivity.this, MainActivity.class);
        navigateUpTo(upIntent);
    }

    public void back(View view) {
        navigateToMainActivity();
    }

    public void toggleFilterStack(View view) {
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    public void saveSortedImage(View view) {
        if (currentFilter == null) {
            Toast.makeText(SortActivity.this, "Please select a filter. :)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Applying filter.", Toast.LENGTH_LONG).show();
        navigateToMainActivity();

        //Apply filter & save
        PixelSort.applyFilterAsync(src, currentFilter, new PixelSort.OnFilterAppliedListener() {
            @Override
            public void onFilterApplied(Bitmap bitmap) {
                Toast.makeText(SortActivity.this, "Filter applied! Saving.",
                        Toast.LENGTH_LONG).show();
                OldMedia.saveImageAsync(SortActivity.this, src, new OldMedia.OnImageSavedListener() {
                    @Override
                    public void onImageSaved() {
                        Toast.makeText(SortActivity.this, "Saved!",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(SortActivity.this, "ERROR: Image failed to save.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

package io.github.pxsort.sorting;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import io.github.pxsort.sorting.filter.Filter;
import io.github.pxsort.sorting.sorter.PixelSorter;
import io.github.pxsort.util.MediaUtils;

/**
 * Interface for asynchronously running pixel sorting operations on some image.
 * <p/>
 * Created by George on 2016-05-04.
 */
public class PixelSortingContext {

    /**
     * No requirements on this dimension.
     */
    private static final int NO_REQ = Integer.MAX_VALUE;

    private static final Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;

    private static final String TAG = PixelSortingContext.class.getSimpleName();

    private final Map<Integer, Bitmap> bitmapMap;

    private final ContentResolver contentResolver;
    private final Uri imageUri;

    private final int imageWidth;
    private final int imageHeight;

    /**
     * @param context
     * @param imageUri
     * @throws FileNotFoundException if the Uri provided points to a nonexistent file
     */
    public PixelSortingContext(Context context, Uri imageUri) throws IOException {
        bitmapMap = new ConcurrentHashMap<>();
        contentResolver = context.getApplicationContext().getContentResolver();
        this.imageUri = imageUri;

        BitmapFactory.Options opts = MediaUtils.decodeBounds(contentResolver, imageUri);
        imageWidth = opts.outWidth;
        imageHeight = opts.outHeight;
    }


    /**
     * Loads this PixelSortingContext's image, pixel sorts it, then returns it via the provided
     * listener.
     *
     * @param filter    The Filter to pixel sort the image with
     * @param reqWidth  the required width of the Bitmap
     * @param reqHeight the required width of the Bitmap
     * @param listener  the listener to return the bitmap through
     * @return An AsyncTask that can be used to cancel the operation if the bitmap is no
     * longer needed.
     * @throws FileNotFoundException
     */
    public BitmapWorkerTask getPixelSortedImage(
            final Filter filter, final int reqWidth, final int reqHeight,
            final OnImageReadyListener listener) {
        return (BitmapWorkerTask) new BitmapWorkerTask() {

            @Override
            protected Bitmap doInBackground(Void... params) {

                Bitmap mutableSrc = null;
                try {
                    mutableSrc = retrieveOriginalImage(reqWidth, reqHeight, true);
                } catch (IOException e) {
                    Log.e(TAG, "Error while retrieving Bitmap: ", e);
                    cancel(false);
                    return null;
                }

                // Scale down the bitmap as much as possible to maximize sort performance.
                Bitmap scaledMutSrc = scaleDownBitmap(mutableSrc, reqWidth, reqHeight);
                if (mutableSrc != scaledMutSrc) {
                    mutableSrc.recycle();
                }
                PixelSorter.fromFilter(filter).applyTo(scaledMutSrc);

                return scaledMutSrc;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(true, bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                if (bitmap != null)
                    bitmap.recycle();

                listener.onImageReady(false, null);
            }
        }.executeOnExecutor(EXECUTOR);
    }


    public AsyncTask<Void, Void, Bitmap> getPixelSortedImage(
            final Filter filter, final OnImageReadyListener listener) {
        return getPixelSortedImage(filter, NO_REQ, NO_REQ, listener);
    }


    public BitmapWorkerTask getOriginalImage(
            final int reqWidth, final int reqHeight, final OnImageReadyListener listener) {
        return (BitmapWorkerTask) new BitmapWorkerTask() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = retrieveOriginalImage(reqWidth, reqHeight, true);
                } catch (IOException e) {
                    Log.e(TAG, "Error while retrieving Bitmap: ", e);
                    cancel(false);
                    return null;
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(true, bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                if (bitmap != null) {
                    bitmap.recycle();
                }

                listener.onImageReady(false, null);
            }
        }.executeOnExecutor(EXECUTOR);
    }


    public AsyncTask<Void, Void, Bitmap> getOriginalImage(final OnImageReadyListener listener) {
        return getOriginalImage(NO_REQ, NO_REQ, listener);
    }


    /**
     * Saves the full-resolution pixel-sorted image to file.
     */
    public void savePixelSortedImage(
            final Context context, final Filter filter, final OnImageSavedListener listener) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Bitmap bitmap;
                try {
                    bitmap = MediaUtils.loadImage(contentResolver, imageUri, 1, true);
                } catch (IOException e) {
                    Log.e(TAG, "Error while loading Bitmap to save: ", e);
                    cancel(false);
                    return null;
                }

                PixelSorter.fromFilter(filter).applyTo(bitmap);
                MediaUtils.saveImage(context, bitmap);

                bitmap.recycle();
                return null;
            }

            @Override
            protected void onCancelled(Void aVoid) {
                listener.onImageSaved(false);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onImageSaved(true);
            }
        }.executeOnExecutor(EXECUTOR);
    }


    private Bitmap retrieveOriginalImage(int reqWidth, int reqHeight, boolean isMutable)
            throws IOException {
        int inSampleSize = calculateInSampleSize(reqWidth, reqHeight);

        if (!bitmapMap.containsKey(inSampleSize)) {
            // load the appropriate version of the bitmap into memory
            Bitmap bitmap = MediaUtils.loadImage(contentResolver, imageUri, inSampleSize, false);
            bitmapMap.put(inSampleSize, bitmap);
        }

        if (isMutable) {
            return bitmapMap.get(inSampleSize).copy(Bitmap.Config.ARGB_8888, true);

        } else {
            return bitmapMap.get(inSampleSize);
        }
    }


    private Bitmap scaleDownBitmap(Bitmap source, int reqWidth, int reqHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        if (reqWidth >= sourceWidth || reqHeight >= sourceHeight) {
            return source;
        }

        int dstWidth;
        int dstHeight;

        if (sourceWidth >= sourceHeight) {
            dstHeight = reqHeight;
            dstWidth = Math.round((float) sourceWidth
                    / (float) sourceHeight * (float) reqWidth);
        } else {
            dstHeight = Math.round((float) sourceHeight
                    / (float) sourceWidth * (float) reqHeight);
            dstWidth = reqWidth;
        }

        return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, false);
    }


    // Sourced from https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    private int calculateInSampleSize(int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // calculateBSTHeight and width larger than the requested calculateBSTHeight and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * Recycle any bitmaps stored in this PixelSortingContext
     */
    public void recycle() {
        for (Bitmap bitmap : bitmapMap.values()) {
            bitmap.recycle();
        }
        bitmapMap.clear();
    }


    /**
     * AsyncTask renamed for convenience/clarity.
     */
    public abstract class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
    }


    /**
     * Interface to asynchronously return original and pixel-sorted images.
     */
    public interface OnImageReadyListener {

        /**
         * Called once the requested image is ready.
         *
         * @param success true if loading the image was successful, false otherwise
         * @param bitmap the Bitmap containing the image, undefined if success == false
         */
        void onImageReady(boolean success, Bitmap bitmap);
    }


    public interface OnImageSavedListener {

        /**
         * Called once the pixel sorted image is saved.
         *
         * @param success true if saving was successful, false otherwise
         */
        void onImageSaved(boolean success);
    }
}

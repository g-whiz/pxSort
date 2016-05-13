package io.github.pxsort.sorting;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.pxsort.sorting.filter.Filter;
import io.github.pxsort.sorting.sorter.PixelSorter;
import io.github.pxsort.util.Media;

/**
 * Interface for asynchronously running pixel sorting operations on some image.
 * <p/>
 * Created by George on 2016-05-04.
 */
public class PixelSortingContext {
    // TODO: 2016-05-04 This class is responsible for:
    //                  - Loading scaled versions of the image at the provided URI/File/?
    //                  - Sorting the image at the provided URI/File/?
    //                  - Saving sorted versions of the image  at the provided URI/File/?
    //                  NOTE: The actual loading and saving should be done in a refactored version of util.Media
    //                          - One method to load scaled versions of the provided image
    //                          - One method to save sorted versions of the provided image (and add them to the gallery)

    private final ContentResolver contentResolver;
    private final Uri imageUri;

    private Executor executor;

    /**
     * @param context
     * @param imageUri
     * @throws FileNotFoundException if the Uri provided points to a nonexistent file
     */
    public PixelSortingContext(Context context, Uri imageUri) throws FileNotFoundException {
        contentResolver = context.getApplicationContext().getContentResolver();
        this.imageUri = imageUri;

        int threads = Runtime.getRuntime().availableProcessors();
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        executor = new ThreadPoolExecutor(threads, threads, 30, TimeUnit.SECONDS, queue);
    }


    public void getPixelSortedImage(
            final Filter filter, final int reqWidth, final int reqHeight,
            final OnImageReadyListener listener) throws FileNotFoundException {
        final InputStream stream = contentResolver.openInputStream(imageUri);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = Media.loadImage(stream, reqWidth, reqHeight);
                PixelSorter.from(filter).applyTo(bitmap);
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                try {
                    if (bitmap != null)
                        bitmap.recycle();

                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }
            }
        }.executeOnExecutor(executor);
    }


    public void getPixelSortedImage(final Filter filter, final OnImageReadyListener listener)
            throws FileNotFoundException {
        final InputStream stream = contentResolver.openInputStream(imageUri);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = Media.loadImage(stream);
                PixelSorter.from(filter).applyTo(bitmap);
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                try {
                    if (bitmap != null)
                        bitmap.recycle();

                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }
            }
        }.executeOnExecutor(executor);
    }


    public void getOriginalImage(
            final int reqWidth, final int reqHeight, final OnImageReadyListener listener)
            throws FileNotFoundException {
        final InputStream stream = contentResolver.openInputStream(imageUri);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = Media.loadImage(stream, reqWidth, reqHeight);
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                try {
                    if (bitmap != null)
                        bitmap.recycle();

                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }
            }
        }.executeOnExecutor(executor);
    }


    public void getOriginalImage(final OnImageReadyListener listener) throws FileNotFoundException {
        final InputStream stream = contentResolver.openInputStream(imageUri);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = Media.loadImage(stream);
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onImageReady(bitmap);
            }

            @Override
            protected void onCancelled(Bitmap bitmap) {
                try {
                    if (bitmap != null)
                        bitmap.recycle();

                    if (stream != null)
                        stream.close();
                } catch (IOException ignored) {
                }
            }
        }.executeOnExecutor(executor);
    }


    /**
     * Saves the full-resolution pixel-sorted image to file.
     *
     * @param closeOnCompletion close any open Streams once the pixel-sorted image has been saved.
     */
    public void savePixelSortedImage(boolean closeOnCompletion) {

    }


    private InputStream getStream() throws IOException {
        return contentResolver.openInputStream(imageUri);
    }


    /**
     * Interface to asynchronously return original and pixel-sorted images.
     */
    public interface OnImageReadyListener {

        /**
         * Called once the requested image is ready.
         *
         * @param bitmap the Bitmap containing the image
         */
        void onImageReady(Bitmap bitmap);
    }
}

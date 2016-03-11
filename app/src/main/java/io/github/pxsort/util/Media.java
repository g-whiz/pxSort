package io.github.pxsort.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.pxsort.R;

/** A class containing static methods for IO operations.
 *
 * Created by George on 2016-01-16.
 */
public class Media {

    public static final String TAG = Media.class.getSimpleName();
    private static final String FILE_NAME_SUFFIX = ".png";

    /**
     * Saves a Bitmap to external storage
     * @param c
     * @param bitmap
     * @param listener
     */
    public static void saveImageAsync(final Context c, final Bitmap bitmap,
                                      final OnImageSavedListener listener) {

        // Save bitmap using an AsyncTask
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return saveImage(c, bitmap);
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                if (successful) {
                    listener.onImageSaved();
                } else {
                    listener.onError();
                }
            }
        }.execute();
    }

    public interface OnImageSavedListener {

        /**
         * Called on successful comlpetion of saveImageAsync.
         */
        void onImageSaved();

        /**
         * Called if an exception is thrown during saving.
         */
        void onError();
    }

    private static boolean saveImage(Context c, Bitmap bitmap) {
        String appName = c.getString(R.string.app_name);

        // Get the directory for the user's public pictures directory.
        File albumDir = getImageStorageDir(c);
        if (albumDir == null) {
            return false;
        }

        //Compress bitmap with PNG encoding
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        String imgName = appName.toUpperCase() + "_" + getDateTimeString() + FILE_NAME_SUFFIX;
        File imgFile = new File(albumDir, imgName);

        FileOutputStream fOut;
        try {
            if (! imgFile.createNewFile()) {
                Log.e(TAG, "File " + imgName + "could not be created. Image was not saved.");
                return false;
            }
            fOut = new FileOutputStream(imgFile);
            fOut.write(out.toByteArray());
            fOut.close();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "An error occurred while writing the image to file " +
                    "(" + imgName + "). Image was not saved.", e);
            return false;
        }

        addImageToGallery(c, Uri.fromFile(imgFile));
        return true;
    }


    public static File getImageStorageDir(Context c) {
        String appName = c.getString(R.string.app_name);

        // Get the directory for the user's public pictures directory.
        File albumDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appName);
        if (!albumDir.exists()) {
            boolean success = albumDir.mkdir();
            if (!success) {
                Log.e(TAG, "The directory " + albumDir.getPath() + " failed to be created.");
                return null;
            }
        }

        return albumDir;
    }

    public static File getNewImageFile(Context c) {
        String prefix = "IMG_" + getDateTimeString();
        File imageFile;
        try {
            imageFile = File.createTempFile(prefix, FILE_NAME_SUFFIX, getImageStorageDir(c));
            return imageFile;
        } catch (IOException e) {
            Log.e(TAG, "The image file failed to be created.");
            return null;
        }
    }

    private static String getDateTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        return dateFormat.format(date);
    }

    /**
     * Add the image at the specified Uri to the media gallery.
     *
     * @param c
     * @param imgUri
     */
    public static void addImageToGallery(Context c, Uri imgUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imgUri);
        c.sendBroadcast(mediaScanIntent);
    }


    /**
     * Loads a Bitmap from Uri in the background.
     *
     * @param contentResolver
     * @param imageUri URI of the location of the source image.
     * @param listener The OnImageLoadedListener to return the loaded Bitmap or thrown exception to.
     * @return
     */
    public static void loadImageAsync(final ContentResolver contentResolver, final Uri imageUri,
                                 final OnImageLoadedListener listener) {

        // Load bitmap using an AsyncTask

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return loadImage(contentResolver, imageUri, listener);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) {
                    listener.onError();
                } else {
                    listener.onImageLoaded(bitmap);
                }
            }

        }.execute();

    }

    private static Bitmap loadImage(ContentResolver contentResolver, Uri imageUri,
                                    OnImageLoadedListener listener) {
        Bitmap bitmap;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;

        try {
            InputStream is = contentResolver.openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (IOException e) {
            Log.e(TAG, "An error occurred while loading the image from file." +
                    " The image was not loaded.", e);
            return null;
        }

        return bitmap;
    }

    /**
     * Callback interface for asynchronous image loading.
     * All methods are invoked on the UI thread.
     */
    public interface OnImageLoadedListener {
        /**
         * Called when an image has been successfully loaded into a Bitmap.
         * @param bm The loaded Bitmap.
         */
        void onImageLoaded(Bitmap bm);

        /**
         * Called if an exception is thrown during loading.
         */
        void onError();
    }

}

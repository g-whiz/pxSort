package io.github.pxsort;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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

/** A class containing static methods for IO operations.
 *
 * Created by George on 2016-01-16.
 */
public class MediaUtils {

    public static final String TAG = MediaUtils.class.getSimpleName();

    //loadFilters()
    //saveFilters()

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

            private static final String ALBUM_NAME = "pxsrt";
            private static final String SUFFIX = ".png";

            private String appName;

            @Override
            protected void onPreExecute() {
                appName = c.getString(R.string.app_name);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return saveImage(c.getContentResolver(), bitmap, appName, ALBUM_NAME, SUFFIX);
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

    private static boolean saveImage(ContentResolver contentResolver, Bitmap bitmap, String appName,
                                     String album_name, String file_suffix) {

        // Get the directory for the user's public pictures directory.
        File albumDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), album_name);
        if (!albumDir.exists()) {
            if (!albumDir.mkdirs()){
                Log.e(TAG, "Directory failed to be created.");
                return false;
            }
        }

        //Compress bitmap with PNG encoding
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        String imgName = appName + "_" + getDateTimeString() + file_suffix;
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

        addImageToGallery(imgFile.getAbsolutePath(), contentResolver);
        return true;
    }


    private static String getDateTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        return dateFormat.format(date);
    }

    private static void addImageToGallery(String filePath, ContentResolver contentResolver) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.CONTENT_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

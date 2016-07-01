package io.pxsort.pxsort.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import io.pxsort.pxsort.R;

/** A class containing static methods for IO operations.
 *
 * Created by George on 2016-01-16.
 */
public abstract class MediaUtils {

    public static final String TAG = MediaUtils.class.getSimpleName();
    private static final String FILE_NAME_SUFFIX = ".png";


    /**
     * Saves a Bitmap to external storage.
     *
     * @param c The context in which to save the Bitmap.
     * @param bitmap The Bitmap to save.
     */
    public static boolean saveImage(Context c, Bitmap bitmap) {
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

        // write the bitmap to the newly created file
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


    private static String getDateTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        return dateFormat.format(date);
    }


    /**
     * Create a new File to store an image at.
     *
     * @param c The context in which to create the new File.
     * @return the new File
     */
    public static File createNewImageFile(Context c) {
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
     * Decodes the bounds of the image data in imgStream and returns a BitmapFactory.Options
     * containing the decoded bounds.
     *
     * @param contentResolver the ContentResolver to decode the image with
     * @param imageUri the Uri of the image to decode
     * @return
     */
    public static BitmapFactory.Options decodeBounds(
            ContentResolver contentResolver, Uri imageUri) throws IOException {
        InputStream imageStream = contentResolver.openInputStream(imageUri);

        // First calculate the maximum inSampleSize for this bitmap.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);

        if (imageStream != null) {
            imageStream.close();
        }
        return options;
    }


    /**
     * Loads the image located at the given Uri with the given mutability and inSampleSize.
     *
     * @param contentResolver the ContentResolver to load the image with
     * @param imageUri the Uri of the image to load
     * @param inSampleSize the inSampleSize to decode the bitmap with
     * @param isMutable true for the returned bitmap to be mutable, false otherwise
     */
    public static Bitmap loadImage(ContentResolver contentResolver, Uri imageUri,
                                   int inSampleSize, boolean isMutable) throws IOException {
        InputStream imageStream = contentResolver.openInputStream(imageUri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inMutable = isMutable;
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, options);

        try {
            if (imageStream != null) {
                imageStream.close();
            }
        } catch (IOException e) {
            // to avoid a potential memory leak
            if (bitmap != null)
                bitmap.recycle();
            throw e;
        }

        return bitmap;
    }
}

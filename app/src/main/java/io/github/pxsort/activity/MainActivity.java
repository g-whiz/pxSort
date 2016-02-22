package io.github.pxsort.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.pxsort.R;

/**
 * Activity for the main menu.
 *
 * Created by George on 2016-02-17.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PICTURE = 2;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * OnClick method: Opens image picker.
     */
    public void pickImage(View view){
        if (view.getId() == R.id.button_load_image) {

            Log.d(TAG, "Opening image picker...");
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE);
        }
    }
    
    
    public void openFilterManager(View view) {

        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }


    public void takePicture(View view) {
        if (view.getId() == R.id.button_snap) {

            Log.d(TAG, "Opening external camera...");
//            // create Intent to take a picture and return control to the calling application
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//
//            // start the image capture Intent
//            startActivityForResult(intent, TAKE_PICTURE);

        }

    }

//    public static final int MEDIA_TYPE_IMAGE = 1;
//
//    /** Create a file Uri for saving an image or video */
//    private static Uri getOutputMediaFileUri(int type){
//        return Uri.fromFile(getOutputMediaFile(type));
//    }
//
//    /** Create a File for saving an image or video */
//    private static File getOutputMediaFile(int type){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}

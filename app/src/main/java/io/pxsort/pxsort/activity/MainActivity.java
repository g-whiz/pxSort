package io.pxsort.pxsort.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.util.MediaUtils;

/**
 * Activity for the main menu.
 *
 * Created by George on 2016-02-17.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String IMAGE_URI = "file_uri";

    private static final String DEFAULT_IMG_NAME = "default.jpg";
    private static final String DEFAULT_IMG_ASSET = "img/" + DEFAULT_IMG_NAME;

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PICTURE = 2;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * OnClick method: Opens image picker.
     */
    public void pickImage(View view){
        if (view.getId() == R.id.button_load) {

            Log.d(TAG, "Opening image picker...");
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE);
        }
    }
    
    
    public void openFilterManager(View view) {
        Intent filterMgrIntent = new Intent(this, FilterManagerActivity.class);

        Uri imgUri = getDefaultImgUri();
        filterMgrIntent.putExtra(IMAGE_URI, imgUri);

        startActivity(filterMgrIntent);
    }


    private Uri getDefaultImgUri() {
        File defaultImg = new File(getCacheDir(), DEFAULT_IMG_NAME);

        if (!defaultImg.exists()) {
            try {
                if (!defaultImg.createNewFile()) {
                    throw new IOException();
                }

                InputStream defaultImgIS = getAssets().open(DEFAULT_IMG_ASSET);
                OutputStream defaultImgOS = new FileOutputStream(defaultImg);

                IOUtils.copy(defaultImgIS, defaultImgOS);

                defaultImgIS.close();
                defaultImgOS.close();

            } catch (IOException e) {
                Log.e(TAG, "Unable to retrieve default image.", e);
                finish();
                return null;
            }
        }

        return Uri.fromFile(defaultImg);
    }


    public void takePicture(View view) {
        if (view.getId() == R.id.button_snap) {

            Log.d(TAG, "Opening external camera...");
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            imageUri = getOutputImgFileUri(); // create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // set the image file name

            // start the image capture Intent
            startActivityForResult(intent, TAKE_PICTURE);
        }

    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputImgFileUri() {
        return Uri.fromFile(MediaUtils.createNewImageFile(this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PICK_IMAGE:
                    imageUri = data.getData();

                case TAKE_PICTURE:
                    MediaUtils.addImageToGallery(this, imageUri);
            }

            Intent sortIntent = new Intent(this, SortActivity.class);
            sortIntent.putExtra(IMAGE_URI, imageUri);
            startActivity(sortIntent);
        }
    }
}

package io.github.pxsrt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    //TODO -button to take a photo then sort it
    //TODO -button to choose photo then sort
    //TODO -horizontal scrollview below SortView containing panels for each different sort
    //TODO (what to do after one is selected?), review and improve concurrency techniques to
    //TODO improve UI responsiveness and sort performance -
    //TODO -two fragments: one containing the SortView, one containing the Sort 'fine tuning' View

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String IMG_PATH = "IMG_PATH";

    public static final int SELECT_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens image picker.
     */
    public void pickImage(View v){
        if (v.getId() == R.id.button_load_image) {
            Log.d(TAG, "Opening Image picker...");
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), SELECT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);

            Log.d(TAG, "Image selected. Path: " + imgPath);
            cursor.close();

            Intent intent = new Intent(this, SortActivity.class);
            intent.putExtra(IMG_PATH, imgPath);

            Log.d(TAG, "Starting SortActivity.");
            startActivity(intent);
        }
    }
}

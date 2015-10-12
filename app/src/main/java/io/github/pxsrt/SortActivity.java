package io.github.pxsrt;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import io.github.pxsrt.presets.Preset;
import io.github.pxsrt.presets.PresetManager;
import io.github.pxsrt.util.SystemUiHider;
import io.github.pxsrt.widget.PresetEditView;
import io.github.pxsrt.widget.PresetSelectorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SortActivity extends Activity implements PresetSelectorView.Listener,
        View.OnClickListener {

    public static final String TAG = SortActivity.class.getSimpleName();

    private static final BitmapFactory.Options bmOpts = new BitmapFactory.Options();
        static {
            bmOpts.inMutable = true;
        }

    private PresetManager presetManager;
    private ImageView sortView;
    private PresetEditView presetEditView;
    private PresetSelectorView presetSelectorView;
    private View presetEditScrollView;
    private View presetEditButton;
    private View presetEditButtonBar;

    private volatile Bitmap sourceBitmap;
    private volatile Bitmap scaledSourceBitmap;

    private volatile Preset activePreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sortView = (ImageView) findViewById(R.id.sort_view);
        presetSelectorView = (PresetSelectorView) findViewById(R.id.preset_selector_view);
        presetSelectorView.setListener(this);

        presetEditView = (PresetEditView) findViewById(R.id.preset_edit_view);
        presetEditView.setTranslationY(presetEditView.getHeight());
        presetEditScrollView = findViewById(R.id.preset_edit_scroll_view);

        sourceBitmap = null;
        presetManager = new PresetManager(this);
        activePreset = null;

        presetEditButton = findViewById(R.id.edit_preset_button);
        presetEditButton.setOnClickListener(this);
        presetEditButtonBar = findViewById(R.id.preset_edit_button_bar);
        findViewById(R.id.commit_edit_button).setOnClickListener(this);
        findViewById(R.id.cancel_edit_button).setOnClickListener(this);

        for (Preset preset : presetManager.getPresets()) {
            presetSelectorView.addPreset(preset);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String imgPath = getIntent().getStringExtra(MainActivity.IMG_PATH);
        new LoadBitmapWorkerTask().execute(imgPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSelectPreset(Preset preset) {
        activePreset = preset;
        presetEditView.setPreset(preset);
        if (sourceBitmap != null) {
            updateSortView();
        }
    }

    @Override
    public void onRemovePreset(Preset preset) {
        if (activePreset.equals(preset)) {
            activePreset = null;
            updateSortView();
        }
        presetManager.removePreset(preset);
    }

    @Override
    public void onCreateNewPreset() {
        Log.d(TAG, "Creating new preset.");
        presetEditView.setPreset(null);
        activePreset = presetEditView.getPreset();
        presetSelectorView.addPreset(activePreset);
        showPresetEditView(true);
        updateSortView();
    }

    private void showPresetEditView(boolean isShowing) {
        if (isShowing) {
            presetEditButton.setVisibility(View.GONE);
            presetEditButtonBar.setVisibility(View.VISIBLE);
            presetEditScrollView.setVisibility(View.VISIBLE);
            presetEditScrollView.animate()
                    .translationY(0)
                    .setDuration(500L)
                    .start();
        } else {
            presetEditScrollView.animate()
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            presetEditButtonBar.setVisibility(View.GONE);
                            presetEditButton.setVisibility(View.VISIBLE);
                            presetEditScrollView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .translationY(presetEditView.getHeight())
                    .setDuration(500L)
                    .start();

        }
    }

    private void updateSortView() {
        new LoadBitmapWorkerTask().execute((String) null);
    }


    private class LoadBitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private int sortViewHeight;
        private int sortViewWidth;
        private String path;

        @Override
        protected void onPreExecute() {
            sortViewHeight = sortView.getHeight();
            sortViewWidth = sortView.getWidth();
        }

        /*Passing (String[]) null as the path parameter to LoadBitmapWorkerTask.execute() will result
         * in the task skipping loading a bitmap from a path. Use whenever a bitmap is already
         * loaded. */
        @Override
        protected Bitmap doInBackground(String... params) {
            path = params[0];
            if (path != null) {
                sourceBitmap = BitmapFactory.decodeFile(path, bmOpts);
            }

            int sourceWidth = sourceBitmap.getWidth();
            int sourceHeight = sourceBitmap.getHeight();

            int dstWidth;
            int dstHeight;

            if (sourceWidth >= sourceHeight) {
                dstHeight = sortViewHeight;
                dstWidth = Math.round((float) sourceWidth
                        / (float) sourceHeight * (float) sortViewWidth);
            } else {
                dstHeight = Math.round((float) sourceHeight
                        / (float) sourceWidth * (float) sortViewHeight);
                dstWidth = sortViewWidth;
            }

            if (activePreset != null) {
                Bitmap scaledSortedBM =
                        Bitmap.createScaledBitmap(sourceBitmap, dstWidth, dstHeight, false);
                activePreset.getSorter().apply(scaledSortedBM);
                return scaledSortedBM;
            }

            return Bitmap.createScaledBitmap(sourceBitmap, dstWidth, dstHeight, false);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            sortView.setImageBitmap(bitmap);
            if (path != null) {
                presetSelectorView.setThumbnailSource(sourceBitmap);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_preset_button:
                presetEditView.setPreset(activePreset);
                if (activePreset != null) {
                    showPresetEditView(true);
                }
                return;

            case R.id.commit_edit_button:
                activePreset = presetEditView.getPreset();
                presetSelectorView.replaceSelectedPreset(activePreset);
                presetManager.removePreset(presetSelectorView.getSelectedPreset());
                presetManager.addPreset(activePreset);
                showPresetEditView(false);
                return;

            case R.id.cancel_edit_button:
                activePreset = presetSelectorView.getSelectedPreset();
                presetEditView.setPreset(activePreset);
                showPresetEditView(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_image:
                saveSortedImage();
                return true;
        }
        return false;
    }

    private void saveSortedImage() {
        if (activePreset != null) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                new SaveBitmapWorkerTask().execute();
            }
        }
    }

    private Context getActivityContext() {
        return this;
    }

    private class SaveBitmapWorkerTask extends AsyncTask<Void, Boolean, Boolean> {

        private static final String TITLE_SAVING = "Saving";
        private static final String MSG_SAVING = "Saving image to file, please wait...";
        private static final String TITLE_SAVED = "Saved";
        private static final String MSG_SAVED = "Your image has been successfully saved!\nPress " +
                "OK to go back to the main menu.";
        private static final String TITLE_SAVE_FAILED = "Not Saved";
        private static final String MSG_SAVE_FAILED = "An error occurred while saving your image. " +
                "It was not saved.\nPress OK to go back to the main menu.";

        private static final String ALBUM_NAME = "PicSort";
        private static final String SUFFIX = ".png";
        private String appName;

        private Context c = getActivityContext();
        private AlertDialog alertDialog;


        @Override
        protected void onPreExecute() {
            alertDialog = (new AlertDialog.Builder(c))
                    .setMessage(MSG_SAVING)
                    .setTitle(TITLE_SAVING)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
            appName = c.getString(R.string.app_name);
        }

        @Override
        protected void onPostExecute(Boolean isSaved) {
            alertDialog.dismiss();
            if (isSaved) {
                alertDialog = (new AlertDialog.Builder(c))
                        .setTitle(TITLE_SAVED)
                        .setMessage(MSG_SAVED)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigateUpToMainActivity();
                            }
                        })
                        .create();
            } else {
                alertDialog = (new AlertDialog.Builder(c))
                        .setTitle(TITLE_SAVE_FAILED)
                        .setMessage(MSG_SAVE_FAILED)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navigateUpToMainActivity();
                            }
                        })
                        .create();
            }
            alertDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Get the directory for the user's public pictures directory.
            File albumDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), ALBUM_NAME);
            if (!albumDir.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }

            activePreset.getSorter().apply(sourceBitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //TODO Setting to select image format.
            sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            String imgName = appName + "_" + getDateTimeString() + SUFFIX;
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
                Log.e(TAG, "An error occurred while writing the image to file (" + imgName + ")." +
                        " Image was not saved.", e);
                return false;
            }

            return true;
        }

        private String getDateTimeString() {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss", Locale.getDefault());
            Date date = Calendar.getInstance().getTime();
            return dateFormat.format(date);
        }
    }

    private void navigateUpToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        navigateUpTo(intent);
    }
}

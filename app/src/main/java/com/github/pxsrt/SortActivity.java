package com.github.pxsrt;

import com.github.pxsrt.util.SystemUiHider;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SortActivity extends Activity {

    public static final int FPS = 30;

    private SortFragment sortFragment;
    private SortConfigFragment sortConfigFragment;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        FragmentManager fragmentManager = getFragmentManager();

        sortFragment = new SortFragment();
        sortConfigFragment = new SortConfigFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.container_sort, sortFragment, SortFragment.TAG);
        fragmentTransaction.add(R.id.container_sort_settings, sortConfigFragment, SortConfigFragment.TAG);
        if (isDualPane()) {
            showSortSettings();
        }
        fragmentTransaction.commit();

        String imgPath = getIntent().getStringExtra(MainActivity.IMG_PATH);
        /*Set option for Bitmap to be mutable.*/
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;

        loadBitmap(imgPath, opts);
        sortFragment.setPixelSorter(sortConfigFragment.getSort());
    }

    private boolean isDualPane() {
        return getResources().getBoolean(R.bool.dual_pane);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                // TODO: If Settings has multiple levels, Up should navigate up
                // that hierarchy.
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_apply_once:
                sortFragment.applySortAndDraw();
                return true;

            case R.id.action_start_sort:
                sortFragment.runSort(FPS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void loadBitmap(final String path, final BitmapFactory.Options opts){
        new Thread(new Runnable() {
            @Override
            public void run() {
                img = BitmapFactory.decodeFile(path, opts);
                sortFragment.setImage(img);
            }
        }).start();
    }

    private void showSortSettings() {
        findViewById(R.id.container_sort_settings).setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }
    private void hideSortSettings() {
        findViewById(R.id.container_sort_settings).setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    private void showSort() {
        findViewById(R.id.container_sort).setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    private void hideSort() {
        findViewById(R.id.container_sort).setVisibility(View.GONE);
        invalidateOptionsMenu();
    }
}

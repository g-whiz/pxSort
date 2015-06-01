package com.github.pxsrt;

import com.android.internal.util.Predicate;
import com.github.pxsrt.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.Comparator;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SortActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String imgPath = intent.getStringExtra(MainActivity.IMG_PATH);

        /*Set option for Bitmap to be mutable.*/
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;

        setContentView(R.layout.activity_sort);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    private Sort createTestSort(){

        Comparator<Pixel> comparator = new Comparator<Pixel>() {
            @Override
            public int compare(Pixel px1, Pixel px2) {
                if (px1.red() > px2.red()){
                    return 1;
                } else if (px1.red() == px2.red()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        Predicate<Pixel> fromPredicate = new Predicate<Pixel>() {
            @Override
            public boolean apply(Pixel px) {
                return px.red() < px.blue();
            }
        };

        Predicate<Pixel> toPredicate = new Predicate<Pixel>() {
            @Override
            public boolean apply(Pixel px) {
                return px.red() > px.blue();
            }
        };

        return new AsendorfSort(comparator, fromPredicate, toPredicate, AsendorfSort.SORT_BY_ROW);
    }
}

package com.github.pxsrt;

import android.graphics.Bitmap;

/**
 * Created by George on 2015-05-25.
 */
public interface Sort {

    /**Applies a pixel sorting algorithm to the given Bitmap.
     *
     * NOTE: this method mutates the given Bitmap
     *
     * @param img Bitmap to which the sort will be applied.
     */
    public void apply(Bitmap img);

}

package com.github.pxsrt.sort;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An abstract class providing the framework for an asynchronous pixel sorting object.
 *
 * Created by George on 2015-05-25.
 */
public abstract class PixelSorter {

    private OnSortCompleteListener listener;

    /**Applies a pixel sorting algorithm to the given Bitmap.
     *
     * NOTE: this method mutates the given Bitmap
     *
     * @param img Bitmap to which the sort will be applied.
     */
    public abstract void apply(Bitmap img);

    /**
     * Adds a Callback to this Sort's callbacks.
     */
    public void setOnSortCompleteListener(OnSortCompleteListener listener){
        this.listener = listener;
    }

    protected void notifyListener() {
        listener.onSortComplete();
    }

    /**
     * Callback interface for callbacks from asynchronous pixel sorting.
     */
    public interface OnSortCompleteListener {

        /**
         * Callback method called on completion of one application of a sort to a Bitmap.
         */
        void onSortComplete();
    }
}

package com.github.pxsrt.sort;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;

/**TODO Documentation everywhere.
 *
 * Created by George on 2015-05-25.
 */
public abstract class PixelSort {

    //TODO Make this an abstract class and move the non-Asendorf-specific
    //methods over from AsendorfSort

    Collection<Callback> callbacks;

    public PixelSort(){
        callbacks = new ArrayList<>();
    }

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
    public void addCallback(Callback callback){
        callbacks.add(callback);
    }

    protected void notifyCallbacks() {
        for (Callback callback : callbacks) {
            callback.sortComplete();
        }
    }

    /**
     * Callback interface for asynchronous pixel sorting.
     */
    public interface Callback {

        /**
         * Callback method called on completion of one application of a sort.
         */
        void sortComplete();
    }
}

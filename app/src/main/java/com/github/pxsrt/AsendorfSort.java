package com.github.pxsrt;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**A class for applying the class of pixel sorting algorithm invented by Kim Asendorf.
 *
 * The sort is applied either by row or by column.
 *
 * For each row or column P, the index of the first pixel in P satisfying some Predicate is found
 * and the index of the last pixel in P satisfying some other Predicate is found.
 *
 * Then, the pixels between the first and last indices found in P (including the pixels at the given
 * indices) are sorted according toPredicate the order determined by some Comparator.
 *
 * Created by George on 2015-05-22.
 */
public class AsendorfSort extends Sort{

    public static final String TAG = AsendorfSort.class.getSimpleName();

    public static final int SORT_BY_ROW = 0;
    public static final int SORT_BY_COLUMN = 1;

    private Comparator<Pixel> comparator;

    private Predicate<Pixel> fromPredicate;
    private Predicate<Pixel> toPredicate;

    private int direction;

    private List<Callback> callbacks;

    /**Sole, parametrized constructor. Takes a Comparator, two Predicates, and either SORT_BY_ROW
     * or SORT_BY_COLUMN. See the description of this class for an overview of the function of each
     * component.
     *
     * @param comparator
     * @param fromPredicate
     * @param toPredicate
     * @param direction
     */
    public AsendorfSort (Comparator<Pixel> comparator, Predicate<Pixel> fromPredicate,
                         Predicate<Pixel> toPredicate, int direction) {
        this.comparator = comparator;
        this.fromPredicate = fromPredicate;
        this.toPredicate = toPredicate;
        this.callbacks = new ArrayList<>();

        if (direction == SORT_BY_COLUMN || direction == SORT_BY_ROW) {
            this.direction = direction;
        } else {
            Log.d(TAG, "Invalid direction. Using SORT_BY_ROW by default.");
            this.direction = SORT_BY_ROW;
        }
    }

    public Pixel[] sort(Pixel[] pixels){
        int fromIndex = getFromIndex(pixels);
        int toIndex = getToIndex(pixels);

        Arrays.sort(pixels, fromIndex, toIndex, comparator);

        return pixels;
    }

    private int getFromIndex(Pixel[] pixels) {
        int startIndex = 0;

        for (int i = 0; i < pixels.length; i++) {
            if (fromPredicate.apply(pixels[i])) {
                startIndex = i;
                break;
            }
        }

        return startIndex;
    }

    private int getToIndex(Pixel[] pixels) {
        int endIndex = pixels.length - 1;

        for (int i = pixels.length - 1; i >= 0; i--) {
            if (toPredicate.apply(pixels[i])) {
                endIndex = i;
                break;
            }
        }

        return endIndex;
    }

    @Override
    public void apply(final Bitmap img) {

        Thread t = new Thread(){

            @Override
            public void run() {

                switch (direction) {
                    case SORT_BY_ROW:
                        Log.d(TAG, "Sorting...");
                        for (int row = 0; row < img.getHeight(); row++) {
                            Pixel[] pixels = getRow(img, row);
                            sort(pixels);
                            setRow(img, pixels, row);
                        }
                        Log.d(TAG, "Sorted.");
                        break;

                    case SORT_BY_COLUMN:
                        Log.d(TAG, "Sorting...");
                        for (int col = 0; col < img.getWidth(); col++) {
                            Pixel[] pixels = getColumn(img, col);
                            sort(pixels);
                            setColumn(img, pixels, col);
                        }
                        Log.d(TAG, "Sorted.");
                        break;

                    default:
                        throw new IllegalStateException("Invalid direction.");
                }

                for (Callback callback : callbacks){
                    callback.sortComplete();
                }
            }
        };

        t.start();
    }

    @Override
    public void addCallback(Callback callback) {
        callbacks.add(callback);
    }
}

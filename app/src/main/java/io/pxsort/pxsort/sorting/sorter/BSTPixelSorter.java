package io.pxsort.pxsort.sorting.sorter;


import android.content.Context;
import android.graphics.Bitmap;

import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * A PixelSorter that organizes pixels into a top down traversal of a binary search tree.
 * <p/>
 * Created by George on 2016-05-10.
 */
class BSTPixelSorter extends SortPixelSorter {

    private static final String TAG = BSTPixelSorter.class.getSimpleName();

    BSTPixelSorter(Filter filter, Context context) {
        super(filter, context);
    }


    @Override
    protected void pixelSort(Bitmap partition) {
        super.pixelSort(partition);

        int[] oldPixels = new int[partition.getWidth() * partition.getHeight()];
        partition.getPixels(oldPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());

        int[] newPixels = topDownTraversal(oldPixels);
        partition.setPixels(newPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());
    }


    private int[] topDownTraversal(int[] oldPixels) {
        int[] newPixels = new int[oldPixels.length];

        int height = calculateBSTHeight(oldPixels.length);

        int idx = 0;
        for (int h = height; h >= 0; h--)
            for (int c = 1; c * (1 << h) - 1 < oldPixels.length; c += 2) {

                int oldPx = oldPixels[idx];
                int newPx = oldPixels[c * (1 << h) - 1];
                newPixels[idx] = combinePixels(oldPx, newPx);
                idx++;
            }

        return newPixels;
    }


    // Calculates the floor of log2(length)
    // From x4u's answer on http://stackoverflow.com/questions/3305059
    private int calculateBSTHeight(int length) {
        int log = 0;
        if ((length & 0xffff0000) != 0) {
            length >>>= 16;
            log = 16;
        }
        if (length >= 256) {
            length >>>= 8;
            log += 8;
        }
        if (length >= 16) {
            length >>>= 4;
            log += 4;
        }
        if (length >= 4) {
            length >>>= 2;
            log += 2;
        }
        return log + (length >>> 1);
    }
}

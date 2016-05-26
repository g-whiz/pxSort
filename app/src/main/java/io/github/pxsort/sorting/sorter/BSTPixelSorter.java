package io.github.pxsort.sorting.sorter;

import io.github.pxsort.sorting.filter.Filter;

/**
 * A PixelSorter that organizes pixels into a top down traversal of a binary search tree.
 * <p/>
 * Created by George on 2016-05-10.
 */
class BSTPixelSorter extends SortPixelSorter {

    private static final String TAG = BSTPixelSorter.class.getSimpleName();

    BSTPixelSorter(Filter filter) {
        super(filter);
    }


    @Override
    protected int[] pixelSort(int[] oldPixels) {
        int[] sortedPixels = super.pixelSort(oldPixels);
        return topDownTraversal(sortedPixels);
    }


    private int[] topDownTraversal(int[] sortedPixels) {
        int[] traversal = new int[sortedPixels.length];

        int height = calculateBSTHeight(sortedPixels.length);

        int idx = 0;
        for (int h = height; h >= 0; h--)
            for (int c = 1; c * (1 << h) - 1 < sortedPixels.length; c += 2) {

                int oldPx = sortedPixels[idx];
                int newPx = sortedPixels[c * (1 << h) - 1];
                traversal[idx] = combinePixels(oldPx, newPx);
                idx++;
            }

        return traversal;
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

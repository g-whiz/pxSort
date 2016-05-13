package io.github.pxsort.sorting.sorter;

import io.github.pxsort.sorting.filter.Filter;

/**
 * A PixelSorter that sorts pixels.
 * <p/>
 * Created by George on 2016-05-10.
 */
class SortPixelSorter extends PixelSorter {

    SortPixelSorter(Filter filter) {
        super(filter);
    }

    /*
     * This is an implementation of counting sort.
     */
    @Override
    protected int[] pixelSort(int[] oldPixels) {
        int[] buckets =
                generateBuckets(generateHistogram(oldPixels, filter.component), filter.order);
        int[] newPixels = new int[oldPixels.length];

        for (int oldPx : oldPixels) {
            int index = 0;
            switch (filter.order) {
                case Filter.ASCENDING:
                    index = getComponent(oldPx, filter.component);
                    break;

                case Filter.DESCENDING:
                    index = buckets.length - (getComponent(oldPx, filter.component) + 1);
                    break;
            }

            // Get the new pixel
            int newPx = newPixels[buckets[index]];
            newPixels[buckets[index]] =
                    combinePixels(oldPx, newPx);

            //Increment the index of the bucket
            buckets[index]++;
        }

        return newPixels;
    }

    private int[] generateHistogram(int[] pixels, int component) {
        int[] histogram = new int[256]; // int[] initializes to all 0's by default

        /*Increment the count of the number of pixels with the component value of px.*/
        for (int px : pixels) {
            histogram[getComponent(px, component)]++;
        }

        return histogram;
    }


    private int[] generateBuckets(int[] histogram, int order) {
        int[] buckets = new int[histogram.length];
        buckets[0] = 0;
        for (int i = 0; i < histogram.length - 1; i++) {
            switch (order) {
                case Filter.ASCENDING:
                    buckets[i + 1] = histogram[i];
                    break;
                case Filter.DESCENDING:
                    buckets[i + 1] = histogram[histogram.length - (i + 1)];
                    break;
            }
        }

        return buckets;
    }
}

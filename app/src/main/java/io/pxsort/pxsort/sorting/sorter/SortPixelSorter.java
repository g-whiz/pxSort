package io.pxsort.pxsort.sorting.sorter;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

import io.pxsort.pxsort.sorting.filter.Filter;
import io.pxsort.pxsort.sorting.renderscript.ScriptC_histogram;

/**
 * A PixelSorter that sorts pixels.
 * <p/>
 * Created by George on 2016-05-10.
 */
class SortPixelSorter extends PixelSorter {

    private static final String TAG = SortPixelSorter.class.getSimpleName();

    SortPixelSorter(Filter filter, Context context) {
        super(filter, context);
    }

    /*
     * This is an implementation of an augmented counting sort.
     */
    @Override
    protected void pixelSort(Bitmap partition) {
        int[] buckets =
                generateBuckets(partition, filter.order);

        int[] oldPixels = new int[partition.getWidth() * partition.getHeight()];
        int[] newPixels = new int[oldPixels.length];

        partition.getPixels(oldPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());

        int oldPx;
        int newPx;
        int bucketIndex;
        for (int i = 0; i < oldPixels.length; i++) {
            oldPx = oldPixels[i];

            switch (filter.order) {
                case Filter.ASCENDING:
                    bucketIndex = getComponent(oldPx, filter.component);
                    break;

                case Filter.DESCENDING:
                    bucketIndex = buckets.length - (getComponent(oldPx, filter.component) + 1);
                    break;

                default:
                    throw new IllegalStateException("Invalid order: " + filter.order);
            }

            // Get the new pixel
            newPx = oldPixels[buckets[bucketIndex]];
            newPixels[i] = combinePixels(oldPx, newPx);

            //Increment the index of the bucket
            buckets[bucketIndex]++;
        }

        partition.setPixels(newPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());
    }

    private int[] generateHistogram(Bitmap partition, int component) {
        int[] oldPixels = new int[partition.getWidth() * partition.getHeight()];
        partition.getPixels(oldPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());

        int[] histogram = new int[256]; // int[] initializes to all 0's by default

        /*Increment the count of the number of pixels with the component value of px.*/
        for (int px : oldPixels) {
            histogram[getComponent(px, component)]++;
        }

        return histogram;
    }

    private int[] generateHistogram(Bitmap partition) {
        int[] histogram = new int[256];
        RenderScript rs = RenderScript.create(appContext, RenderScript.ContextType.NORMAL);

        Allocation allocPixels = Allocation.createFromBitmap(rs, partition,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SHARED | Allocation.USAGE_SCRIPT);

        Allocation allocHistogram = Allocation.createSized(rs, Element.I32(rs), 256);
        allocHistogram.copyFrom(histogram); // this seems like it's unnecessary, putting it
        // in to ensure hist array is zeroed to start

        ScriptC_histogram rsHistogram = new ScriptC_histogram(rs);
        rsHistogram.bind_histogram(allocHistogram);
        rsHistogram.set_component_const(this.filter.component);
        rsHistogram.forEach_populate_histogram(allocPixels);

        allocHistogram.copyTo(histogram);
        rs.destroy();
        return histogram;
    }

    private int[] generateBuckets(Bitmap partition, int order) {
        int[] histogram = generateHistogram(partition);
        int[] buckets = new int[histogram.length];

        buckets[0] = 0;
        for (int i = 0; i < histogram.length - 1; i++) {
            switch (order) {
                case Filter.ASCENDING:
                    buckets[i + 1] = buckets[i] + histogram[i];
                    break;
                case Filter.DESCENDING:
                    buckets[i + 1] = buckets[i] + histogram[histogram.length - (i + 1)];
                    break;
            }
        }

        return buckets;
    }
}

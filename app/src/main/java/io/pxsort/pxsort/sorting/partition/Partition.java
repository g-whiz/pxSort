package io.pxsort.pxsort.sorting.partition;

import android.graphics.Bitmap;

import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * Abstract class for partitioning a Bitmap into sections to iterate over and pixel sort.
 * <p/>
 * Created by George on 2016-05-05.
 */
public abstract class Partition {

    protected final Bitmap src;

    public Partition(Bitmap src) {
        this.src = src;
    }

    protected Bitmap getSrcBitmap() {
        return src;
    }

    /**
     * Returns the pixels of the next partition.
     *
     * @return the pixels of the next partition
     * @throws ArrayIndexOutOfBoundsException if called when hasNext() == false
     */
    public abstract Bitmap next();

    /**
     * Returns true if there are more partitions.
     *
     * @return true if there are more partitions
     */
    public abstract boolean hasNext();

    /**
     * Sets the pixels in the current partition to those in partition.
     *
     * @param partition the pixels to set the current partition as
     */
    public abstract void set(Bitmap partition);

    public static Partition create(Bitmap bitmap, Filter filter) {
        switch (filter.partitionType) {
            case Filter.GRID_PARTITION:
                return new GridPartition(bitmap, filter.numRows, filter.numCols);

            default:
                throw new IllegalArgumentException(
                        "Unknown partitionType in the given Filter: " + filter.partitionType);
        }
    }
}

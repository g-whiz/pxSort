package io.github.pxsort.sorting.partition;

import android.graphics.Bitmap;

import io.github.pxsort.sorting.filter.Filter;

/**
 * Abstract class for partitioning a Bitmap into sections to iterate over and pixel sort.
 * <p/>
 * Created by George on 2016-05-05.
 */
public abstract class Partitioner {

    private Bitmap bitmap;

    public Partitioner(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    protected Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Returns the pixels of the next partition.
     *
     * @return the pixels of the next partition
     * @throws ArrayIndexOutOfBoundsException if called when hasNext() == false
     */
    public abstract int[] nextPartition();

    /**
     * Returns true if there are more partitions.
     *
     * @return true if there are more partitions
     */
    public abstract boolean hasNext();

    /**
     * Sets the pixels in the current partition to those in the given partition.
     *
     * @param partition the pixels to set the current partition as
     */
    public abstract void setPartition(int[] partition);

    public static Partitioner from(Bitmap bitmap, Filter filter) {
        switch (filter.partitionType) {
            case Filter.GRID_PARTITION:
                return new GridPartitioner(bitmap, filter.numRows, filter.numCols);

            default:
                throw new IllegalArgumentException(
                        "Unknown partitionType in the given Filter: " + filter.partitionType);
        }
    }
}

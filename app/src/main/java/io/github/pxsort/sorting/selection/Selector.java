package io.github.pxsort.sorting.selection;

import io.github.pxsort.sorting.filter.Filter;

/**
 * A class for selecting a subset of pixels in a partition to pixel sort.
 * What subset is selected is determined by the specific type of Selector, and its parameters.
 *
 * Created by George on 2016-06-12.
 */
public abstract class Selector {
    // TODO: 2016-06-17:
    // - add constants to Filter for Selectors
    // - update the structure of Filter, and the FilterDB Schema (:-P)


    /**
     * Returns a subset of the pixels in partition.
     *
     * @param partition the partition to select pixels from
     * @return the selected subset of pixels
     */
    public abstract int[] getSelection(int[] partition);


    /**
     * Sets some subset of the pixels in partition to the pixels in selection.
     *
     * @param partition the partition to insert the pixels from selection into
     * @param selection the pixels to insert into partition.
     *                  Precondition: selection.length == getSelection(partition).length
     * @return a copy of partition with the inserted pixels from selection
     */
    public abstract int[] setSelection(int[] partition, int[] selection);


    /**
     * Returns a new Selector as determined by the parameters in f.
     *
     * @param f the Filter to create the new Selector with.
     * @return the new Selector
     */
    public static Selector fromFilter(Filter f) {
        // TODO: 2016-06-19
        return null;
    }
}

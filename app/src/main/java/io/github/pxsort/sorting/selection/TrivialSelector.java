package io.github.pxsort.sorting.selection;

/**
 * The trivial Selector returns all pixels in a Partition
 *
 * Created by George on 2016-06-19.
 */
class TrivialSelector extends Selector {

    @Override
    public int[] getSelection(int[] partition) {
        return partition;
    }

    @Override
    public int[] setSelection(int[] partition, int[] selection) {
         return selection;
    }
}

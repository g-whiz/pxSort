package io.github.pxsort.sorting.selection;

import io.github.pxsort.sorting.filter.Filter;

/**
 * Created by George on 2016-06-23.
 */
class ThresholdSelector extends Selector {



    public ThresholdSelector(Filter f) {

    }

    @Override
    public int[] getSelection(int[] partition) {
        return new int[0];
    }

    @Override
    public int[] setSelection(int[] partition, int[] selection) {
        return new int[0];
    }
}

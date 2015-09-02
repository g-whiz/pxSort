package com.github.pxsrt.sort;

import com.android.internal.util.Predicate;
import com.github.pxsrt.sort.comparator.ComponentComparator;
import com.github.pxsrt.sort.predicate.ComponentPredicate;

import java.util.Arrays;
import java.util.Comparator;


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
public class RowPixelSorter extends AbstractRowPixelSorter {

    public static final String TAG = RowPixelSorter.class.getSimpleName();

    public RowPixelSorter(ComponentComparator comparator, ComponentPredicate fromPredicate,
                          ComponentPredicate toPredicate, int direction) {
        super(comparator, fromPredicate, toPredicate, direction);
    }


    @Override
    protected Pixel[] sort(Pixel[] pixels){
        int fromIndex = getFromIndex(pixels);
        int toIndex = getToIndex(pixels);

        Arrays.sort(pixels, fromIndex, toIndex, comparator);

        return pixels;
    }
}

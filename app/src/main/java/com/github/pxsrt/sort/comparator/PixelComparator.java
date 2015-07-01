package com.github.pxsrt.sort.comparator;

import com.github.pxsrt.sort.Evaluator;
import com.github.pxsrt.sort.Pixel;

import java.util.Comparator;

/**
 * Created by George on 2015-06-11.
 */
public abstract class PixelComparator implements Comparator<Pixel>, Evaluator<Pixel, Number> {

    /**Values will be sorted in ascending (left to right) order.*/
    public static final String ASCENDING_ORDER = "Ascending Order";

    /**Values will be sorted in descending (left to right) order.*/
    public static final String DESCENDING_ORDER = "Descending Order";

    private volatile String ordering;
    private final String name;

    /**
     * Main constructor. Ordering is set to DESCENDING_ORDER by default.
     * @param name The name of this PixelComparator.
     */
    public PixelComparator(String name, String ordering) {
        this.name = name;
        this.ordering = ordering;
    }


    @Override
    public int compare(Pixel lpx, Pixel rpx) {

        int result;

        if(evaluate(lpx).doubleValue() >= evaluate(rpx).doubleValue()){
            result = 1;
        } else if (evaluate(lpx) == evaluate(rpx)) {
            result = 0;
        } else {
            result = -1;
        }

        switch (ordering) {
            case ASCENDING_ORDER:
                return result * -1;

            case DESCENDING_ORDER:
                return result;

            default:
                return 0;
        }
    }

    /**
     * Returns the current direction this PixelComparator is ordering pixels in.
     * @return Either ASCENDING_ORDER or DESCENDING_ORDER
     */
    public String getOrdering() {
        return ordering;
    }

    /**
     * Returns the current direction this PixelComparator is ordering pixels in.
     * @param ordering Either {@link #ASCENDING_ORDER} or {@link #DESCENDING_ORDER}
     * @return This PixelComparator.
     */
    public PixelComparator setOrdering(String ordering) {
        this.ordering = ordering;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}

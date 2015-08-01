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

    /**The comparator will examine the pixel's red component.*/
    public static final String RED = "Red";

    /**The comparator will examine the pixel's green component.*/
    public static final String GREEN = "Green";

    /**The comparator will examine the pixel's blue component.*/
    public static final String BLUE = "Blue";

    /**The comparator will examine the pixel's hue component.*/
    public static final String HUE = "Hue";

    /**The comparator will examine the pixel's saturation component.*/
    public static final String SATURATION = "Saturation";

    /**The comparator will examine the pixel's value component.*/
    public static final String VALUE = "Value";

    private volatile String ordering;

    public String getComponent() {
        return component;
    }

    private final String component;

    public static PixelComparator createNew(String component, String ordering) {
        switch (component) {
            case RED:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new PixelComparator(component, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }

    /**
     * Main constructor. Ordering is set to DESCENDING_ORDER by default.
     * @param component The component of this PixelComparator.
     */
    public PixelComparator(String component, String ordering) {
        this.component = component;
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
        return component;
    }
}

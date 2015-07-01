package com.github.pxsrt.sort.comparator;

import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-30.
 */
public class PixelComparatorFactory {

    /**The comparator will arrange the pixels in ascending order.*/
    public static final String ASCENDING_ORDER = PixelComparator.ASCENDING_ORDER;

    /**The comparator will arrange the pixels in descending order.*/
    public static final String DESCENDING_ORDER = PixelComparator.DESCENDING_ORDER;

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

    public static PixelComparator createNew(String component, String ordering) {
        switch (component) {
            case RED:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new PixelComparator(component + " IN " + ordering, ordering) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }
}

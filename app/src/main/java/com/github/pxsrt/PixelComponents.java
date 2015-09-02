package com.github.pxsrt;

import com.github.pxsrt.sort.Pixel;
import com.github.pxsrt.sort.comparator.ComponentComparator;

/**
 * Created by George on 2015-08-28.
 */
public class PixelComponents {

    public static String[] getComponents() {
        return new String[] {RED, GREEN, BLUE, HUE, SATURATION, VALUE};
    }

    public static Number getFromPixel(String component, Pixel pixel) {
        switch (component) {
            case RED:
                return pixel.red();
            case GREEN:
                return pixel.green();
            case BLUE:
                return pixel.blue();
            case HUE:
                return pixel.hue();
            case SATURATION:
                return pixel.saturation();
            case VALUE:
                return pixel.value();
            default:
                return -1;
        }
    }

    /**String representing the red component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String RED = "Red Component";

    /**String representing the green component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String GREEN = "Green Component";

    /**String representing the blue component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String BLUE = "Blue Component";

    /**String representing the hue component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String HUE = "Hue Component";

    /**String representing the saturation component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String SATURATION = "Saturation Component";

    /**String representing the value component of a pixel. Used in
     * {@link com.github.pxsrt.sort.predicate.PixelPredicate PixelPredicate},
     * {@link ComponentComparator PixelComparator},
     * and {@link com.github.pxsrt.view.ComponentSpinner ComponentSpinner}.
     */
    public static final String VALUE = "Value Component";

}

package io.github.pxsrt.sort;

/**
 * Separate constants class for various params to be passed to the pixel sorting algorithms in
 * {@link io.github.pxsrt.sort.Operations Operations }.
 *
 * Created by George on 2015-12-25.
 */
public class OperationsConstants {

    /* ***** COMPONENT CONSTANTS ***** */

    /**
     * Sort pixels by their red colour component.
     */
    public static final int RED = 0;

    /**
     * Sort pixels by their green colour component.
     */
    public static final int GREEN = 1;

    /**
     * Sort pixels by their blue colour component.
     */
    public static final int BLUE = 2;

    /**
     * Sort pixels by their hue component.
     */
    public static final int HUE = 3;

    /**
     * Sort pixels by their saturation component.
     */
    public static final int SATURATION = 4;

    /**
     * Sort pixels by their value component.
     */
    public static final int VALUE = 5;

    //// TODO: 2015-12-25 Add brightness to components?


    /* ***** ORDER CONSTANTS ***** */

    /**
     * Sort pixels in descending order.
     */
    public static final int DESCENDING = 0;

    /**
     * Sort pixels in ascending order.
     */
    public static final int ASCENDING = 1;


    /* ***** METHOD CONSTANTS ***** */

    /**
     * Move entire pixels.
     */
    public static final int MOVE_PIXELS = 0;

    /**
     * Move only the selected component.
     */
    public static final int MOVE_COMPONENT = 1;

    /**
     * Move only the red component.
     */
    public static final int MOVE_RED = 2;

    /**
     * Move only the green component.
     */
    public static final int MOVE_GREEN = 3;

    /**
     * Move only the blue component.
     */
    public static final int MOVE_BLUE = 4;

    /**
     * Move only the hue component.
     */
    public static final int MOVE_HUE = 5;

    /**
     * Move only the saturation component.
     */
    public static final int MOVE_SATURATION = 6;

    /**
     * Move only the value component.
     */
    public static final int MOVE_VALUE = 7;

}

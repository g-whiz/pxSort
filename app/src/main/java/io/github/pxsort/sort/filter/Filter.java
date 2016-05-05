package io.github.pxsort.sort.filter;

import android.content.ContentValues;

/**
 * An object containing the parameters for use with a PixelSorter.
 * <p/>
 * Created by George on 2016-05-04.
 */
public class Filter {

    /**
     * The name of this Filter.
     */
    public final String name;

    /**
     * The component that defines pixel ordering when pixel sorting with this Filter.
     */
    public final int component;

    /**
     * The order in which pixels are sorted when pixel sorting with this Filter.
     */
    public final int order;

    /**
     * How sorted pixels are recombined with the original image when pixel sorting with this Filter.
     */
    public final int combination;

    /**
     *
     */
    public final int algorithm;

    /**
     *
     */
    public final int numRows;

    /**
     *
     */
    public final int numCols;

    /**
     *
     */
    public final boolean isDefault;

    /**
     * Sole constructor.
     *
     * @param name        The name of this Filter.
     * @param component   The component that defines pixel ordering when pixel sorting with
     *                    this Filter.
     *                    <p/>
     *                    This should be one of the COMPONENT CONSTANTS.
     * @param order       The order in which pixels are sorted when pixel sorting with this Filter.
     *                    <p/>
     *                    This should be one of the ORDER CONSTANTS.
     * @param combination How sorted pixels are recombined with the original image when pixel
     *                    sorting with this Filter.
     *                    <p/>
     *                    This should be some (bitwise-OR'ed) combination of the
     *                    COMBINATION CONSTANTS.
     * @param algorithm   The algorithm used to sort pixels
     * @param numRows
     * @param numCols
     * @param isDefault
     */
    public Filter(String name, int component, int order, int combination, int algorithm, int numRows,
                  int numCols, boolean isDefault) {
        this.name = name;
        this.component = component;
        this.order = order;
        this.combination = combination;
        this.algorithm = algorithm;
        this.numRows = numRows;
        this.numCols = numCols;
        this.isDefault = isDefault;
    }


    /**
     * Creates a new Filter from ContentValues (presumably from FilterDB).
     *
     * @param values
     * @return
     */
    public static Filter from(ContentValues values) {
        // TODO: 2016-05-04 Nice syntax. Filter.from(values)
        return null;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("component", component);
        values.put("order", order);
        values.put("combination", combination);
        values.put("algorithm", algorithm);
        values.put("num_rows", numRows);
        values.put("num_cols", numCols);

        return values;
    }

    @Override
    public String toString() {
        return name;
    }

/* ************** CONSTANTS ************** */

    /* ***** COMPONENT CONSTANTS ***** */

    /**
     * COMPONENT CONSTANT: Sort pixels by their red colour component.
     */
    public static final int RED = 0;

    /**
     * COMPONENT CONSTANT: Sort pixels by their green colour component.
     */
    public static final int GREEN = 1;

    /**
     * COMPONENT CONSTANT: Sort pixels by their blue colour component.
     */
    public static final int BLUE = 2;

    /**
     * COMPONENT CONSTANT: Sort pixels by their hue component.
     */
    public static final int HUE = 3;

    /**
     * COMPONENT CONSTANT: Sort pixels by their saturation component.
     */
    public static final int SATURATION = 4;

    /**
     * COMPONENT CONSTANT: Sort pixels by their value component.
     */
    public static final int VALUE = 5;


    /* ***** ORDER CONSTANTS ***** */

    /**
     * ORDER CONSTANT: Sort pixels in descending order.
     */
    public static final int DESCENDING = 0;

    /**
     * ORDER CONSTANT: Sort pixels in ascending order.
     */
    public static final int ASCENDING = 1;


    /* ***** COMBINATION BITS ***** */

    /**
     * COMBINATION BIT: Replace the entire pixel.
     */
    public static final int REPLACE_PIXEL = 0x1;

    /**
     * COMBINATION BIT: Replace only the red component.
     */
    public static final int REPLACE_RED = 0x2;

    /**
     * COMBINATION BIT: Replace only the green component.
     */
    public static final int REPLACE_GREEN = 0x4;

    /**
     * COMBINATION BIT: Replace only the blue component.
     */
    public static final int REPLACE_BLUE = 0x8;

    /**
     * COMBINATION BIT: Replace only the hue component.
     */
    public static final int REPLACE_HUE = 0x10;

    /**
     * COMBINATION BIT: Replace only the saturation component.
     */
    public static final int REPLACE_SATURATION = 0x20;

    /**
     * COMBINATION BIT: Replace only the value component.
     */
    public static final int REPLACE_VALUE = 0x40;


    /* ***** ALGORITHM CONSTANTS ***** */

    /**
     * ALGORITHM CONSTANT: Sort the pixels.
     */
    public static final int SORT = 0;

    /**
     * ALGORITHM CONSTANT: Heapify the pixels.
     */
    public static final int HEAPIFY = 1;

    /**
     * ALGORITHM CONSTANT: Do a top-down bst traversal of the pixels.
     */
    public static final int BST = 2;
}

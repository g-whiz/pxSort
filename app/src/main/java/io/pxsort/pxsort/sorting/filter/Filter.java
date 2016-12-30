package io.pxsort.pxsort.sorting.filter;

import android.content.ContentValues;
import android.database.Cursor;

import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_ALGORITHM;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_1;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_2;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_3;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_FUNC_4;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMBINE_TYPE;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_COMPONENT;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_IS_BUILT_IN;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_NAME;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_NUM_COLS;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_NUM_ROWS;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_ORDER;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.COL_PARTITION_TYPE;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_ALGORITHM;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMBINE_FUNC_1;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMBINE_FUNC_2;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMBINE_FUNC_3;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMBINE_FUNC_4;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMBINE_TYPE;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_COMPONENT;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_IS_BUILT_IN;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_NAME;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_NUM_COLS;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_NUM_ROWS;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_ORDER;
import static io.pxsort.pxsort.sorting.filter.FilterDBConstants.IDX_PARTITION_TYPE;

/**
 * An object containing data on the exact pixel-sorting algorithm to apply to an image.
 * <p/>
 * Created by George on 2016-05-04.
 */
public class Filter {

    public final String name;

    public final boolean isBuiltIn;

    public final int algorithm;

    public final int component;

    public final int order;

    public final int combineType;

    public final int combineFunc1;

    public final int combineFunc2;

    public final int combineFunc3;

    public final int combineFunc4;

    public final int partitionType;

    public final int numRows;

    public final int numCols;

    /**
     *
     * @param name        The name of this Filter.
     * @param isBuiltIn   true if this Filter comes built into the app.
     * @param algorithm   The algorithm used to sort pixels when pixel sorting with this Filter.
     *
     *                    Should be one of the ALGORITHM CONSTANTS.
     * @param component   The component that defines pixel ordering when pixel sorting with
     *                    this Filter.
     *
     *                    This should be one of the COMPONENT CONSTANTS.
     * @param order       The order in which pixels are sorted when pixel sorting with this Filter.
     *
     *                    This should be one of the ORDER CONSTANTS.
     * @param combineType What components to use when combining the pixel-sorted image with the
     *                    original.
     *
     *                    This should be one of the COMBINE TYPE CONSTANTS.
     * @param combineFunc1 How to combine the first new component (alpha) with the original.
     *
     *                    This should be one of the COMBINE FUNCTION CONSTANTS.
     * @param combineFunc2 How to combine the second new component (hue or red) with the original.
     *
     *                    This should be one of the COMBINE FUNCTION CONSTANTS.
     * @param combineFunc3 How to combine the third new component (saturation or green) with the
     *                     original.
     *
     *                    This should be one of the COMBINE FUNCTION CONSTANTS.
     * @param combineFunc4 How to combine the fourth new component (value or blue) with the
     *                     original.
     *
     *                    This should be one of the COMBINE FUNCTION CONSTANTS.
     * @param partitionType How to partition the image when pixel sorting it.
     * @param numRows     The number of rows a bitmap is partitioned into if this Filter
     *                    uses a GRID_PARTITION.
     * @param numCols     The number of columns a bitmap is partitioned into if this Filter
     *                    uses a GRID_PARTITION.
     */
    public Filter(
            String name, boolean isBuiltIn, int algorithm, int component, int order,
            int combineType, int combineFunc1, int combineFunc2, int combineFunc3,
            int combineFunc4, int partitionType, int numRows, int numCols) {
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.algorithm = algorithm;
        this.component = component;
        this.order = order;
        this.combineType = combineType;
        this.combineFunc1 = combineFunc1;
        this.combineFunc2 = combineFunc2;
        this.combineFunc3 = combineFunc3;
        this.combineFunc4 = combineFunc4;
        this.partitionType = partitionType;
        this.numRows = numRows;
        this.numCols = numCols;
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_IS_BUILT_IN, isBuiltIn);

        values.put(COL_ALGORITHM, algorithm);
        values.put(COL_COMPONENT, component);
        values.put(COL_ORDER, order);

        values.put(COL_COMBINE_TYPE, combineType);
        values.put(COL_COMBINE_FUNC_1, combineFunc1);
        values.put(COL_COMBINE_FUNC_2, combineFunc2);
        values.put(COL_COMBINE_FUNC_3, combineFunc3);
        values.put(COL_COMBINE_FUNC_4, combineFunc4);

        values.put(COL_PARTITION_TYPE, partitionType);
        values.put(COL_NUM_ROWS, numRows);
        values.put(COL_NUM_COLS, numCols);

        return values;
    }


    public static Filter fromCursor(Cursor cursor) {
        return new Filter(
                cursor.getString(IDX_NAME),
                (cursor.getInt(IDX_IS_BUILT_IN) != 0),  // DB doesn't use booleans, convert from int

                cursor.getInt(IDX_ALGORITHM),
                cursor.getInt(IDX_COMPONENT),
                cursor.getInt(IDX_ORDER),

                cursor.getInt(IDX_COMBINE_TYPE),
                cursor.getInt(IDX_COMBINE_FUNC_1),
                cursor.getInt(IDX_COMBINE_FUNC_2),
                cursor.getInt(IDX_COMBINE_FUNC_3),
                cursor.getInt(IDX_COMBINE_FUNC_4),

                cursor.getInt(IDX_PARTITION_TYPE),
                cursor.getInt(IDX_NUM_ROWS),
                cursor.getInt(IDX_NUM_COLS)
        );
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        // Filter names must be unique.
        // Filter.equals() is used as part of Collection.contains() to determine if there
        // is a name conflict when adding a new Filter.
        return obj instanceof Filter && ((Filter) obj).name.equals(this.name);
    }


    /* ************** CONSTANTS ************** */

    /* ***** COMPONENT CONSTANTS ***** */

    /**
     * COMPONENT CONSTANT: Sort pixels by their red colour component.
     */
    public static final int CMPNT_RED = 0;

    /**
     * COMPONENT CONSTANT: Sort pixels by their green colour component.
     */
    public static final int CMPNT_GREEN = 1;

    /**
     * COMPONENT CONSTANT: Sort pixels by their blue colour component.
     */
    public static final int CMPNT_BLUE = 2;

    /**
     * COMPONENT CONSTANT: Sort pixels by their hue component.
     */
    public static final int CMPNT_HUE = 3;

    /**
     * COMPONENT CONSTANT: Sort pixels by their saturation component.
     */
    public static final int CMPNT_SAT = 4;

    /**
     * COMPONENT CONSTANT: Sort pixels by their value component.
     */
    public static final int CMPNT_VAL = 5;

    /**
     * COMPONENT CONSTANT: Sort pixels by their alpha component.
     */
    public static final int CMPNT_ALPHA = 6;


    /* ***** ORDER CONSTANTS ***** */

    /**
     * ORDER CONSTANT: Sort pixels in descending order.
     */
    public static final int DESCENDING = 0;

    /**
     * ORDER CONSTANT: Sort pixels in ascending order.
     */
    public static final int ASCENDING = 1;


    /* ***** COMBINE TYPE CONSTANTS ***** */

    /**
     * Combine pixels according to their ARGB components.
     */
    public static final int COMBINE_ARGB = 0;

    /**
     * Combine pixels according to their AHSV (alpha, HSV) components.
     */
    public static final int COMBINE_AHSV = 1;


    /* ***** COMBINATION FUNCTION CONSTANTS ***** */

    /**
     * COMBINATION FUNCTION: Preserve the original value.
     */
    public static final int PRESERVE = 0;

    /**
     * COMBINATION FUNCTION: Replace the original value with the new value.
     */
    public static final int REPLACE = 1;

    /**
     * COMBINATION FUNCTION: Add the original and new values (mod 256).
     */
    public static final int ADD = 2;

    /**
     * COMBINATION FUNCTION: Subtract the original and new values (mod 256).
     */
    public static final int SUBTRACT = 3;

    /**
     * COMBINATION FUNCTION: Multiply the original and new values (mod 256).
     */
    public static final int MULTIPLY = 4;

    /**
     * COMBINATION FUNCTION: Exclusive-or the original and new values.
     */
    public static final int XOR = 5;


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


    /* ***** PARTITION CONSTANTS ***** */

    /**
     * PARTITION CONSTANT: Partition the image into a grid.
     */
    public static final int GRID_PARTITION = 0;
}

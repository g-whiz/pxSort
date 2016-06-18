package io.github.pxsort.sorting.sorter;

import android.graphics.Bitmap;
import android.graphics.Color;

import io.github.pxsort.sorting.filter.Filter;
import io.github.pxsort.sorting.partition.Partitioner;

/**
 * Abstract data type for pixel sorting Bitmaps.
 *
 * The methods in this class are not threadsafe.
 * <p/>
 * Created by George on 2016-05-04.
 */
public abstract class PixelSorter {

    private static final String TAG = PixelSorter.class.getSimpleName();
    /**
     * This PixelSorter's Filter
     */
    protected final Filter filter;

    // Arrays used to store temporary values when executing the getComponent and combinePixel
    // helper methods. Since the methods are executed potentially millions of times per Bitmap,
    // allocating new arrays on each invocation can cause performance issues.
    private int[] components;
    private float[] hsv;

    private int[] oldComponents;
    private int[] newComponents;

    private int[] combineFuncs;

    protected PixelSorter(Filter filter) {
        this.filter = filter;
        this.components = new int[4];
        this.oldComponents = new int[4];
        this.newComponents = new int[4];
        this.hsv = new float[3];

        this.combineFuncs = new int[]{
                filter.combineFunc1,
                filter.combineFunc2,
                filter.combineFunc3,
                filter.combineFunc4
        };
    }

    public void applyTo(Bitmap bitmap) {
        Partitioner partitioner = Partitioner.from(bitmap, filter);

        while (partitioner.hasNext()) {
            int[] unsortedPixels = partitioner.nextPartition();
            int[] sortedPixels = pixelSort(unsortedPixels);
            partitioner.setPartition(sortedPixels);
        }
    }


    /**
     * Apply this PixelSorter's pixel sorting algorithm to pixels.
     *
     * @param pixels
     * @return
     */
    protected abstract int[] pixelSort(int[] pixels);


    /**
     * Combines the given pixels according to the rules defined in filter.
     *
     * @param oldPixel
     * @param newPixel
     * @return the pixel resulting from the combination.
     */
    protected int combinePixels(int oldPixel, int newPixel) {
        extractComponents(filter.combineType, oldPixel, oldComponents);
        extractComponents(filter.combineType, newPixel, newComponents);

        for (int i = 0; i < 4; i++) {
            newComponents[i] =
                    combineComponents(combineFuncs[i], oldComponents[i], newComponents[i]);
        }

        return componentsToPixel(filter.combineType, newComponents);
    }


    protected int getComponent(int pixel, int component) {
        switch (component) {
            case Filter.CMPNT_ALPHA:
                return Color.alpha(pixel);

            case Filter.CMPNT_RED:
                return Color.red(pixel);

            case Filter.CMPNT_GREEN:
                return Color.green(pixel);

            case Filter.CMPNT_BLUE:
                return Color.blue(pixel);

            case Filter.CMPNT_HUE:
                return extractComponents(Filter.COMBINE_AHSV, pixel, components)[1];

            case Filter.CMPNT_SAT:
                return extractComponents(Filter.COMBINE_AHSV, pixel, components)[2];

            case Filter.CMPNT_VAL:
                return extractComponents(Filter.COMBINE_AHSV, pixel, components)[3];

            default:
                throw new IllegalStateException(
                        "Unknown component in this PixelSorter's Filter: " + component);
        }
    }


    private int combineComponents(int combineFunc, int oldComponent, int newComponent) {
        switch (combineFunc) {
            case Filter.PRESERVE:
                return oldComponent;

            case Filter.REPLACE:
                return newComponent;

            case Filter.ADD:
                return (oldComponent + newComponent) % 256;

            case Filter.SUBTRACT:
                return Math.abs(oldComponent - newComponent) % 256;

            case Filter.MULTIPLY:
                return (oldComponent * newComponent) % 256;

            case Filter.XOR:
                return Math.abs(oldComponent ^ newComponent) % 256;
            default:
                throw new IllegalStateException(
                        "Unknown combineFunc in this PixelSorter's Filter: " + combineFunc);
        }
    }


    private int[] extractComponents(int combineType, int pixel, int[] components) {
        components[0] = Color.alpha(pixel);

        if (combineType == Filter.COMBINE_ARGB) {
            components[1] = Color.red(pixel);
            components[2] = Color.green(pixel);
            components[3] = Color.blue(pixel);

        } else if (combineType == Filter.COMBINE_AHSV) {
            Color.colorToHSV(pixel, hsv);

            components[1] = (int) (hsv[0] * 255f / 360f);
            components[2] = (int) (hsv[1] * 255f);
            components[3] = (int) (hsv[2] * 255f);
        } else {
            throw new IllegalStateException(
                    "Unknown combineType in this PixelSorter's Filter: " + combineType);
        }

        // return components for convenience
        return components;
    }


    private int componentsToPixel(int combineType, int[] components) {
        if (combineType == Filter.COMBINE_ARGB) {
            return Color.argb(
                    components[0],
                    components[1],
                    components[2],
                    components[3]);

        } else if (combineType == Filter.COMBINE_AHSV) {
            hsv[0] = ((float) components[1]) * 360f / 255f;
            hsv[1] = ((float) components[2]) / 255f;
            hsv[2] = ((float) components[3]) / 255f;

            return Color.HSVToColor(components[0], hsv);

        } else {
            throw new IllegalStateException(
                    "Unknown combineType in this PixelSorter's Filter: " + combineType);
        }
    }


    /**
     * Factory method for instantiating a PixelSorter from a Filter.
     *
     * @param filter the filter to create the PixelSorter from
     * @return the newly-created PixelSorter
     */
    public static PixelSorter fromFilter(Filter filter) {
        switch (filter.algorithm) {
            case Filter.SORT:
                return new SortPixelSorter(filter);

            case Filter.HEAPIFY:
                return new HeapifyPixelSorter(filter);

            case Filter.BST:
                return new BSTPixelSorter(filter);

            default:
                throw new IllegalArgumentException(
                        "Unknown algorithm in the given Filter: " + filter.algorithm);
        }
    }
}

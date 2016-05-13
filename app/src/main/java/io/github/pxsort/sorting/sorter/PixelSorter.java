package io.github.pxsort.sorting.sorter;

import android.graphics.Bitmap;
import android.graphics.Color;

import io.github.pxsort.sorting.filter.Filter;
import io.github.pxsort.sorting.partition.Partition;

/**
 * Abstract data type for pixel sorting Bitmaps.
 * <p/>
 * Created by George on 2016-05-04.
 */
public abstract class PixelSorter {

    protected final Filter filter;

    protected PixelSorter(Filter filter) {
        this.filter = filter;
    }

    public void applyTo(Bitmap bitmap) {
        Partition partition = Partition.from(bitmap, filter);

        while (partition.hasNext()) {
            int[] unsortedPixels = partition.next();
            int[] sortedPixels = pixelSort(unsortedPixels);
            partition.set(sortedPixels);
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
        int[] combineFuncs = new int[]{
                filter.combineFunc1,
                filter.combineFunc2,
                filter.combineFunc3,
                filter.combineFunc4
        };
        int[] oldComponents = pixelToComponents(filter.combineType, oldPixel);
        int[] newComponents = pixelToComponents(filter.combineType, newPixel);

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
                return pixelToComponents(Filter.COMBINE_AHSV, pixel)[1];

            case Filter.CMPNT_SAT:
                return pixelToComponents(Filter.COMBINE_AHSV, pixel)[2];

            case Filter.CMPNT_VAL:
                return pixelToComponents(Filter.COMBINE_AHSV, pixel)[3];

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

    private int[] pixelToComponents(int combineType, int pixel) {
        int[] components = new int[4];
        components[0] = Color.alpha(pixel);

        if (combineType == Filter.COMBINE_ARGB) {
            components[1] = Color.red(pixel);
            components[2] = Color.green(pixel);
            components[3] = Color.blue(pixel);

        } else if (combineType == Filter.COMBINE_AHSV) {
            float[] hsv = new float[3];
            Color.colorToHSV(pixel, hsv);

            components[1] = (int) (hsv[0] * 255f / 360f);
            components[2] = (int) (hsv[1] * 255f);
            components[3] = (int) (hsv[2] * 255f);
        } else {
            throw new IllegalStateException(
                    "Unknown combineType in this PixelSorter's Filter: " + combineType);
        }

        return components;
    }


    private int componentsToPixel(int combineType, int[] components) {
        int alpha = components[0];

        if (combineType == Filter.COMBINE_ARGB) {
            int red = components[1];
            int green = components[2];
            int blue = components[3];

            return Color.argb(alpha, red, green, blue);

        } else if (combineType == Filter.COMBINE_AHSV) {
            float[] hsv = new float[3];
            hsv[0] = ((float) components[1]) * 360f / 255f;
            hsv[1] = ((float) components[2]) / 255f;
            hsv[2] = ((float) components[3]) / 255f;

            return Color.HSVToColor(alpha, hsv);

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
    public static PixelSorter from(Filter filter) {
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

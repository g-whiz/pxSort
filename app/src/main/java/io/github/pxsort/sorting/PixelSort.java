package io.github.pxsort.sorting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;

import java.util.Arrays;

import io.github.pxsort.filter.Filter;

/**
 * Created by George on 2016-01-17.
 */
public class PixelSort {


    public static void applyFilterAsync(final Bitmap bitmap, final Filter f,
                                        final OnFilterAppliedListener listener) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                applyFilter(bitmap, f);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onFilterApplied(bitmap);
            }
        }.execute();

    }

    public interface OnFilterAppliedListener {

        /**
         * Called on successful completion of applyFilterAsync.
         *
         * @param bitmap the Bitmap that the filter was applied to.
         */
        void onFilterApplied(Bitmap bitmap);
    }

    /**
     * Applies the pixel sort specified by f to bitmap.
     *
     * @param bitmap Image to apply f to.
     * @param f      The filter to apply to bitmap.
     */
    public static void applyFilter(Bitmap bitmap, Filter f) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        //Sort individual grid positions
        for (int row = 0; row < f.getNumRows(bmpHeight); row++)
            for (int column = 0; column < f.getNumCols(bmpWidth); column++) {

                int[] dimensions = getGridPositionDimensions(row, column, bmpWidth,
                        bmpHeight, f);
                int gridWidth = dimensions[2];
                int gridHeight = dimensions[3];
                int[] pixels = new int[gridWidth * gridHeight];

                int x = dimensions[0];
                int y = dimensions[1];
                int width = dimensions[2];
                int height = dimensions[3];
                bitmap.getPixels(pixels, 0, width, x, y,
                        width, height);

                switch (f.getBaseOp()) {
                    case SORT:
                        pixels = Sort.apply(pixels, f.getComponent(),
                                f.getOrder(), f.getZipMethod());
                        break;

                    case HEAPIFY:
                        pixels = Heapify.apply(pixels, f.getComponent(),
                                f.getOrder(), f.getZipMethod());
                        break;
                }

                bitmap.setPixels(pixels, 0, width, x, y, width, height);
            }
    }

    /**
     * Pixel-sorting-specific implementation of counting sort.
     */
    public static class Sort {

        public static int[] apply(@ColorInt int[] pixels, int component, int order, int method) {

            int[] buckets = generateBuckets( generateHistogram(pixels, component), order);
            int[] newPixels = new int[pixels.length];

            for (int px : pixels) {
                int index = 0;
                switch (order) {
                    case ASCENDING:
                        index = getComponent(px, order);
                        break;

                    case DESCENDING:
                        index = buckets.length - (getComponent(px, component) + 1);
                        break;
                }

                newPixels[buckets[index]] =
                        applyZipMethod(newPixels[buckets[index]], px, component, method);

                //Increment the index of the bucket
                buckets[index]++;
            }

            return newPixels;
        }


        /**
         *
         * @param pixels
         * @param component
         * @return
         */
        private static int[] generateHistogram (@ColorInt int[] pixels, int component) {
            int numBuckets;
            if (component <= 2) {
                numBuckets = 256;
            } else if (component == SATURATION || component == VALUE) {
                numBuckets = 101;
            } else {
                numBuckets = 360;
            }

            int[] histogram = new int[numBuckets];

        /*Increment the count of the number of pixels with the component value of px.*/
            for (int px : pixels) {
                histogram[ getComponent(px, component) ]++;
            }

            return histogram;
        }


        private static int[] generateBuckets(int[] histogram, int order) {
            int[] buckets = new int[histogram.length];
            buckets[0] = 0;
            for (int i = 0; i < histogram.length - 1; i++) {
                switch (order) {
                    case ASCENDING:
                        buckets[i + 1] = histogram[i];
                        break;
                    case DESCENDING:
                        buckets[i + 1] = histogram[histogram.length - (i + 1)];
                        break;
                }
            }

            return buckets;
        }

    }

    /**
     * Pixel-sorting-specific implementation of heapify.
     */
    public static class Heapify {

        /**
         *
         * @param pixels int[] Array of ARGB pixels.
         * @param component int One of the COMPONENT CONSTANTs.
         * @param order int One of the ORDER CONSTANTs.
         *              ASCENDING builds a min-heap, DESCENDING builds a max-heap.
         * @param zipMethod int How to combine the sorted pixels with the original image.
         *                  One of the ZIP METHOD CONSTANTs.
         * @return int[] Array of ARGB pixels.
         */
        public static int[] apply(@ColorInt int[] pixels, int component, int order, int zipMethod) {

            int[] newPixels = Arrays.copyOf(pixels, pixels.length);
            newPixels = buildHeap(newPixels, component, order);

            for (int i = 0; i < newPixels.length; i++) {
                newPixels[i] = applyZipMethod(pixels[i], newPixels[i], component, zipMethod);
            }

            return newPixels;
        }

        private static int[] buildHeap (int[] heap, int component, int order) {

            for(int i = heap.length / 2; i >= 0; i--) {
                heapify(heap, component, order, i);
            }

            return heap;
        }

        private static void heapify(int[] heap, int component, int order, int index) {
            int leftIndex = leftChildIndex(index);
            int rightIndex = rightChildIndex(index);

            int largest = index;

            if (rightIndex < heap.length) {
                switch (order) {
                    case DESCENDING:
                        if (getComponent(heap[leftIndex], component)
                                > getComponent(heap[index], component)) {
                            largest = leftIndex;
                        }
                        if (getComponent(heap[rightIndex], component)
                                > getComponent(heap[largest], component)) {
                            largest = rightIndex;
                        }
                        break;

                    case ASCENDING:
                        if (getComponent(heap[leftIndex], component)
                                < getComponent(heap[index], component)) {
                            largest = leftIndex;
                        }
                        if (getComponent(heap[rightIndex], component)
                                < getComponent(heap[largest], component)) {
                            largest = rightIndex;
                        }
                        break;
                }
            }

            if (largest != index) {
                swap(heap, largest, index);
                heapify(heap, component, order, largest);
            }
        }

        private static void swap(int[] heap, int index1, int index2) {
            int temp = heap[index1];
            heap[index1] = heap[index2];
            heap[index2] = temp;
        }

        private static int leftChildIndex(int parentIndex) {
            return 2 * parentIndex;
        }

        private static int rightChildIndex(int parentIndex) {
            return 2 * parentIndex + 1;
        }
    }


    private static int[] getGridPositionDimensions(int row, int column, int bitmapWidth,
                                                   int bitmapHeight, Filter f) {
        int numCols = f.getNumCols(bitmapWidth);
        int numRows = f.getNumRows(bitmapHeight);

        int gridWidth = bitmapWidth / numCols
                + (column < bitmapWidth % numCols ? 1 : 0);

        int gridHeight = bitmapHeight / numRows +
                (row < bitmapHeight % numRows ? 1 : 0);

        int gridX = column * (bitmapWidth / numCols) +
                (column < bitmapWidth % numCols ? column : bitmapWidth % numCols);

        int gridY = row * (bitmapHeight / numRows) +
                (row < bitmapHeight % numRows ? row : bitmapHeight % numRows);

        return new int[]{gridX, gridY, gridWidth, gridHeight};
    }


    private static int applyZipMethod(@ColorInt int dest, @ColorInt int source,
                                      int component, int zipMethod) {
        switch (zipMethod) {
            case MOVE_PIXELS:
                return source;

            case MOVE_COMPONENT:
                return replaceComponent(source, dest, component);

            case MOVE_RED:
                return replaceComponent(source, dest, RED);

            case MOVE_GREEN:
                return replaceComponent(source, dest, GREEN);

            case MOVE_BLUE:
                return replaceComponent(source, dest, BLUE);

            case MOVE_HUE:
                return replaceComponent(source, dest, HUE);

            case MOVE_SATURATION:
                return replaceComponent(source, dest, SATURATION);

            case MOVE_VALUE:
                return replaceComponent(source, dest, VALUE);

            default: //???
                return 0;
        }
    }


    private static int replaceComponent(@ColorInt int dest, @ColorInt int source, int component) {
        int r, g, b;
        float[] destHSV;
        float[] sourceHSV;
        switch (component) {
            case RED:
                r = Color.red(source);
                g = Color.green(dest);
                b = Color.blue(dest);
                return Color.argb(0xFF, r, g, b);

            case GREEN:
                r = Color.red(dest);
                g = Color.green(source);
                b = Color.blue(dest);
                return Color.argb(0xFF, r, g, b);

            case BLUE:
                r = Color.red(dest);
                g = Color.green(dest);
                b = Color.blue(source);
                return Color.argb(0xFF, r, g, b);

            case HUE:
                destHSV = new float[3];
                sourceHSV = new float[3];
                Color.colorToHSV(dest, destHSV);
                Color.colorToHSV(source, sourceHSV);
                destHSV[0] = sourceHSV[0];
                return Color.HSVToColor(destHSV);

            case SATURATION:
                destHSV = new float[3];
                sourceHSV = new float[3];
                Color.colorToHSV(dest, destHSV);
                Color.colorToHSV(source, sourceHSV);
                destHSV[1] = sourceHSV[1];
                return Color.HSVToColor(destHSV);

            case VALUE:
                destHSV = new float[3];
                sourceHSV = new float[3];
                Color.colorToHSV(dest, destHSV);
                Color.colorToHSV(source, sourceHSV);
                destHSV[2] = sourceHSV[2];
                return Color.HSVToColor(destHSV);

            default:
                return 0;
        }
    }


    private static int getComponent(@ColorInt int px, int component) {
        float[] hsv;
        switch (component) {
            case RED:
                return Color.red(px);

            case GREEN:
                return Color.green(px);

            case BLUE:
                return Color.blue(px);

            case HUE:
                hsv = new float[3];
                Color.colorToHSV(px, hsv);
                return (int) hsv[0];

            case SATURATION:
                hsv = new float[3];
                Color.colorToHSV(px, hsv);
                return (int) (100f * hsv[1]);

            case VALUE:
                hsv = new float[3];
                Color.colorToHSV(px, hsv);
                return (int) (100f * hsv[2]);

            default:
                return 0;
        }
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

    //// TODO: 2015-12-25 Add brightness to components if/when brightness included in Color


    /* ***** ORDER CONSTANTS ***** */

    /**
     * ORDER CONSTANT: Sort pixels in descending order.
     */
    public static final int DESCENDING = 0;

    /**
     * ORDER CONSTANT: Sort pixels in ascending order.
     */
    public static final int ASCENDING = 1;


    /* ***** ZIP METHOD CONSTANTS ***** */

    /**
     * ZIP METHOD CONSTANT: Move entire pixels.
     */
    public static final int MOVE_PIXELS = 0;

    /**
     * ZIP METHOD CONSTANT: Move only the selected component.
     */
    public static final int MOVE_COMPONENT = 1;

    /**
     * ZIP METHOD CONSTANT: Move only the red component.
     */
    public static final int MOVE_RED = 2;

    /**
     * ZIP METHOD CONSTANT: Move only the green component.
     */
    public static final int MOVE_GREEN = 3;

    /**
     * ZIP METHOD CONSTANT: Move only the blue component.
     */
    public static final int MOVE_BLUE = 4;

    /**
     * ZIP METHOD CONSTANT: Move only the hue component.
     */
    public static final int MOVE_HUE = 5;

    /**
     * ZIP METHOD CONSTANT: Move only the saturation component.
     */
    public static final int MOVE_SATURATION = 6;

    /**
     * ZIP METHOD CONSTANT: Move only the value component.
     */
    public static final int MOVE_VALUE = 7;


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

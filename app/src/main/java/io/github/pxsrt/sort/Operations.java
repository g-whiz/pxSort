package io.github.pxsrt.sort;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

import static io.github.pxsrt.sort.OperationsConstants.*;


/**
 * Container class for all of the pixel sorting algorithms.
 *
 * Created by George on 2015-12-17.
 */
public class Operations {

    private interface Operation {
        //// TODO: 2015-12-30 Figure out how to use the @Contract annotation to improve type-safeness 
        //@Contract("null->fail")
        int[] apply(@ColorInt int[] pixels, int component, int order, int method);
    }

    public static class BucketSort implements Operation {

        public BucketSort () {}

        public int[] apply(@ColorInt int[] pixels, int component, int order, int method) {

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
                        applyMethod(newPixels[buckets[index]], px, component, method);

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


        /**
         *
         * @param histogram
         * @param order
         * @return
         */
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


    public static class Heapify implements Operation{

        public Heapify() {}

        /**
         *
         * @param pixels int[] Array of ARGB pixels.
         * @param component int One of the "COMPONENT CONSTANTS" in
         *              {@link io.github.pxsrt.sort.OperationsConstants OperationsConstants}.
         * @param order int One of the "ORDER CONSTANTS" in
         *              {@link io.github.pxsrt.sort.OperationsConstants OperationsConstants}.
         *              ASCENDING builds a min-heap, Descending builds a max-heap.
         * @param method int One of the "METHOD CONSTANTS" in
         *              {@link io.github.pxsrt.sort.OperationsConstants OperationsConstants}.
         * @return
         */
        public int[] apply(@ColorInt int[] pixels, int component, int order, int method) {

            int[] newPixels = Arrays.copyOf(pixels, pixels.length);
            newPixels = buildHeap(newPixels, component, order);

            for (int i = 0; i < newPixels.length; i++) {
                newPixels[i] = applyMethod(pixels[i], newPixels[i], component, method);
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

            if (leftIndex < heap.length) {
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


    private static int applyMethod(@ColorInt int dest, @ColorInt int source,
                                   int component, int method) {
        switch (method) {
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


    private static int replaceComponent(int dest, int source, int component) {
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


    private static int getComponent(int px, int component) {
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
}

package io.github.pxsrt.sort;

import android.graphics.Color;

/**
 * Base class for the pixel sorting algorithm.
 *
 * Created by George on 2015-12-17.
 */
public class PixelSort {

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


    /**
     * Sort pixels in descending order.
     */
    public static final int DESCENDING = 0;

    /**
     * Sort pixels in ascending order.
     */
    public static final int ASCENDING = 1;

//// TODO: 2015-12-18 max/min-heap (build heap instead of sorting)
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


    public static int[] apply(int[] pixels, int component, int order, int method) {

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

            //Apply sort method.
            switch (method) {
                case MOVE_PIXELS:
                    newPixels[buckets[index]] = px;
                    break;

                case MOVE_COMPONENT:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, component);
                    break;

                case MOVE_RED:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, RED);
                    break;

                case MOVE_GREEN:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, GREEN);
                    break;

                case MOVE_BLUE:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, BLUE);
                    break;

                case MOVE_HUE:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, HUE);
                    break;

                case MOVE_SATURATION:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, SATURATION);
                    break;

                case MOVE_VALUE:
                    newPixels[buckets[index]] =
                            replaceComponent(newPixels[buckets[index]], px, VALUE);
                    break;
            }

            //Increment the index of the bucket
            buckets[index]++;
        }

        return newPixels;
    }

    protected static int replaceComponent(int dest, int source, int component) {
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

    /**
     *
     * @param pixels
     * @param component
     * @return
     */
    private static int[] generateHistogram (int[] pixels, int component) {
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

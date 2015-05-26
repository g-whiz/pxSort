package com.github.pxsrt;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.util.Arrays;
import java.util.Comparator;


/**A class for applying the class of pixel sorting algorithm invented by Kim Asendorf.
 *
 * The sort is applied either by row or by column.
 *
 * For each row or column P, the index of the first pixel in P satisfying some Predicate is found
 * and the index of the last pixel in P satisfying some other Predicate is found.
 *
 * Then, the pixels between the first and last indices found in P (including the pixels at the given
 * indices) are sorted according to the order determined by some Comparator.
 *
 * Created by George on 2015-05-22.
 */
public class AsendorfSort implements Sort{

    public static final String TAG = AsendorfSort.class.getSimpleName();

    public static final int SORT_BY_ROW = 0;
    public static final int SORT_BY_COLUMN = 1;

    private static final int PIXELS_PER_THREAD = 1000000;

    private Comparator<Pixel> comparator;

    private Predicate<Pixel> from;
    private Predicate<Pixel> to;

    private int direction;

    /**Sole, parametrized constructor. Takes a Comparator, two Predicates, and either SORT_BY_ROW
     * or SORT_BY_COLUMN. See the description of this class for an overview of the function of each
     * component.
     *
     * @param comparator
     * @param from
     * @param to
     * @param direction
     */
    public AsendorfSort (Comparator<Pixel> comparator, Predicate<Pixel> from, Predicate<Pixel> to,
                         int direction) {
        this.comparator = comparator;
        this.from = from;
        this.to = to;

        if (direction == SORT_BY_COLUMN || direction == SORT_BY_ROW) {
            this.direction = direction;
        } else {
            Log.d(TAG, "Invalid direction. Using SORT_BY_ROW by default.");
            this.direction = SORT_BY_ROW;
        }
    }

    public void apply(final Bitmap img){

        int pixelArraysPerThread = getNumPixelArrays(img) / getNumThreads(img);

        for (int i = 1; i <= getNumThreads(img); i++) {

            final int firstIndex = (i - 1) * pixelArraysPerThread;
            final int lastIndex;

            if (i == getNumThreads(img)) {
                lastIndex = getNumPixelArrays(img) - 1;
            } else {
                lastIndex = i * pixelArraysPerThread - 1;
            }

            new Thread() {

                @Override
                public void run() {
                    for (int index = firstIndex; index <= lastIndex; index ++) {

                        Pixel[] pixels = null;

                        switch (direction) {
                            case SORT_BY_COLUMN:
                                setColumn(img, sort(getColumn(img, index)), index);
                                break;

                            case SORT_BY_ROW:
                                setRow(img, sort(getRow(img, index)), index);
                                break;
                        }

                    }
                }

            }.start();

        }

    }

    private int getNumPixelArrays(Bitmap img) {

        switch (direction) {
            case SORT_BY_COLUMN:
                return img.getWidth();

            case SORT_BY_ROW:
                return img.getHeight();

            default:
                return 0;
        }
    }

    private int getNumThreads(Bitmap img) {
        return (img.getWidth() * img.getHeight()) / PIXELS_PER_THREAD + 1;
    }

    public Pixel[] sort(Pixel[] pixels){
        int startIndex = getStartIndex(pixels, from);
        int endIndex = getEndIndex(pixels, to);

        Pixel[] pixelsToSort = Arrays.copyOfRange(pixels, startIndex, endIndex);
        Arrays.sort(pixelsToSort, comparator);

        for (int i = startIndex; i <= startIndex; i++) {
            pixels[i] = pixelsToSort[i - startIndex];
        }

        return pixels;
    }

    private int getStartIndex(Pixel[] pixels, Predicate<Pixel> from) {
        int startIndex = 0;

        for (int i = 0; i < pixels.length; i++) {
            if (from.apply(pixels[i])) {
                startIndex = i;
                break;
            }
        }

        return startIndex;
    }

    private int getEndIndex(Pixel[] pixels, Predicate<Pixel> to) {
        int endIndex = pixels.length - 1;

        for (int i = pixels.length - 1; i >= 0; i--) {
            if (to.apply(pixels[i])) {
                endIndex = i;
                break;
            }
        }

        return endIndex;
    }

    private Pixel[] getRow(Bitmap img, int row){
        Pixel[] pixels = new Pixel[img.getWidth()];
        for(int x = 0; x < img.getWidth(); x++) {
            pixels[x] = new Pixel(img.getPixel(x, row));
        }

        return pixels;
    }

    private void setRow(Bitmap img, Pixel[] pixels, int row) {
        if (pixels.length == img.getWidth()) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setPixel(x, row, pixels[x].getColor());
            }
        }
    }

    private Pixel[] getColumn(Bitmap img, int column){
        Pixel[] pixels = new Pixel[img.getHeight()];
        for(int y = 0; y < img.getHeight(); y++) {
            pixels[y] = new Pixel(img.getPixel(column, y));
        }

        return pixels;
    }

    private void setColumn(Bitmap img, Pixel[] pixels, int column){
        if (pixels.length == img.getHeight()) {
            for (int y = 0; y < img.getHeight(); y++) {
                img.setPixel(column, y, pixels[y].getColor());
            }
        }
    }
}

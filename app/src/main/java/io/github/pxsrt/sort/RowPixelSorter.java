package io.github.pxsrt.sort;

import android.graphics.Bitmap;
import android.util.Log;

import io.github.pxsrt.sort.comparator.ComponentComparator;
import io.github.pxsrt.sort.predicate.ComponentPredicate;

import java.util.Arrays;

/**TODO Documentation everywhere.
 * An abstract framework for a PixelSort that applies a pixel sorting algorithm to a Bitmap
 * row-by-row (or column-by-column).
 *
 *
 * Created by George on 2015-06-08.
 */
public class RowPixelSorter extends PixelSorter {

    /**This class' tag.*/
    public static final String TAG = RowPixelSorter.class.getSimpleName();

    /**Sort images row-by-row. */
    public static final int HORIZONTAL = 0;

    /**Sort images column-by-column.*/
    public static final int VERTICAL = 1;

    /**The comparator determining pixel ordering in this PixelSort.*/
    private final ComponentComparator comparator;

    /**The predicate determining the index in each row/column to apply this PixelSort's
     * pixel sorting algorithm from.
     */
    private final ComponentPredicate fromPredicate;

    /**The predicate determining the index in each row/column to apply this PixelSort's
     * pixel sorting algorithm from.
     */
    private final ComponentPredicate toPredicate;

    private int direction;

    /**Sole, parametrized constructor.
     *
     * @param comparator Comparator which determines how pixels are ordered when sortedCount.
     * @param fromPredicate Predicate determining the first pixel of each row to sort from.
     * @param toPredicate Predicate determining the last pixel of each row to sort to.
     * @param direction Either HORIZONTAL or VERTICAL.
     */
    public RowPixelSorter(ComponentComparator comparator, ComponentPredicate fromPredicate,
                             ComponentPredicate toPredicate, int direction) {
        this.comparator = comparator;
        this.fromPredicate = fromPredicate;
        this.toPredicate = toPredicate;

        if (direction == VERTICAL || direction == HORIZONTAL) {
            this.direction = direction;
        } else {
            Log.d(TAG, "Invalid direction. Using HORIZONTAL by default.");
            this.direction = HORIZONTAL;
        }
    }

    @Override
    public void apply(final Bitmap img) {
        Log.d(TAG, "Sorting...");

        final int[] colors = new int[img.getHeight() * img.getWidth()];
        img.getPixels(colors, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        switch (direction) {
            case HORIZONTAL:
                for (int row = 0; row < img.getHeight(); row++) {
                    sortRow(colors, img.getWidth(), row);
                }
                break;

            case VERTICAL:
                for (int col = 0; col < img.getWidth(); col++) {
                    sortColumn(colors, img.getHeight(), col);
                }
                break;

            default: //Due to the constructor, this line should not ever be reached.
                throw new IllegalStateException("Invalid direction: " + direction);
        }

        img.setPixels(colors, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        Log.d(TAG, "Sorted.");
    }

    private void sortRow(final int[] colors, final int imgWidth, final int row) {
        Pixel[] pixels = getRow(colors, imgWidth, row);
        sort(pixels);
        setRow(colors, pixels, row);
    }

    private void sortColumn(final int[] colors, final int imgHeight, final int col) {
        Pixel[] pixels = getColumn(colors, imgHeight, col);
        sort(pixels);
        setColumn(colors, pixels, col);
    }

    private Pixel[] getRow(int[] colors, int imgWidth, int row){
        Pixel[] pixels = new Pixel[imgWidth];
        for(int col = 0; col < imgWidth; col++) {
            int color;
            color = colors[row * imgWidth + col];
            pixels[col] = new Pixel(color);
        }

        return pixels;
    }

    private void setRow(int[] colors, Pixel[] pixels, int row) {
        for (int col = 0; col < pixels.length; col++) {
            int color = pixels[col].getColor();
            colors[row * pixels.length + col] = color;
        }
    }

    private Pixel[] getColumn(int[] colors, int imgHeight, int col){
        Pixel[] pixels = new Pixel[imgHeight];
        for(int row = 0; row < imgHeight; row++) {
            int color;
            color = colors[col * imgHeight + row];
            pixels[row] = new Pixel(color);
        }

        return pixels;
    }

    private void setColumn(int[] colors, Pixel[] pixels, int col){
        for (int row = 0; row < pixels.length; row++) {
            int color = pixels[row].getColor();
            colors[col * pixels.length + row] = color;
        }
    }

    /**Applies a pixel sorting algorithm to an array of Pixels
     * (mutates the array).
     *
     * @param pixels Array of Pixels to be sortedCount.
     */
    protected Pixel[] sort(Pixel[] pixels){
        int fromIndex = getFromIndex(pixels);
        int toIndex = getToIndex(pixels);

        Arrays.sort(pixels, fromIndex, toIndex, comparator);

        return pixels;
    }

    /**Returns the index of the first pixel in pixels satisfying the condition in fromPredicate.
     *
     * @param pixels The pixels to get the index from.
     * @return The index of the first pixel in pixels satisfying the condition in fromPredicate.
     */
    protected int getFromIndex(Pixel[] pixels) {
        int startIndex = 0;

        for (int i = 0; i < pixels.length; i++) {
            if (fromPredicate.apply(pixels[i])) {
                startIndex = i;
                break;
            }
        }

        return startIndex;
    }

    /**Returns the index of the last pixel in pixels satisfying the condition in toPredicate.
     *
     * @param pixels The pixels to get the index from.
     * @return The index of the last pixel in pixels satisfying the condition in toPredicate.
     */
    protected int getToIndex(Pixel[] pixels) {
        int endIndex = pixels.length - 1;

        for (int i = pixels.length - 1; i >= 0; i--) {
            if (toPredicate.apply(pixels[i])) {
                endIndex = i;
                break;
            }
        }

        return endIndex;
    }

    public ComponentComparator getComparator() {
        return comparator;
    }

    public int getDirection() {
        return direction;
    }

    public ComponentPredicate getFromPredicate() {
        return fromPredicate;
    }

    public ComponentPredicate getToPredicate() {
        return toPredicate;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RowPixelSorter
                && comparator.equals(((RowPixelSorter)o).getComparator())
                && fromPredicate.equals(((RowPixelSorter)o).getFromPredicate())
                && toPredicate.equals(((RowPixelSorter)o).getToPredicate())
                && direction == ((RowPixelSorter)o).getDirection();
    }
}

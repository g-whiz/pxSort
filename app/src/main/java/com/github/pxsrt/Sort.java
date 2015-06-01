package com.github.pxsrt;

import android.graphics.Bitmap;

/**
 * Created by George on 2015-05-25.
 */
public abstract class Sort {

    //TODO Make this an abstract class and move the non-Asendorf-specific
    //methods over from AsendorfSort

    /**Applies a pixel sorting algorithm to the given Bitmap.
     *
     * NOTE: this method mutates the given Bitmap
     *
     * @param img Bitmap to which the sort will be applied.
     */
    public abstract void apply(Bitmap img);

    /**
     * Adds a Callback to this Sort's callbacks.
     */
    public abstract void addCallback(Callback callback);

    protected Pixel[] getRow(Bitmap img, int row){
        Pixel[] pixels = new Pixel[img.getWidth()];
        for(int x = 0; x < img.getWidth(); x++) {
            pixels[x] = new Pixel(img.getPixel(x, row));
        }

        return pixels;
    }

    protected void setRow(Bitmap img, Pixel[] pixels, int row) {
        if (pixels.length == img.getWidth()) {
            for (int col = 0; col < img.getWidth(); col++) {
                synchronized (img) {
                    img.setPixel(col, row, pixels[col].getColor());
                }
            }
        }
    }

    protected Pixel[] getColumn(Bitmap img, int column){
        Pixel[] pixels = new Pixel[img.getHeight()];
        for(int y = 0; y < img.getHeight(); y++) {
            pixels[y] = new Pixel(img.getPixel(column, y));
        }

        return pixels;
    }

    protected void setColumn(Bitmap img, Pixel[] pixels, int column){
        if (pixels.length == img.getHeight()) {
            for (int y = 0; y < img.getHeight(); y++) {
                synchronized (img) {
                    img.setPixel(column, y, pixels[y].getColor());
                }
            }
        }
    }

    /**
     * Callback interface for asynchronous pixel sorting.
     */
    public static interface Callback {

        /**
         * Callback method called on completion of one application of a sort.
         */
        public void sortComplete();
    }
}

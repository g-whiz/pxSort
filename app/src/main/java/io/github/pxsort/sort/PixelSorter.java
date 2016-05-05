package io.github.pxsort.sort;

import android.graphics.Bitmap;

import io.github.pxsort.filter.Filter;

/**
 * Created by George on 2016-05-04.
 */
public enum PixelSorter {

    SORT {
        @Override
        public void applyTo(Bitmap bitmap) {

        }
    },

    HEAPIFY {
        @Override
        public void applyTo(Bitmap bitmap) {

        }
    },

    BST {
        @Override
        public void applyTo(Bitmap bitmap) {

        }
    };

    public abstract void applyTo(Bitmap bitmap);

    public static PixelSorter from(Filter f) {
        // TODO: 2016-05-04 Use this method to retrieve the PixelSorter corresponding to f.
        // Why? Nice syntax: PixelSorter.from(filter).applyTo(bitmap);
        return null;
    }
}

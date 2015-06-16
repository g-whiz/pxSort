package com.github.pxsrt.sort;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by George on 2015-06-08.
 */
public abstract class AbstractRowPixelSort extends PixelSort {

    public static final String TAG = AbstractRowPixelSort.class.getSimpleName();

    public static final int SORT_BY_ROW = 0;
    public static final int SORT_BY_COLUMN = 1;

    protected Comparator<Pixel> comparator;

    protected Predicate<Pixel> fromPredicate;
    protected Predicate<Pixel> toPredicate;

    private Executor executor;
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 16;
    private static final long KEEP_ALIVE_TIME = 500L; // In milliseconds.

    private int rowsSorted;
    private final Object rowsSortedLock = new Object();

    private int direction;

    /**Sole, parametrized constructor.
     *
     * @param comparator Comparator which determines how pixels are ordered when sorted.
     * @param fromPredicate Predicate determining the first pixel of each row to sort from.
     * @param toPredicate Predicate determining the last pixel of each row to sort to.
     * @param direction Either SORT_BY_ROW or SORT_BY_COLUMN.
     */
    protected AbstractRowPixelSort(Comparator<Pixel> comparator, Predicate<Pixel> fromPredicate,
                                   Predicate<Pixel> toPredicate, int direction) {
        super();
        this.comparator = comparator;
        this.fromPredicate = fromPredicate;
        this.toPredicate = toPredicate;

        this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        if (direction == SORT_BY_COLUMN || direction == SORT_BY_ROW) {
            this.direction = direction;
        } else {
            Log.d(TAG, "Invalid direction. Using SORT_BY_ROW by default.");
            this.direction = SORT_BY_ROW;
        }
    }

    protected abstract Pixel[] sort(Pixel[] pixels);

    private void sortRow(final Bitmap img, final int row) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Pixel[] pixels = getRow(img, row);
                sort(pixels);
                setRow(img, pixels, row);
                rowSorted();
            }
        });
    }

    private void sortColumn(final Bitmap img, final int col) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Pixel[] pixels = getColumn(img, col);
                sort(pixels);
                setColumn(img, pixels, col);
                rowSorted();
            }
        });
    }

    private int getRowsSorted() {
        synchronized (rowsSortedLock) {
            return rowsSorted;
        }
    }

    private void rowSorted() {
        synchronized (rowsSortedLock) {
            rowsSorted++;
        }
    }

    private void clearRowsSorted() {
        synchronized (rowsSortedLock) {
            rowsSorted = 0;
        }
    }

    @Override
    public void apply(final Bitmap img) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                clearRowsSorted();
                Log.d(TAG, "Sorting...");

                switch (direction) {
                    case SORT_BY_ROW:
                        for (int row = 0; row < img.getHeight(); row++) {
                            sortRow(img, row);
                        }
                        break;

                    case SORT_BY_COLUMN:
                        for (int col = 0; col < img.getWidth(); col++) {
                            sortColumn(img, col);
                        }
                        break;

                    default:
                        throw new IllegalStateException("Invalid direction: " + direction);
                }

                while(getRowsSorted() != img.getWidth()){
                    try {
                        Log.d(TAG, "Waiting 0.1s for sort to finish.");
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Thread interrupted.", e);
                    }
                }
                Log.d(TAG, "Sorted.");

                notifyCallbacks();
            }
        }).start();
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
            for (int col = 0; col < img.getWidth(); col++) {
                synchronized (img) {
                    img.setPixel(col, row, pixels[col].getColor());
                }
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
                synchronized (img) {
                    img.setPixel(column, y, pixels[y].getColor());
                }
            }
        }
    }

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
}

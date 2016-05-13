package io.github.pxsort.sorting.partition;

import android.graphics.Bitmap;

/**
 * A Partition that divides a Bitmap into a grid.
 * <p/>
 * Created by George on 2016-05-05.
 */
public class GridPartition extends Partition {

    private final int rows;
    private final int columns;

    private final int bitmapWidth;
    private final int bitmapHeight;

    private int currRow;
    private int currColumn;

    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * Sole constructor.
     *
     * @param bitmap  The bitmap to partition.
     * @param rows    The number of rows in the grid. Maximum 64.
     * @param columns The number of columns in the grid. Maximum 64.
     */
    public GridPartition(Bitmap bitmap, int rows, int columns) {
        super(bitmap);
        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();

        if (bitmapHeight <= Math.min(64, rows)) {
            this.rows = 1;
        } else {
            this.rows = Math.min(64, rows);
        }

        if (bitmapWidth <= Math.min(64, columns)) {
            this.columns = 1;
        } else {
            this.columns = Math.min(64, columns);
        }

        currRow = 0;
        currColumn = -1; //to account for the initial +1 at the first call of next()
        updatePartitionDimensions();
    }


    @Override
    public int[] next() {
        if (!hasNext()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        currColumn++;
        if (currColumn == columns) {
            currColumn = 0;
            currRow++;
        }

        int[] partition = new int[width * height];
        getBitmap().getPixels(partition, 0, width, x, y, width, height);

        return partition;
    }

    /**
     * Updates x, y, width, and calculateBSTHeight to reflect the dimensions of the current partition.
     */
    private void updatePartitionDimensions() {
        x = currColumn * (bitmapWidth / columns) +
                (currColumn < bitmapWidth % columns ? currColumn : bitmapWidth % columns);

        y = currRow * (bitmapHeight / rows) +
                (currRow < bitmapHeight % rows ? currRow : bitmapHeight % rows);

        width = bitmapWidth / columns
                + (currColumn < bitmapWidth % columns ? 1 : 0);

        height = bitmapHeight / rows +
                (currRow < bitmapHeight % rows ? 1 : 0);
    }

    @Override
    public boolean hasNext() {
        return currRow < (rows - 1)
                || (currRow == (rows - 1)
                && (currColumn + 1) < columns);
    }

    @Override
    public void set(int[] partition) {
        getBitmap().setPixels(partition, 0, width, x, y, width, height);
    }
}

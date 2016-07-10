package io.pxsort.pxsort.sorting.partition;

import android.graphics.Bitmap;

/**
 * A Partition that divides a Bitmap into a grid.
 * <p/>
 * Created by George on 2016-05-05.
 */
class GridPartition extends Partition {

    private final int rows;
    private final int columns;

    private final int bitmapWidth;
    private final int bitmapHeight;

    private Bitmap partBitmap;
    private final int[] pixelBuf;

    private int currPartRow;
    private int currPartColumn;

    private int partX;
    private int partY;
    private int partWidth;
    private int partHeight;

    /**
     * Sole constructor.
     *
     * @param src  The bitmap to partition.
     * @param rows    The number of rows in the grid. Maximum 64.
     * @param columns The number of columns in the grid. Maximum 64.
     */
    public GridPartition(Bitmap src, int rows, int columns) {
        super(src);
        this.bitmapWidth = src.getWidth();
        this.bitmapHeight = src.getHeight();

        this.rows = Math.min(bitmapHeight, rows);

        this.columns = Math.min(bitmapWidth, columns);

        this.partBitmap = createPartBitmap();
        // at this point, partBitmap has the dimensions of the maximum-sized partition
        pixelBuf = new int[partBitmap.getHeight() * partBitmap.getWidth()];

        this.currPartRow = 0;
        this.currPartColumn = -1; //to account for the initial +1 at the first call of next()
    }


    @Override
    public Bitmap next() {
        if (!hasNext()) {
            throw new ArrayIndexOutOfBoundsException(
                    "next() called on GridPartition with no next partition.");
        }

        if (partBitmap.isRecycled()) {
            partBitmap = createPartBitmap();
        }

        // update the coordinate of the current partition in the partition grid
        currPartColumn++;
        if (currPartColumn == columns) {
            currPartColumn = 0;
            currPartRow++;
        }

        updateToNextPartition();
        src.getPixels(pixelBuf, 0, partWidth, partX, partY, partWidth, partHeight);
        partBitmap.setPixels(pixelBuf, 0, partWidth, 0, 0, partWidth, partHeight);

        return partBitmap;
    }


    private Bitmap createPartBitmap() {
        int maxPartHeight = bitmapHeight / rows + (bitmapHeight % rows > 0 ? 1 : 0);
        int maxPartWidth = bitmapWidth / columns + (bitmapWidth % columns > 0 ? 1 : 0);

        return Bitmap.createBitmap(maxPartWidth, maxPartHeight, Bitmap.Config.ARGB_8888);
    }


    /**
     * Updates partX, partY, partWidth, and partHeight to reflect the
     * dimensions of the current partition.
     */
    private void updateToNextPartition() {
        partX = currPartColumn * (bitmapWidth / columns) +
                (currPartColumn < bitmapWidth % columns ? currPartColumn : bitmapWidth % columns);

        partY = currPartRow * (bitmapHeight / rows) +
                (currPartRow < bitmapHeight % rows ? currPartRow : bitmapHeight % rows);

        partWidth = bitmapWidth / columns
                + (currPartColumn < bitmapWidth % columns ? 1 : 0);

        partHeight = bitmapHeight / rows +
                (currPartRow < bitmapHeight % rows ? 1 : 0);

        if (partBitmap.getWidth() != partWidth
                || partBitmap.getHeight() != partHeight) {
            partBitmap.reconfigure(partWidth, partHeight, Bitmap.Config.ARGB_8888);
        }
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = currPartRow < (rows - 1)
                || (currPartRow == (rows - 1)
                && (currPartColumn + 1) < columns);

        if (!hasNext && !partBitmap.isRecycled()) {
            partBitmap.recycle();
        }

        return hasNext;
    }

    @Override
    public void set(Bitmap partition) {
        if (partition.isRecycled()) {
            throw new IllegalArgumentException(
                    "Cannot set the current partition with a recycled bitmap.");
        }
        partition.getPixels(pixelBuf, 0, partWidth, 0, 0, partWidth, partHeight);
        src.setPixels(pixelBuf, 0, partWidth, partX, partY, partWidth, partHeight);
    }
}

package io.pxsort.pxsort.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;

/**
 * Created by George on 2016-08-16.
 */
abstract class AbstractFilterViewHolder extends RecyclerView.ViewHolder
        implements PixelSortingContext.OnImageReadyListener {

    private WeakReference<PixelSortingContext.BitmapWorkerTask> thumbTaskRef;
    protected final ImageView thumbnailView;
    protected final TextView titleView;

    /**
     * Sole constructor.
     *
     * @param itemView A view with an ImageView with id "thumbnail", and a
     *                 TextView with id "title" in its tree.
     */
    public AbstractFilterViewHolder(View itemView) {
        super(itemView);
        thumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
        titleView = (TextView) itemView.findViewById(R.id.title);

        if (thumbnailView == null || titleView == null) {
            throw new IllegalArgumentException("The provided itemView has an invalid layout.");
        }
    }


    /**
     * Sets this holder's BitmapWorkerTask
     *
     * @param thumbTask
     */
    public void setThumbTask(PixelSortingContext.BitmapWorkerTask thumbTask) {
        if (thumbTaskRef != null && thumbTaskRef.get() != null) {
            thumbTaskRef.get().cancel(true);
        }

        thumbTaskRef = new WeakReference<>(thumbTask);
    }


    /**
     * Gets this holder's BitmapWorkerTask, if it exists.
     *
     * @return This holder's BitmapWorkerTask, or null if the task has finished
     */
    public PixelSortingContext.BitmapWorkerTask getThumbTask() {
        return thumbTaskRef != null ? thumbTaskRef.get() : null;
    }


    @Override
    public void onImageReady(Bitmap bitmap) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                bitmap, thumbnailView.getWidth(), thumbnailView.getHeight());
        thumbnailView.setImageBitmap(thumbnail);
        bitmap.recycle();
    }
}

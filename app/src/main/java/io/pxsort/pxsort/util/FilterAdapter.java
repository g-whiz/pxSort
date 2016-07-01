package io.pxsort.pxsort.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * Adapter subclass for SortActivity UI
 * <p/>
 * Created by George on 2016-02-23.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterTileViewHolder> {

    private static final String TAG = FilterAdapter.class.getSimpleName();

    private static final int NONE_SELECTED = -1;
    private int selectedPosition;
    private FilterTileViewHolder selectedHolder;

    private List<Filter> filters;
    private PixelSortingContext sortingContext;
    private OnFilterSelectedListener selectionListener;


    public FilterAdapter(PixelSortingContext sortingContext, List<Filter> filters,
                         OnFilterSelectedListener selectionListener) {
        this.sortingContext = sortingContext;
        this.filters = filters;
        this.selectionListener = selectionListener;
        selectedPosition = NONE_SELECTED;

        selectedHolder = null;
    }


    @Override
    public FilterTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_filter_tile, parent, false);
        return new FilterTileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterTileViewHolder holder, int position) {
        // bind the Filter to the view
        Filter filter = filters.get(position);
        holder.title.setText(filter.name);

        if (holder.thumbView.getWidth() > 0 && holder.thumbView.getHeight() > 0) {
            loadImg(holder, filter);
        } else {
            // thumbView hasn't been measured
            loadImgOnMeasure(holder, filter);
        }

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // color the border of the thumbView
        int borderColor = getBorderColor(holder.itemView.getContext(), position);
        holder.thumbnailBorder.setBackgroundColor(borderColor);

        holder.itemView.setOnClickListener(holder);
    }

    @Override
    public void onViewRecycled(FilterTileViewHolder holder) {
        holder.itemView.setOnClickListener(null);

        if (holder.getAdapterPosition() == selectedPosition) {
            selectedHolder = null;
        }

        if (holder.getThumbTask() != null) {
            holder.getThumbTask().cancel(true);
        }

        if (holder.thumbView.getDrawable() instanceof BitmapDrawable) {
            //recycle old bitmap and clear the ImageView
            Bitmap bitmap =
                    ((BitmapDrawable) holder.thumbView.getDrawable()).getBitmap();
            holder.thumbView.setImageResource(R.color.primary_dark);

            bitmap.recycle();
        }

        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return filters.size();
    }


    private int getBorderColor(Context c, int position) {
        switch (position % 3) {
            case 0:
                return c.getResources().getColor(R.color.cyan);

            case 1:
                return c.getResources().getColor(R.color.magenta);

            case 2:
                return c.getResources().getColor(R.color.yellow);

            default: //Reaching this line is mathematically impossible...
                return 0;
        }
    }


    // Defers loading a Filter thumbnail until holder's views are guaranteed to be measured
    private void loadImgOnMeasure(final FilterTileViewHolder holder, final Filter filter) {
        holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        loadImg(holder, filter);

                        // views are now measured, no need to defer loading anymore
                        holder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
        });
    }


    private void loadImg(FilterTileViewHolder holder, Filter filter) {
        holder.setThumbTask(
                sortingContext.getPixelSortedImage(
                    filter, holder.thumbView.getWidth(),
                    holder.thumbView.getHeight(), holder)
        );
    }


    private void setSelected(FilterTileViewHolder holder, boolean isSelected) {
        int COLOR_SELECTED = holder.itemView.getContext()
                .getResources().getColor(R.color.secondary_dark);
        int COLOR_DESELECTED = Color.TRANSPARENT;

        if (isSelected) {
            holder.itemView.setBackgroundColor(COLOR_SELECTED);
        } else {
            holder.itemView.setBackgroundColor(COLOR_DESELECTED);
        }
    }


    /**
     * View holder for a FilterAdapter's Views.
     */
    protected class FilterTileViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PixelSortingContext.OnImageReadyListener {
        public final ImageView thumbView;
        public final View thumbnailBorder;
        public final TextView title;

        private WeakReference<PixelSortingContext.BitmapWorkerTask> thumbTaskRef;

        public FilterTileViewHolder(View itemView) {
            super(itemView);
            thumbView = (ImageView) itemView.findViewById(R.id.thumb);
            thumbnailBorder = itemView.findViewById(R.id.thumb_border);
            title = (TextView) itemView.findViewById(R.id.filter_title);
            thumbTaskRef = null;
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
        public void onClick(View v) {
            Filter selectedFilter = filters.get(getAdapterPosition());

            if (getAdapterPosition() != selectedPosition) {
                // this view has been selected. deselect the previously selected view
                if (selectedHolder != null) {
                    setSelected(selectedHolder, false);
                }

                setSelected(this, true);
                selectedHolder = this;
                selectedPosition = getAdapterPosition();

                selectionListener.onFilterSelected(true, selectedFilter);

            } else {
                // this view has been deselected
                setSelected(this, false);
                selectedHolder = null;
                selectedPosition = NONE_SELECTED;
                selectionListener.onFilterSelected(false, selectedFilter);
            }
        }


        @Override
        public void onImageReady(boolean success, Bitmap bitmap) {
            if (success) {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                        bitmap, thumbView.getWidth(), thumbView.getHeight());
                thumbView.setImageBitmap(thumbnail);
                bitmap.recycle();
            }
        }
    }


    /**
     * Interface for listening for user Filter-selection events.
     */
    public interface OnFilterSelectedListener {

        /**
         * Called whenever a filter is selected/deselected by a user.
         *
         * @param isSelected true when selected, false when deselected.
         * @param filter     the Filter that was selected/deselected
         */
        void onFilterSelected(boolean isSelected, Filter filter);
    }
}

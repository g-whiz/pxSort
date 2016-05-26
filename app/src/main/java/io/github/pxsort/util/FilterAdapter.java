package io.github.pxsort.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.pxsort.R;
import io.github.pxsort.sorting.PixelSortingContext;
import io.github.pxsort.sorting.filter.Filter;

/**
 * Adapter subclass for SortActivity UI
 * <p/>
 * Created by George on 2016-02-23.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterTileViewHolder> {

    private static final String TAG = FilterAdapter.class.getSimpleName();

    //array for keeping track of which filter(s) are selected
    private static final int NONE_SELECTED = -1;
    private int selectedPosition;
    private FilterTileViewHolder selectedHolder;

    private List<Filter> filters;
    private PixelSortingContext sortingContext;
    private OnFilterSelectedListener listener;

    public FilterAdapter(PixelSortingContext sortingContext, List<Filter> filters,
                         OnFilterSelectedListener listener) {
        this.sortingContext = sortingContext;
        this.filters = filters;
        this.listener = listener;
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
    public void onBindViewHolder(final FilterTileViewHolder holder, int position) {
        // bind the Filter to the view
        Filter filter = filters.get(position);
        holder.title.setText(filter.name);

        // If one or both of the thumbView's dimensions are 0 (e.g. before the view has been
        // measured), then the resulting loaded image will be meaninglessly small
        // (only 1 or 2 pixels). Thus, we skip the following code if that is the case
        if (holder.thumbView.getWidth() != 0 && holder.thumbView.getHeight() != 0) {
            holder.loaderTask = sortingContext.getPixelSortedImage(
                    filter, holder.thumbView.getWidth(),
                    holder.thumbView.getHeight(), holder);
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
        if (holder.position == selectedPosition) {
            selectedHolder = null;
        }

        if (holder.loaderTask != null) {
            holder.loaderTask.cancel(true);
            holder.loaderTask = null;
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


    /**
     * View holder for a FilterAdapter's Views.
     */
    protected class FilterTileViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PixelSortingContext.OnImageReadyListener {
        public final ImageView thumbView;
        public final View thumbnailBorder;
        public final TextView title;

        public AsyncTask<Void, Void, Bitmap> loaderTask;
        int position;

        public FilterTileViewHolder(View itemView) {
            super(itemView);
            thumbView = (ImageView) itemView.findViewById(R.id.thumb);
            thumbnailBorder = itemView.findViewById(R.id.thumb_border);
            title = (TextView) itemView.findViewById(R.id.filter_title);
            loaderTask = null;
        }

        @Override
        public void onClick(View v) {
            Filter selectedFilter = filters.get(getAdapterPosition());

            if (getAdapterPosition() != selectedPosition) {
                // this view has been selected. deselect the previously selected view
                if (selectedHolder != null) {
                    selectedHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }

                int selectedColor = itemView.getContext()
                        .getResources().getColor(R.color.secondary_dark);
                v.setBackgroundColor(selectedColor);

                selectedHolder = this;
                selectedPosition = getAdapterPosition();

                listener.onFilterSelected(true, selectedFilter);

            } else {
                // this view has been deselected
                v.setBackgroundColor(Color.TRANSPARENT);
                selectedHolder = null;
                selectedPosition = NONE_SELECTED;
                listener.onFilterSelected(false, selectedFilter);
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
            loaderTask = null;
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

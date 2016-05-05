package io.github.pxsort.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.pxsort.R;
import io.github.pxsort.filter.Filter;
import io.github.pxsort.sort.PixelSort;

/**
 * Adapter subclass for SortActivity UI
 * <p/>
 * Created by George on 2016-02-23.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterTileViewHolder> {

    private static final Bitmap.Config thumbnailConfig = Bitmap.Config.ARGB_8888;

    //array for keeping track of which filter(s) are selected
    private boolean[] isSelected;
    int numSelected;

    private List<Filter> filterList;

    private Bitmap thumbnailSrc;
    private boolean hasThumbnail;
    private boolean thumbnailSrcIsScaled;

    private OnFilterSelectionListener listener;

    /**
     * Custom ViewHolder subclass.
     */
    public class FilterTileViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public ImageView thumbnailBorder;
        public TextView title;

        public FilterTileViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.filter_thumb);
            thumbnailBorder = (ImageView) itemView.findViewById(R.id.filter_thumb_mask);
            title = (TextView) itemView.findViewById(R.id.filter_title);
        }
    }


    /**
     * Interface for listening for user Filter-selection events.
     */
    public interface OnFilterSelectionListener {

        /**
         * Called whenever a filter is selected/deselected by a user.
         *
         * @param isSelected true when selected, false when deselected.
         * @param filter     the Filter that was selected/deselected
         */
        void onFilterSelection(boolean isSelected, Filter filter);
    }


    public FilterAdapter(List<Filter> filterList, OnFilterSelectionListener listener) {
        this(filterList, null, listener);
    }


    /**
     * @param filterList
     * @param thumbnailSrc
     */
    public FilterAdapter(List<Filter> filterList, Bitmap thumbnailSrc,
                         OnFilterSelectionListener listener) {
        this.filterList = filterList;
        this.thumbnailSrc = thumbnailSrc;
        this.listener = listener;
        numSelected = 0;

        //boolean arrays init to all false
        isSelected = new boolean[filterList.size()];

        hasThumbnail = thumbnailSrc != null;
        thumbnailSrcIsScaled = false;
    }

    @Override
    public FilterTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_filter_tile, parent, false);
        return new FilterTileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilterTileViewHolder holder, final int position) {
        Filter filter = filterList.get(position);

        //dynamically color each view
        int foregroundColor = getForegroundColor(holder.itemView.getContext(), position);
        final int backgroundColor = getBackgroundColor(holder.itemView.getContext(), position);

        holder.thumbnailBorder.setColorFilter(foregroundColor);

        holder.title.setText(filter.getName());
        holder.title.setTextColor(foregroundColor);

        if (isSelected[position]) {
            holder.itemView.setBackgroundColor(backgroundColor);
        }


        if (hasThumbnail) {
            if (!thumbnailSrcIsScaled) {
                scaleThumbnail(holder.thumbnail.getWidth(), holder.thumbnail.getHeight());
            }

            //Apply filter to the thumbnail
            Bitmap bm = createSortedThumbnail(filter);
            holder.thumbnail.setImageBitmap(bm);
        } else {
            //color thumbnail area the same color as the border
            holder.thumbnail.setBackgroundColor(foregroundColor);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if any filters are already selected
                if (numSelected == 1) {
                    if (!isSelected[position]) {
                        //deselect the other filter
                        for (int i = 0; i < isSelected.length; i++) {
                            if (isSelected[i]) {
                                isSelected[i] = false;
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    } else {
                        numSelected--;
                    }
                }

                isSelected[position] = !(isSelected[position]);
                //Change the background color of the view when it is selected/deselected
                v.setBackgroundColor(isSelected[position] ? backgroundColor : Color.TRANSPARENT);
                listener.onFilterSelection(isSelected[position], filterList.get(position));
            }
        });
    }

    @Override
    public void onViewRecycled(FilterTileViewHolder holder) {
        if (holder.thumbnail.getDrawable() instanceof BitmapDrawable) {
            //recycle old bitmap
            Bitmap bitmap =
                    ((BitmapDrawable) holder.thumbnail.getDrawable()).getBitmap();
            bitmap.recycle();
        }

        super.onViewRecycled(holder);
    }

    private Bitmap createSortedThumbnail(Filter filter) {
        Bitmap sortedThumb = thumbnailSrc.copy(thumbnailConfig, true);

        PixelSort.applyFilter(sortedThumb, filter);
        return sortedThumb;
    }

    private void scaleThumbnail(int width, int height) {
        thumbnailSrc = Bitmap.createScaledBitmap(thumbnailSrc, width, height, false);
        thumbnailSrcIsScaled = true;
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    private int getForegroundColor(Context c, int position) {
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

    private int getBackgroundColor(Context c, int position) {
        switch (position % 3) {
            case 0:
                return c.getResources().getColor(R.color.yellow_light);

            case 1:
                return c.getResources().getColor(R.color.cyan_light);

            case 2:
                return c.getResources().getColor(R.color.magenta_light);

            default: //Reaching this line is mathematically impossible...
                return 0;
        }
    }

    public void setThumbnailSrc(Bitmap thumbnailSrc) {
        this.thumbnailSrc = thumbnailSrc;
        hasThumbnail = thumbnailSrc != null;

        //views can be updated with thumbnails
        if (hasThumbnail) {
            notifyDataSetChanged();
        }
    }
}

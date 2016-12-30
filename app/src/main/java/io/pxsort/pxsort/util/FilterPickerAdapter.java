package io.pxsort.pxsort.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * Adapter for SortActivity's filter picker.
 * <p>
 * Created by George on 2016-08-16.
 */
public class FilterPickerAdapter extends
        AbstractFilterAdapter<FilterPickerAdapter.FilterPickerViewHolder> {

    private static final String TAG = FilterPickerAdapter.class.getSimpleName();

    public static final int NONE_SELECTED = -1;
    private int selectedPosition;
    private FilterPickerViewHolder selectedHolder;

    private OnFilterSelectedListener selectionListener;


    public FilterPickerAdapter(PixelSortingContext psContext, List<Filter> filters,
                               OnFilterSelectedListener selectionListener, int selectedPosition) {
        super(psContext, filters);
        this.selectionListener = selectionListener;
        this.selectedPosition = selectedPosition;
        this.selectedHolder = null;
    }


    public FilterPickerAdapter(PixelSortingContext psContext, List<Filter> filters,
                               OnFilterSelectedListener selectionListener) {
        this(psContext, filters, selectionListener, NONE_SELECTED);
    }


    @Override
    public FilterPickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_filter_tile, parent, false);
        return new FilterPickerViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(FilterPickerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        setSelected(holder, position == selectedPosition);

        // color the border of the thumbView
        int borderColor = getBorderColor(holder.itemView.getContext(), position);
        holder.thumbnailBorder.setBackgroundColor(borderColor);
    }

    @Override
    public void onViewRecycled(FilterPickerViewHolder holder) {
        if (holder.getAdapterPosition() == selectedPosition) {
            selectedHolder = null;
        }

        super.onViewRecycled(holder);
    }

    /**
     * Returns the position of the currently selected item, or NONE_SELECTED if no item is selected.
     *
     * @return position of the currently selected item, or NONE_SELECTED if no item is selected
     */
    public int getSelectedPosition() {
        return selectedPosition;
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


    private void setSelected(FilterPickerViewHolder holder, boolean isSelected) {
        int COLOR_SELECTED = holder.itemView.getContext()
                .getResources().getColor(R.color.secondary_dark);
        int COLOR_DESELECTED = Color.TRANSPARENT;

        if (isSelected) {
            holder.itemView.setBackgroundColor(COLOR_SELECTED);
        } else {
            holder.itemView.setBackgroundColor(COLOR_DESELECTED);
        }
    }


    class FilterPickerViewHolder extends AbstractFilterViewHolder implements View.OnClickListener {

        public final View thumbnailBorder;

        /**
         * Sole constructor.
         *
         * @param itemView A view with an ImageView with id "thumbnail", a View with id
         *                 "thumb_border", and a TextView with id "title" in its tree.
         */
        public FilterPickerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.thumbnailBorder = itemView.findViewById(R.id.thumb_border);

            if (thumbnailBorder == null) {
                throw new IllegalArgumentException("The provided itemView has an invalid layout.");
            }
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

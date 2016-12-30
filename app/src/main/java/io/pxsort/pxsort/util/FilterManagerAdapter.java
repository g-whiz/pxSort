package io.pxsort.pxsort.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * Created by George on 2016-08-15.
 */
public class FilterManagerAdapter extends
        AbstractFilterAdapter<FilterManagerAdapter.FilterManagerViewHolder> {

    private FilterManagerEventListener listener;

    public FilterManagerAdapter(PixelSortingContext psContext, List<Filter> filters,
                                FilterManagerEventListener listener) {
        super(psContext, filters);
        this.listener = listener;
    }

    @Override
    public FilterManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_mgr_card, parent, false);
        return new FilterManagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterManagerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Filter f = filters.get(position);
        if (f.isBuiltIn) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void deleteFilter(int position) {
        filters.remove(position);
        notifyItemRemoved(position);
    }


    protected class FilterManagerViewHolder extends AbstractFilterViewHolder
            implements View.OnClickListener {

        public final View editButton;
        public final View deleteButton;

        /**
         * Sole constructor.
         *
         * @param itemView A view with an ImageView with id "thumbnail", and a
         *                 TextView with id "title" in its tree.
         */
        public FilterManagerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            this.editButton = itemView.findViewById(R.id.edit);
            this.editButton.setOnClickListener(this);

            itemView.findViewById(R.id.clone).setOnClickListener(this);

            this.deleteButton = itemView.findViewById(R.id.delete);
            this.deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Filter f = filters.get(getAdapterPosition());

            switch (view.getId()) {
                case R.id.card:
                case R.id.edit:
                    listener.onEditFilter(f);
                    return;

                case R.id.clone:
                    listener.onCloneFilter(f);
                    return;

                case R.id.delete:
                    deleteFilter(getAdapterPosition());
                    listener.onDeleteFilter(f);
            }
        }
    }

    public interface FilterManagerEventListener {

        /**
         * Called when a Filter has been selected to edit.
         *
         * @param filter The selected Filter.
         */
        void onEditFilter(Filter filter);


        /**
         * Called when a Filter has been selected to clone.
         *
         * @param filter The selected Filter.
         */
        void onCloneFilter(Filter filter);


        /**
         * Called when a Filter has been selected for deletion.
         *
         * @param filter The selected Filter.
         */
        void onDeleteFilter(Filter filter);
    }
}

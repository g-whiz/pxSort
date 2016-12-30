package io.pxsort.pxsort.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.PixelSortingContext;
import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * A class providing abstract functionality for generating previews from a list of filters.
 * <p>
 * Created by George on 2016-08-13.
 */
public abstract class AbstractFilterAdapter<VH extends AbstractFilterViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected final PixelSortingContext psContext;
    protected final List<Filter> filters;

    public AbstractFilterAdapter(PixelSortingContext psContext, List<Filter> filters) {
        this.psContext = psContext;
        this.filters = filters;
    }


    @Override
    public int getItemCount() {
        return filters.size();
    }


    @Override
    public void onViewRecycled(VH holder) {
        if (holder.getThumbTask() != null) {
            holder.getThumbTask().cancel(true);
        }

        if (holder.thumbnailView.getDrawable() instanceof BitmapDrawable) {
            //recycle old bitmap and clear the ImageView
            Bitmap bitmap =
                    ((BitmapDrawable) holder.thumbnailView.getDrawable()).getBitmap();
            holder.thumbnailView.setImageResource(R.color.primary_dark);

            bitmap.recycle();
        }

        super.onViewRecycled(holder);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        Filter filter = filters.get(position);

        holder.titleView.setText(filter.name);

        if (holder.thumbnailView.getWidth() > 0 && holder.thumbnailView.getHeight() > 0) {
            loadImg(holder, filter);
        } else {
            // thumbView hasn't been measured
            loadPreviewOnMeasure(holder, filter);
        }
    }


    // Defers loading a Filter thumbnail until holder's views are guaranteed to be measured
    private void loadPreviewOnMeasure(final AbstractFilterViewHolder holder, final Filter filter) {
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


    private void loadImg(AbstractFilterViewHolder holder, Filter filter) {
        holder.setThumbTask(
                psContext.getPixelSortedImage(
                        filter, holder.thumbnailView.getWidth(),
                        holder.thumbnailView.getHeight(), holder)
        );
    }
}

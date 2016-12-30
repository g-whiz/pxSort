package io.pxsort.pxsort.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.pxsort.pxsort.R;

/**
 * Created by George on 2016-07-18.
 */
public class OptionAdapter extends
        RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {

    private final List<Option> options;

    public OptionAdapter(List<Option> options) {
        this.options = options;
    }


    @Override
    public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(OptionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    /**
     * A mapping of an integer constant to a name.
     */
    public class Option {
        /**
         * The name of this Parameter's value.
         */
        public final CharSequence name;

        /**
         * This Parameter's value.
         */
        public final int value;

        public Option(CharSequence name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    // TODO: 2016-07-27 CMY colours for the option text?

    protected static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;

        public OptionViewHolder(View itemView) {
            super(itemView);
            this.nameView = (TextView) itemView.findViewById(R.id.option_name);
        }
    }
}

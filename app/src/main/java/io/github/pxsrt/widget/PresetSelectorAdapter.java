package io.github.pxsrt.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import io.github.pxsrt.presets.Preset;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by George on 2015-09-03.
 */
public class PresetSelectorAdapter implements Adapter {

    private Set<DataSetObserver> registeredObservers;
    private List<Preset> presets;

    public PresetSelectorAdapter(List<Preset> presets, int viewResId) {
        registeredObservers = new TreeSet<>();
        this.presets = presets;

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (!registeredObservers.contains(observer)) {
            registeredObservers.add(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (registeredObservers.contains(observer)) {
            registeredObservers.remove(observer);
        }
    }

    /**
     * Notifies registered {@link DataSetObserver DataSetObservers} of a change in
     * this Adapter's underlying data set.
     */
    public void notifyDataSetChanged() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

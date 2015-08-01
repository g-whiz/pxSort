package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.sort.presets.Preset;

/**
 * Created by George on 2015-07-20.
 */
public class PresetFragment extends Fragment{

    private String name;
    private PixelSorter sorter;
    private int drawableResId;

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSorter(PixelSorter sorter) {
        this.sorter = sorter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return null;
    }

    /**Interface for passing preset to sorter/pickerfragment*/
    public interface OnPresetPickedListener {

        public void onPresetPicket(Preset preset);
    }
}

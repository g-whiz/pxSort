package io.github.pxsrt.presets;

import io.github.pxsrt.sort.PixelSorter;

/**
 * Created by George on 2015-07-24.
 */
public class Preset {
    private String name;
    private PixelSorter sorter;
    private boolean isDefault;

    public Preset(String name, PixelSorter sorter, boolean isDefault) {
        this.name = name;
        this.sorter = sorter;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public PixelSorter getSorter() {
        return sorter;
    }

    public void setSorter(PixelSorter sorter) {
        this.sorter = sorter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Preset
                && name.equals(((Preset) o).getName())
                && sorter.equals(((Preset) o).getSorter())
                && isDefault == ((Preset) o).isDefault;
    }

    public interface PresetObserver {
        /**
         * Called when the preset being observed has changed.
         */
        void presetChanged(Preset preset);
    }

}

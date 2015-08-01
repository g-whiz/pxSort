package com.github.pxsrt.sort.presets;

import com.github.pxsrt.sort.PixelSorter;

/**
 * Created by George on 2015-07-24.
 */
public class Preset {
    private String name;
    private String thumbnailFileName;
    private PixelSorter sorter;
    private boolean isDefault;

    public Preset(String name, String thumbnailFileName,
                  PixelSorter sorter, boolean isDefault) {
        this.name = name;
        this.sorter = sorter;
        this.thumbnailFileName = thumbnailFileName;
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

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public void setThumbnailFileName(String thumbnailFileName) {
        this.thumbnailFileName = thumbnailFileName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}

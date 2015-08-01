package com.github.pxsrt.sort.presets;

import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.predicate.RangePredicate;
import com.github.pxsrt.sort.predicate.ThresholdPredicate;

/**
 * Created by George on 2015-07-27.
 */
public class PresetConstants {

    public static final String USER_PRESETS = "user-presets.pxsrt";
    public static final String DEFAULT_PRESETS = "default-presets.pxsrt";

    public static final String NAME = "name";
    public static final String THUMBNAIL_FILENAME = "thumb";
    public static final String IS_DEFAULT_PRESET = "is-default";

    public static final String SORTER_TYPE = "sorter-type";
    public static final String ROW_PIXEL_SORTER = RowPixelSorter.class.getSimpleName();

    public static final String SORTER = "sorter";
    public static final String COMPARATOR = "comparator";
    public static final String FROM_PREDICATE = "predicate-from";
    public static final String TO_PREDICATE = "predicate-to";
    public static final String DIRECTION = "direction";

    public static final String COMPONENT = "component";

    public static final String ORDERING = "order";

    public static final String PREDICATE_TYPE = "predicate-type";
    public static final String THRESHOLD = "threshold";
    public static final String UPPER_BOUND = "upper-bound";
    public static final String LOWER_BOUND = "lower-bound";

}

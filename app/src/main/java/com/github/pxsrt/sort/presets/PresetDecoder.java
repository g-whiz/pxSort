package com.github.pxsrt.sort.presets;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.comparator.PixelComparator;
import com.github.pxsrt.sort.predicate.PixelPredicate;

import org.json.JSONException;
import org.json.JSONObject;
import static com.github.pxsrt.sort.presets.PresetConstants.*;

/**
 * Created by George on 2015-07-26.
 */
public class PresetDecoder {

    public static Preset decode(JSONObject encodedPreset) throws JSONException {

        String name = encodedPreset.getString(NAME);
        String thumbnailFilename = encodedPreset.getString(THUMBNAIL_FILENAME);
        PixelSorter sorter = decodeSorter(encodedPreset);
        boolean isDefault = encodedPreset.getBoolean(IS_DEFAULT_PRESET);

        return new Preset(name, thumbnailFilename, sorter, isDefault);
    }

    private static PixelSorter decodeSorter(JSONObject encodedSorter) throws JSONException {
        String sorterType = encodedSorter.getString(SORTER_TYPE);

        if (sorterType.equals(ROW_PIXEL_SORTER)) {
            PixelComparator comparator = decodeComparator(encodedSorter
                    .getJSONObject(COMPARATOR));
            PixelPredicate fromPredicate = decodePredicate(encodedSorter
                    .getJSONObject(FROM_PREDICATE));
            PixelPredicate toPredicate = decodePredicate(encodedSorter
                    .getJSONObject(TO_PREDICATE));
            int direction = encodedSorter.getInt(DIRECTION);

            return new RowPixelSorter(comparator, fromPredicate, toPredicate, direction);
        } else {
            return null;
        }
    }

    private static PixelComparator decodeComparator(JSONObject encodedComparator) throws JSONException {
        String component = encodedComparator.getString(COMPONENT);
        String ordering = encodedComparator.getString(ORDERING);
        return PixelComparator.createNew(component, ordering);
    }

    private static PixelPredicate decodePredicate(JSONObject encodedPredicate)
            throws JSONException {
        String type = encodedPredicate.getString(PREDICATE_TYPE);
        String component = encodedPredicate.getString(COMPONENT);

        if (type.equals(PixelPredicate.ABOVE_THRESHOLD)
                || type.equals(PixelPredicate.BELOW_THRESHOLD)) {
            Number threshold = encodedPredicate.getDouble(THRESHOLD);
            return PixelPredicate.createPredicate(type, component, threshold, null);

        } else if (type.equals(PixelPredicate.WITHIN_RANGE)) {
            Number upperBound = encodedPredicate.getDouble(UPPER_BOUND);
            Number lowerBound = encodedPredicate.getDouble(LOWER_BOUND);
            return PixelPredicate.createPredicate(type, component, lowerBound, upperBound);

        } else {
            return null;
        }
    }
}

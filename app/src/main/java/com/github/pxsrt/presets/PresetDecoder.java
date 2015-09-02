package com.github.pxsrt.presets;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.comparator.ComponentComparator;
import com.github.pxsrt.sort.predicate.ComponentPredicate;

import org.json.JSONException;
import org.json.JSONObject;
import static com.github.pxsrt.presets.PresetConstants.*;

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
            ComponentComparator comparator = decodeComparator(encodedSorter
                    .getJSONObject(COMPARATOR));
            ComponentPredicate fromPredicate = decodeComponentPredicate(encodedSorter
                    .getJSONObject(FROM_PREDICATE));
            ComponentPredicate toPredicate = decodeComponentPredicate(encodedSorter
                    .getJSONObject(TO_PREDICATE));
            int direction = encodedSorter.getInt(DIRECTION);

            return new RowPixelSorter(comparator, fromPredicate, toPredicate, direction);
        } else {
            return null;
        }
    }

    private static ComponentComparator decodeComparator(JSONObject encodedComparator) throws JSONException {
        String component = encodedComparator.getString(COMPONENT);
        String ordering = encodedComparator.getString(ORDERING);
        return new ComponentComparator(component, ordering) {
        };
    }

    private static ComponentPredicate decodeComponentPredicate(JSONObject encodedPredicate)
            throws JSONException {
        String component = encodedPredicate.getString(COMPONENT);
        Double lowerBound = encodedPredicate.getDouble(LOWER_BOUND);
        Double upperBound = encodedPredicate.getDouble(UPPER_BOUND);

        return new ComponentPredicate(component, lowerBound, upperBound);
    }
}

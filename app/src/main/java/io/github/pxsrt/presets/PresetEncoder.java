package io.github.pxsrt.presets;

import android.support.annotation.NonNull;
import android.util.Log;

import io.github.pxsrt.sort.PixelSorter;
import io.github.pxsrt.sort.RowPixelSorter;
import io.github.pxsrt.sort.comparator.ComponentComparator;
import io.github.pxsrt.sort.predicate.ComponentPredicate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by George on 2015-07-24.
 */
public class PresetEncoder {

    public static JSONObject encode(Preset preset) throws JSONException {
        JSONObject encodedPreset = new JSONObject();
        encodedPreset.put(PresetConstants.NAME, preset.getName());
        encodedPreset.put(PresetConstants.IS_DEFAULT_PRESET, preset.isDefault());
        encodedPreset.put(PresetConstants.SORTER, encodeSorter(preset.getSorter()));
        return encodedPreset;
    }

    private static JSONObject encodeSorter(PixelSorter sorter) throws JSONException {
        JSONObject encodedSorter = new JSONObject();

        if (sorter instanceof RowPixelSorter) {
            RowPixelSorter rowSorter = (RowPixelSorter) sorter;
            encodedSorter.put(PresetConstants.SORTER_TYPE, PresetConstants.ROW_PIXEL_SORTER);
            encodedSorter.put(PresetConstants.COMPARATOR, encodeComparator(
                    rowSorter.getComparator()));

            encodedSorter.put(PresetConstants.FROM_PREDICATE, encodePredicate(rowSorter.getFromPredicate()));

            encodedSorter.put(PresetConstants.TO_PREDICATE, encodePredicate(rowSorter.getToPredicate()));

            encodedSorter.put(PresetConstants.DIRECTION, rowSorter.getDirection());
        } else {
            return null;
        }

        return encodedSorter;
    }

    private static JSONObject encodeComparator(ComponentComparator comparator) throws JSONException {
        JSONObject encodedComparator = new JSONObject();

        encodedComparator.put(PresetConstants.COMPONENT, comparator.getComponent());
        encodedComparator.put(PresetConstants.ORDERING, comparator.getOrdering());

        return encodedComparator;
    }

    private static JSONObject encodePredicate(ComponentPredicate predicate) throws JSONException {
        JSONObject encodedPredicate = new JSONObject();

        encodedPredicate.put(PresetConstants.COMPONENT, predicate.getComponent());
        encodedPredicate.put(PresetConstants.LOWER_BOUND, predicate.getLowerBound());
        encodedPredicate.put(PresetConstants.UPPER_BOUND, predicate.getUpperBound());

        return encodedPredicate;
    }
}

package com.github.pxsrt.presets;

import android.util.Log;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.comparator.ComponentComparator;
import com.github.pxsrt.sort.predicate.ComponentPredicate;

import static com.github.pxsrt.presets.PresetConstants.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by George on 2015-07-24.
 */
public class PresetEncoder {
    public static final String TAG = PresetEncoder.class.getSimpleName();

    public static JSONObject encode(Preset preset) {
        JSONObject encodedPreset = new JSONObject();
        try {
            encodedPreset.put(NAME, preset.getName());
            encodedPreset.put(THUMBNAIL_FILENAME, preset.getThumbnailFileName());
            encodedPreset.put(IS_DEFAULT_PRESET, preset.isDefault());
            encodedPreset.put(SORTER, encodeSorter(preset.getSorter()));
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset.", e);
        }
        return encodedPreset;
    }

    private static JSONObject encodeSorter(PixelSorter sorter) {
        JSONObject encodedSorter = new JSONObject();

        try {
            if (sorter instanceof RowPixelSorter) {
                RowPixelSorter rowSorter = (RowPixelSorter) sorter;
                encodedSorter.put(SORTER_TYPE, ROW_PIXEL_SORTER);
                encodedSorter.put(COMPARATOR, encodeComparator(
                        (ComponentComparator) rowSorter.getComparator()));

                encodedSorter.put(FROM_PREDICATE, encodePredicate(rowSorter.getFromPredicate()));

                encodedSorter.put(TO_PREDICATE, encodePredicate(rowSorter.getToPredicate()));

                encodedSorter.put(DIRECTION, rowSorter.getDirection());
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset's sorter.", e);
        }

        return encodedSorter;
    }

    private static JSONObject encodeComparator(ComponentComparator comparator) {
        JSONObject encodedComparator = new JSONObject();

        try {
            encodedComparator.put(COMPONENT, comparator.getComponent());
            encodedComparator.put(ORDERING, comparator.getOrdering());
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset's comparator.", e);
            return null;
        }

        return encodedComparator;
    }

    private static JSONObject encodePredicate(ComponentPredicate predicate) {
        JSONObject encodedPredicate = new JSONObject();

        try {
            encodedPredicate.put(COMPONENT, predicate.getComponent());
            encodedPredicate.put(LOWER_BOUND, predicate.getLowerBound());
            encodedPredicate.put(UPPER_BOUND, predicate.getUpperBound());
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset's predicate.", e);
            return null;
        }

        return encodedPredicate;
    }
}

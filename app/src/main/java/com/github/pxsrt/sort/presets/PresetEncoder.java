package com.github.pxsrt.sort.presets;

import android.util.Log;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.comparator.PixelComparator;
import com.github.pxsrt.sort.predicate.PixelPredicate;
import com.github.pxsrt.sort.predicate.RangePredicate;
import com.github.pxsrt.sort.predicate.ThresholdPredicate;

import static com.github.pxsrt.sort.presets.PresetConstants.*;

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
                        (PixelComparator) rowSorter.getComparator()));

                encodedSorter.put(FROM_PREDICATE, encodePredicate(
                        (PixelPredicate) rowSorter.getFromPredicate()));

                encodedSorter.put(TO_PREDICATE, encodePredicate(
                        (PixelPredicate) rowSorter.getToPredicate()));

                encodedSorter.put(DIRECTION, rowSorter.getDirection());
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset's sorter.", e);
        }

        return encodedSorter;
    }

    private static JSONObject encodeComparator(PixelComparator comparator) {
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

    private static JSONObject encodePredicate(PixelPredicate predicate) {
        JSONObject encodedPredicate = new JSONObject();

        try {
            encodedPredicate.put(PREDICATE_TYPE, predicate.getType());
            encodedPredicate.put(COMPONENT, predicate.getComponent());

            if (predicate instanceof ThresholdPredicate) {
                encodedPredicate.put(THRESHOLD,
                        ((ThresholdPredicate) predicate).getThreshold().doubleValue());
            } else if (predicate instanceof RangePredicate) {
                encodedPredicate.put(UPPER_BOUND,
                        ((RangePredicate) predicate).getUpperBound().doubleValue());
                encodedPredicate.put(LOWER_BOUND,
                        ((RangePredicate) predicate).getLowerBound().doubleValue());
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "An error occurred while encoding a preset's predicate.", e);
            return null;
        }

        return encodedPredicate;
    }
}

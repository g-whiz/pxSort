package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.pxsrt.sort.predicate.PixelPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-06-30.
 */
public class PredicatePickerFragment extends Fragment{

    private static final List<String> PREDICATE_TYPES = new ArrayList<>();
    static {
        PREDICATE_TYPES.add(PixelPredicate.ABOVE_THRESHOLD);
        PREDICATE_TYPES.add(PixelPredicate.BELOW_THRESHOLD);
        PREDICATE_TYPES.add(PixelPredicate.WITHIN_RANGE);
    }

    private static final List<String> COMPONENT_TYPES = new ArrayList<>();
    static {
        COMPONENT_TYPES.add(PixelPredicate.RED);
        COMPONENT_TYPES.add(PixelPredicate.GREEN);
        COMPONENT_TYPES.add(PixelPredicate.BLUE);
        COMPONENT_TYPES.add(PixelPredicate.HUE);
        COMPONENT_TYPES.add(PixelPredicate.SATURATION);
        COMPONENT_TYPES.add(PixelPredicate.VALUE);
    }

    private static final int HUE_SELECTOR_MIN = 0;
    private static final int HUE_SELECTOR_MAX = 360;
    private static final String HUE_SELECTOR_SUFFIX = " /360";

    private static final int VALUE_SELECTOR_MIN = 0;
    private static final int VALUE_SELECTOR_MAX = 100;
    private static final String VALUE_SELECTOR_SUFFIX = "%";

    private static final int RGB_SELECTOR_MIN = 0;
    private static final int RGB_SELECTOR_MAX = 255;
    private static final String RGB_SELECTOR_SUFFIX = "";

    private String predicateType;
    private String componentType;

    private Spinner.OnItemSelectedListener predicateTypeListener;
    private Spinner.OnItemSelectedListener componentTypeListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_predicate_picker, container, false);
        Spinner componentSpinner = (Spinner) view.findViewById(R.id.spinner_component);
        Spinner predicateTypeSpinner = (Spinner) view.findViewById(R.id.spinner_predicate_type);
        initializeListeners();

        ArrayAdapter<String> componentAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, COMPONENT_TYPES);
        componentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        componentSpinner.setAdapter(componentAdapter);
        componentSpinner.setOnItemSelectedListener(componentTypeListener);
        componentSpinner.setSelection(0);

        ArrayAdapter<String> predicateTypeAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, PREDICATE_TYPES);
        predicateTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        predicateTypeSpinner.setAdapter(predicateTypeAdapter);
        predicateTypeSpinner.setOnItemSelectedListener(predicateTypeListener);
        predicateTypeSpinner.setSelection(0);

        return view;
    }

    private void initializeListeners() {
        predicateTypeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                predicateType = (String) parent.getItemAtPosition(position);
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        componentTypeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                componentType = (String) parent.getItemAtPosition(position);
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private NumberPicker getPrimaryPicker() {
        if (getView() != null) {
            return (NumberPicker) getView().findViewById(R.id.primary_picker);
        }
        return null;
    }

    private NumberPicker getSecondaryPicker() {
        if (getView() != null) {
            return (NumberPicker) getView().findViewById(R.id.secondary_picker);
        }
        return null;
    }

    private Number getPrimaryPickerValue(){
        if (getPrimaryPicker() != null) {
            if (getSelectedComponent().equals(PixelPredicate.VALUE)
                    || getSelectedComponent().equals(PixelPredicate.SATURATION)) {
                return ((double) getPrimaryPicker().getValue()) / 100.0;
            } else {
                return getPrimaryPicker().getValue();
            }
        }
        return null;
    }

    private Number getSecondaryPickerValue(){
        if (getSecondaryPicker() != null) {
            if (getSelectedComponent().equals(PixelPredicate.VALUE)
                    || getSelectedComponent().equals(PixelPredicate.SATURATION)) {
                return ((double) getSecondaryPicker().getValue()) / 100.0;
            } else {
                return getSecondaryPicker().getValue();
            }
        }
        return null;
    }

    private void showSecondaryPicker(boolean show) {
        if (getSecondaryPicker() != null) {
            if (show) {
                getSecondaryPicker().setVisibility(View.VISIBLE);
            } else {
                getSecondaryPicker().setVisibility(View.GONE);
            }
        }
    }

    private String getSelectedPredicateType() {
        return predicateType;
    }

    private String getSelectedComponent() {
        return componentType;
    }

    private void updateView() {
        if (getSelectedPredicateType().equals(PixelPredicate.WITHIN_RANGE)) {
            showSecondaryPicker(true);
            setPrimaryPickerTitle(R.string.lower_bound);
        } else {
            showSecondaryPicker(false);
            setPrimaryPickerTitle(R.string.threshold);
        }

        updatePickerFormat();
    }

    private void setPrimaryPickerTitle(int textId) {
        if (getView() != null) {
            TextView title = (TextView) getView().findViewById(R.id.primary_picker_title);
            title.setText(textId);
        }
    }

    private void setPickerRange(int min, int max) {
        if (getPrimaryPicker() != null && getSecondaryPicker() != null) {
            getPrimaryPicker().setMinValue(min);
            getPrimaryPicker().setMaxValue(max);

            getSecondaryPicker().setMinValue(min);
            getSecondaryPicker().setMaxValue(max);

            getPrimaryPicker().setValue(min);
            getPrimaryPicker().setValue(max);
        }
    }

    private void updatePickerFormat() {
        if (getSelectedComponent() != null
                && getPrimaryPicker() != null
                && getSecondaryPicker() != null) {

            NumberPicker.Formatter formatter = null;

            if (getSelectedComponent().equals(PixelPredicate.HUE)) {
                setPickerRange(HUE_SELECTOR_MIN, HUE_SELECTOR_MAX);
                formatter = new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return value + HUE_SELECTOR_SUFFIX;
                    }
                };

            } else if (getSelectedComponent().equals(PixelPredicate.SATURATION)
                    || getSelectedComponent().equals(PixelPredicate.VALUE)) {
                setPickerRange(VALUE_SELECTOR_MIN, VALUE_SELECTOR_MAX);
                formatter = new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return value + VALUE_SELECTOR_SUFFIX;
                    }
                };

            } else if (getSelectedComponent().equals(PixelPredicate.RED)
                    || getSelectedComponent().equals(PixelPredicate.GREEN)
                    || getSelectedComponent().equals(PixelPredicate.BLUE)) {
                setPickerRange(RGB_SELECTOR_MIN, RGB_SELECTOR_MAX);
                formatter = new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return value + RGB_SELECTOR_SUFFIX;
                    }
                };
            }

            getPrimaryPicker().setFormatter(formatter);
            getSecondaryPicker().setFormatter(formatter);
        }
    }

    public PixelPredicate getPredicate() {
        return PixelPredicate.createPredicate(predicateType, componentType,
                getPrimaryPickerValue(), getSecondaryPickerValue());
    }
}

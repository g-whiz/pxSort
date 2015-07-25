package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.github.pxsrt.R;
import com.github.pxsrt.sort.comparator.PixelComparator;
import com.github.pxsrt.sort.comparator.PixelComparatorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-07-12.
 */
public class ComparatorPickerFragment extends Fragment {

    private String selectedComponent;

    private Spinner.OnItemSelectedListener componentListener;

    private static final List<String> COMPONENT_TYPES = new ArrayList<>();
    static {
        COMPONENT_TYPES.add(PixelComparatorFactory.RED);
        COMPONENT_TYPES.add(PixelComparatorFactory.GREEN);
        COMPONENT_TYPES.add(PixelComparatorFactory.BLUE);
        COMPONENT_TYPES.add(PixelComparatorFactory.HUE);
        COMPONENT_TYPES.add(PixelComparatorFactory.SATURATION);
        COMPONENT_TYPES.add(PixelComparatorFactory.VALUE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_comparator_picker, container, false);
        Spinner componentSpinner  = (Spinner) view.findViewById(R.id.comparator_spinner);
        initializeListeners();

        ArrayAdapter<String> componentAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, COMPONENT_TYPES);
        componentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        componentSpinner.setAdapter(componentAdapter);
        componentSpinner.setOnItemSelectedListener(componentListener);
        componentSpinner.setSelection(0);

        return view;
    }

    private void initializeListeners() {
        componentListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedComponent = (String) parent.getItemAtPosition(position);
                //TODO updateView() ?
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private String getSelectedComponent() {
        return selectedComponent;
    }

    private RadioGroup getOrderRadioGroup() {
        if (getView() != null) {
            return (RadioGroup) getView().findViewById(R.id.radio_group_comparator_order);
        }
        return null;
    }

    private String getOrder() {
        RadioGroup orderGroup = getOrderRadioGroup();

        if (orderGroup != null) {
            if (orderGroup.getCheckedRadioButtonId() == R.id.ascending_order) {
                return PixelComparatorFactory.ASCENDING_ORDER;
            } else {
                return PixelComparatorFactory.DESCENDING_ORDER;
            }
        }

        return null;
    }

    public PixelComparator getComparator() {
        return PixelComparatorFactory.createNew(getSelectedComponent(), getOrder());
    }
}

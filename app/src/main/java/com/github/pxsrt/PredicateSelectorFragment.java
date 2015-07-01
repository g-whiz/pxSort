package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.pxsrt.sort.predicate.PixelPredicateFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-06-30.
 */
public class PredicateSelectorFragment extends Fragment{

    private static final List<String> PREDICATE_TYPES = new ArrayList<>();
    static {
        PREDICATE_TYPES.add(PixelPredicateFactory.ABOVE_THRESHOLD);
        PREDICATE_TYPES.add(PixelPredicateFactory.BELOW_THRESHOLD);
        PREDICATE_TYPES.add(PixelPredicateFactory.WITHIN_RANGE);
    }

    private static final List<String> COMPONENT_TYPES = new ArrayList<>();
    static {
        COMPONENT_TYPES.add(PixelPredicateFactory.RED);
        COMPONENT_TYPES.add(PixelPredicateFactory.GREEN);
        COMPONENT_TYPES.add(PixelPredicateFactory.BLUE);
        COMPONENT_TYPES.add(PixelPredicateFactory.HUE);
        COMPONENT_TYPES.add(PixelPredicateFactory.SATURATION);
        COMPONENT_TYPES.add(PixelPredicateFactory.VALUE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_predicate_selector, container, false);
        Spinner componentSpinner = (Spinner) view.findViewById(R.id.spinner_component);
        Spinner predicateTypeSpinner = (Spinner) view.findViewById(R.id.spinner_predicate_type);

        ArrayAdapter<String> componentAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, COMPONENT_TYPES);
        componentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        componentSpinner.setAdapter(componentAdapter);

        ArrayAdapter<String> predicateTypeAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, PREDICATE_TYPES);
        predicateTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        predicateTypeSpinner.setAdapter(predicateTypeAdapter);

        return view;
    }

    private void showSecondaryTextView(boolean show) {
        if(show) {
            getView().findViewById(R.id.secondary_text_view_container).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.secondary_text_view_container).setVisibility(View.GONE);
        }
    }

    //TODO Dialogues for selecting threshold/range
}

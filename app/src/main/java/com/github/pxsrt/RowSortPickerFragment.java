package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.github.pxsrt.ComparatorPickerFragment;
import com.github.pxsrt.PredicatePickerFragment;
import com.github.pxsrt.R;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.comparator.PixelComparator;
import com.github.pxsrt.sort.predicate.PixelPredicate;

/**
 * Created by George on 2015-07-14.
 */
public class RowSortPickerFragment extends Fragment {


    private PixelComparator getComparator() {
        ComparatorPickerFragment fragment  = (ComparatorPickerFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_comparator_picker);
        return fragment.getComparator();
    }

    private int getDirection() {
        if (getView() != null) {
            int directionId = ((RadioGroup) getView().findViewById(R.id.radio_group_direction))
                    .getCheckedRadioButtonId();

            if (directionId  == R.id.sort_by_row) {
                return RowPixelSorter.SORT_BY_ROW;

            } else {
                return RowPixelSorter.SORT_BY_COLUMN;
            }
        }

        return -1;
    }

    private PixelPredicate getFromPredicate() {
        PredicatePickerFragment fragment = (PredicatePickerFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_from_predicate_picker);
        return fragment.getPredicate();
    }

    private PixelPredicate getToPredicate() {
        PredicatePickerFragment fragment = (PredicatePickerFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_to_predicate_picker);
        return fragment.getPredicate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_row_sort_picker, container, false);

        return view;
    }

    public RowPixelSorter getSorter() {
        return new RowPixelSorter(getComparator(), getFromPredicate(),
                getToPredicate(), getDirection());
    }
}

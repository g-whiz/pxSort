package com.github.pxsrt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.util.Predicate;
import com.github.pxsrt.sort.RowPixelSorter;
import com.github.pxsrt.sort.Pixel;
import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.ui.HelpButton;

import java.util.Comparator;

/**
 * Created by George on 2015-06-04.
 */
public class SortConfigFragment extends Fragment {
    public static final String TAG = SortConfigFragment.class.getSimpleName();

    //TODO Add functionality for loading presets.
    //TODO Add functionality for randomizing.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View sortSettingsView = inflater.inflate(R.layout.layout_fragment_sort_settings,
                container, false);

        return sortSettingsView;
    }

    public PixelSorter getSort(){
        return createTestSort();
    }

    private PixelSorter createTestSort(){

        Comparator<Pixel> comparator = new Comparator<Pixel>() {
            @Override
            public int compare(Pixel px1, Pixel px2) {
                if (px1.red() > px2.red()){
                    return 1;
                } else if (px1.red() == px2.red()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        Predicate<Pixel> fromPredicate = new Predicate<Pixel>() {
            @Override
            public boolean apply(Pixel px) {
                return px.red() < px.blue();
            }
        };

        Predicate<Pixel> toPredicate = new Predicate<Pixel>() {
            @Override
            public boolean apply(Pixel px) {
                return px.red() > px.blue();
            }
        };

        return new RowPixelSorter(comparator, fromPredicate, toPredicate, RowPixelSorter.SORT_BY_ROW);
    }

    public void showHelp(View view) {
        String messageTitle = ((HelpButton) view).getMessageTitle();
        String messageBody = ((HelpButton) view).getMessageBody();


    }

    //TODO Dynamically add settings panels.
    //TODO Implement pop-up help dialog.
    //TODO
}

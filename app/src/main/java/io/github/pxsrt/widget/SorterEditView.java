package io.github.pxsrt.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import io.github.pxsrt.R;
import io.github.pxsrt.sort.RowPixelSorter;
import io.github.pxsrt.sort.comparator.ComponentComparator;
import io.github.pxsrt.sort.predicate.ComponentPredicate;

/**
 * Created by George on 2015-09-01.
 */
public class SorterEditView extends LinearLayout {

    private RadioGroup directionGroup;
    private ComparatorEditView comparatorEditView;
    private PredicateEditView fromPredicateEditView;
    private PredicateEditView toPredicateEditView;

    public SorterEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);

        LayoutInflater.from(context).inflate(R.layout.view_sorter_edit, this, true);

        directionGroup = (RadioGroup) findViewById(R.id.radio_group_direction);
        comparatorEditView = (ComparatorEditView) findViewById(R.id.comparator_edit_view);
        fromPredicateEditView = (PredicateEditView) findViewById(R.id.predicate_edit_view_from);
        toPredicateEditView = (PredicateEditView) findViewById(R.id.predicate_edit_view_to);
    }

    public SorterEditView(Context context) {
        this(context, null);
    }

    private int getDirection() {
        switch (directionGroup.getCheckedRadioButtonId()) {
            case R.id.vertical:
                return RowPixelSorter.VERTICAL;
            case R.id.horizontal:
                return RowPixelSorter.HORIZONTAL;
        }
        return 0;
    }

    private void setDirection(int direction) {
        switch (direction) {
            case RowPixelSorter.VERTICAL:
                ((RadioButton) findViewById(R.id.vertical)).setChecked(true);
                return;

            case RowPixelSorter.HORIZONTAL:
                    ((RadioButton) findViewById(R.id.horizontal)).setChecked(true);
        }
    }

    public RowPixelSorter getSorter() {
        ComponentComparator comparator = comparatorEditView.getComparator();
        ComponentPredicate fromPredicate = fromPredicateEditView.getPredicate();
        ComponentPredicate toPredicate = toPredicateEditView.getPredicate();

        return new RowPixelSorter(comparator, fromPredicate, toPredicate, getDirection());
    }

    public void setSorter(RowPixelSorter sorter) {
        if (sorter != null) {
            comparatorEditView.setComparator(sorter.getComparator());
            fromPredicateEditView.setPredicate(sorter.getFromPredicate());
            toPredicateEditView.setPredicate(sorter.getToPredicate());
            setDirection(sorter.getDirection());
        } else {
            comparatorEditView.setComparator(null);
            fromPredicateEditView.setPredicate(null);
            toPredicateEditView.setPredicate(null);
            setDirection(VERTICAL);
        }
    }
}

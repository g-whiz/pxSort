package com.github.pxsrt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.pxsrt.R;
import com.github.pxsrt.sort.comparator.ComponentComparator;

/**
 * Created by George on 2015-08-28.
 */
public class ComparatorEditView extends LinearLayout {

    public ComparatorEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.TOP);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_comparator_edit, this, true);
    }

    public ComparatorEditView(Context context) {
        this(context, null);
    }

    public ComponentComparator getComparator() {
        return new ComponentComparator(getComponentSpinner().getSelectedComponent(), getOrder());
    }

    public void setComparator(ComponentComparator comparator) {
        getComponentSpinner().setSelectedComponent(comparator.getComponent());
        setOrder(comparator.getOrdering());
    }

    private ComponentSpinner getComponentSpinner() {
        return (ComponentSpinner) findViewById(R.id.component_spinner);
    }

    private String getOrder() {
        RadioGroup orderGroup = (RadioGroup) findViewById(R.id.radio_group_comparator_order);

        if (orderGroup.getCheckedRadioButtonId() == R.id.ascending_order) {
            return ComponentComparator.ASCENDING_ORDER;
        } else {
            return ComponentComparator.DESCENDING_ORDER;
        }
    }

    private void setOrder(String order) {
        RadioButton ascendingOrder = (RadioButton) findViewById(R.id.ascending_order);
        RadioButton descendingOrder = (RadioButton) findViewById(R.id.descending_order);

        ascendingOrder.setChecked(order.equals(ComponentComparator.ASCENDING_ORDER));
        descendingOrder.setChecked(order.equals(ComponentComparator.DESCENDING_ORDER));
    }
}

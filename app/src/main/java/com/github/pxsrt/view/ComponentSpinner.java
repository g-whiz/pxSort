package com.github.pxsrt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.pxsrt.PixelComponents;

import java.util.Arrays;

/**
 * Created by George on 2015-08-28.
 */
public class ComponentSpinner extends Spinner {

    private static final String[] components = PixelComponents.getComponents();

    public ComponentSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, components);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
        setSelection(0);
    }

    public ComponentSpinner(Context context) {
        this(context, null);
    }

    public String getSelectedComponent() {
        return (String) getSelectedItem();
    }

    public void setSelectedComponent(String component) {
        int index = Arrays.binarySearch(components, component);
        if (index >= 0) {
            setSelection(index);
        }
    }
}

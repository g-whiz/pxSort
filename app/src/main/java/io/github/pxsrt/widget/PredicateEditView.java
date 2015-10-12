package io.github.pxsrt.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import io.github.pxsrt.sort.predicate.ComponentPredicate;

import java.util.HashMap;
import java.util.Map;

import io.github.pxsrt.PixelComponents;

/**
 * Created by George on 2015-08-28.
 */
public class PredicateEditView extends LinearLayout {

    private static final Map<String, Integer> componentUpperBoundMap = new HashMap<>();
        static {
            componentUpperBoundMap.put(PixelComponents.RED, 255);
            componentUpperBoundMap.put(PixelComponents.GREEN, 255);
            componentUpperBoundMap.put(PixelComponents.BLUE, 255);
            componentUpperBoundMap.put(PixelComponents.HUE, 360);
            componentUpperBoundMap.put(PixelComponents.SATURATION, 100);
            componentUpperBoundMap.put(PixelComponents.VALUE, 100);
        }
    
    SeekBar aboveSeekBar;
    SeekBar belowSeekBar;
    ComponentSpinner componentSpinner;
    
    public PredicateEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.TOP);

        LayoutInflater.from(context).inflate(io.github.pxsrt.R.layout.view_predicate_edit, this, true);

        if (attrs != null) {
            TextView titleTextView = (TextView) findViewById(io.github.pxsrt.R.id.title_text);
            TypedArray a = context.obtainStyledAttributes(attrs, io.github.pxsrt.R.styleable.PredicateEditView);
            titleTextView.setText(a.getString(0));
            a.recycle();
        }



        aboveSeekBar = (SeekBar) findViewById(io.github.pxsrt.R.id.seek_bar_above);
        belowSeekBar = (SeekBar) findViewById(io.github.pxsrt.R.id.seek_bar_below);
        
        aboveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView aboveText = (TextView) findViewById(io.github.pxsrt.R.id.text_view_above);
                Integer value = aboveSeekBar.getProgress();
                aboveText.setText(value.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        belowSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView belowText = (TextView) findViewById(io.github.pxsrt.R.id.text_view_below);
                Integer value = belowSeekBar.getProgress();
                belowText.setText(value.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        componentSpinner = (ComponentSpinner) findViewById(io.github.pxsrt.R.id.component_spinner);
        componentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSeekBarUpperBounds(componentUpperBoundMap.get
                        (componentSpinner.getSelectedComponent()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public PredicateEditView(Context context) {
        this(context, null);
    }
    
    private void setSeekBarUpperBounds(int upperBound) {
        if (upperBound > 0) {
            aboveSeekBar.setMax(upperBound);
            belowSeekBar.setMax(upperBound);
        }
    }

    private Number getUpperBound() {
        if (((CheckBox)findViewById(io.github.pxsrt.R.id.check_box_above)).isChecked()) {
            switch (componentSpinner.getSelectedComponent()) {
                case PixelComponents.RED:
                case PixelComponents.GREEN:
                case PixelComponents.BLUE:
                case PixelComponents.HUE:
                    return aboveSeekBar.getProgress();
                case PixelComponents.VALUE:
                case PixelComponents.SATURATION:
                    return ((Integer) aboveSeekBar.getProgress()).doubleValue() / 100.0D;
            }
        }

        return null;
    }

    private void setUpperBound(Number upperBound) {

        if (upperBound != null) {
            switch (componentSpinner.getSelectedComponent()) {
                case PixelComponents.RED:
                case PixelComponents.GREEN:
                case PixelComponents.BLUE:
                case PixelComponents.HUE:
                    aboveSeekBar.setProgress(upperBound.intValue());
                case PixelComponents.VALUE:
                case PixelComponents.SATURATION:
                    aboveSeekBar.setProgress(
                            ((Double) (upperBound.doubleValue() * 100.0D)).intValue());
            }
        } else {
            ((CheckBox)findViewById(io.github.pxsrt.R.id.check_box_above)).setChecked(false);
            aboveSeekBar.setProgress(0);
        }
    }

    private void setLowerBound(Number lowerBound) {

        if (lowerBound != null) {
            switch (componentSpinner.getSelectedComponent()) {
                case PixelComponents.RED:
                case PixelComponents.GREEN:
                case PixelComponents.BLUE:
                case PixelComponents.HUE:
                    belowSeekBar.setProgress(lowerBound.intValue());
                case PixelComponents.VALUE:
                case PixelComponents.SATURATION:
                    belowSeekBar.setProgress(((Double) (lowerBound.doubleValue() * 100.0D)).intValue());
            }
        } else {
            ((CheckBox)findViewById(io.github.pxsrt.R.id.check_box_below)).setChecked(false);
            belowSeekBar.setProgress(0);
        }
    }

    private Number getLowerBound() {
        if (((CheckBox)findViewById(io.github.pxsrt.R.id.check_box_below)).isChecked()) {
            switch (componentSpinner.getSelectedComponent()) {
                case PixelComponents.RED:
                case PixelComponents.GREEN:
                case PixelComponents.BLUE:
                case PixelComponents.HUE:
                    return belowSeekBar.getProgress();
                case PixelComponents.VALUE:
                case PixelComponents.SATURATION:
                    return ((Integer) belowSeekBar.getProgress()).doubleValue() / 100.0D;
            }
        }

        return null;
    }

    public ComponentPredicate getPredicate() {
        return new ComponentPredicate(componentSpinner.getSelectedComponent(),
                getLowerBound(), getUpperBound());
    }

    public void setPredicate(ComponentPredicate predicate) {
        if (predicate != null) {
            componentSpinner.setSelectedComponent(predicate.getComponent());
            setUpperBound(predicate.getUpperBound());
            setLowerBound(predicate.getLowerBound());
        } else {
            componentSpinner.setSelection(0);
            setUpperBound(componentUpperBoundMap.get(componentSpinner.getSelectedComponent()));
            setLowerBound(0);
        }
    }
}

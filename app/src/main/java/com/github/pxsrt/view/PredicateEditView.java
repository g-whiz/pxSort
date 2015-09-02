package com.github.pxsrt.view;

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

import com.github.pxsrt.R;
import com.github.pxsrt.sort.predicate.ComponentPredicate;

import static com.github.pxsrt.PixelComponents.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 2015-08-28.
 */
public class PredicateEditView extends LinearLayout {

    private static final Map<String, Integer> componentUpperBoundMap = new HashMap<>();
        static {
            componentUpperBoundMap.put(RED, 255);
            componentUpperBoundMap.put(GREEN, 255);
            componentUpperBoundMap.put(BLUE, 255);
            componentUpperBoundMap.put(HUE, 360);
            componentUpperBoundMap.put(SATURATION, 100);
            componentUpperBoundMap.put(VALUE, 100);
        }
    
    SeekBar aboveSeekBar;
    SeekBar belowSeekBar;
    ComponentSpinner componentSpinner;
    
    public PredicateEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.TOP);

        LayoutInflater.from(context).inflate(R.layout.view_predicate_edit, this, true);

        TextView titleTextView = (TextView) findViewById(R.id.title_text);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PredicateEditView);

        titleTextView.setText(a.getString(0));

        a.recycle();

        aboveSeekBar = (SeekBar) findViewById(R.id.seek_bar_above);
        belowSeekBar = (SeekBar) findViewById(R.id.seek_bar_below);
        
        aboveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView aboveText = (TextView) findViewById(R.id.text_view_above);
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
                TextView belowText = (TextView) findViewById(R.id.text_view_below);
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
        
        componentSpinner = (ComponentSpinner) findViewById(R.id.component_spinner);
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
        if (((CheckBox)findViewById(R.id.check_box_above)).isChecked()) {
            switch (componentSpinner.getSelectedComponent()) {
                case RED:
                case GREEN:
                case BLUE:
                case HUE:
                    return aboveSeekBar.getProgress();
                case VALUE:
                case SATURATION:
                    return ((Integer) aboveSeekBar.getProgress()).doubleValue() / 100.0D;
            }
        }

        return null;
    }

    private void setUpperBound(Number upperBound) {

        if (upperBound != null) {
            switch (componentSpinner.getSelectedComponent()) {
                case RED:
                case GREEN:
                case BLUE:
                case HUE:
                    aboveSeekBar.setProgress(upperBound.intValue());
                case VALUE:
                case SATURATION:
                    aboveSeekBar.setProgress(((Double) (upperBound.doubleValue() * 100.0D)).intValue());
            }
        } else {
            ((CheckBox)findViewById(R.id.check_box_above)).setChecked(false);
            aboveSeekBar.setProgress(0);
        }
    }

    private void setLowerBound(Number lowerBound) {

        if (lowerBound != null) {
            switch (componentSpinner.getSelectedComponent()) {
                case RED:
                case GREEN:
                case BLUE:
                case HUE:
                    belowSeekBar.setProgress(lowerBound.intValue());
                case VALUE:
                case SATURATION:
                    belowSeekBar.setProgress(((Double) (lowerBound.doubleValue() * 100.0D)).intValue());
            }
        } else {
            ((CheckBox)findViewById(R.id.check_box_below)).setChecked(false);
            belowSeekBar.setProgress(0);
        }
    }

    private Number getLowerBound() {
        if (((CheckBox)findViewById(R.id.check_box_below)).isChecked()) {
            switch (componentSpinner.getSelectedComponent()) {
                case RED:
                case GREEN:
                case BLUE:
                case HUE:
                    return belowSeekBar.getProgress();
                case VALUE:
                case SATURATION:
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
        componentSpinner.setSelectedComponent(predicate.getComponent());
        setUpperBound(predicate.getUpperBound());
        setLowerBound(predicate.getLowerBound());
    }
}

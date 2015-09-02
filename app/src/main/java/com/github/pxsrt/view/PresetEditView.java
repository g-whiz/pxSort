package com.github.pxsrt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.pxsrt.R;
import com.github.pxsrt.presets.Preset;
import com.github.pxsrt.sort.RowPixelSorter;

/**TODO Thumbnails
 * Created by George on 2015-09-01.
 */
public class PresetEditView extends LinearLayout {

    EditText presetNameEditText;
    SorterEditView sorterEditView;

    public PresetEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);

        LayoutInflater.from(context).inflate(R.layout.view_preset_edit, this, true);

        presetNameEditText = (EditText) findViewById(R.id.preset_name);
        sorterEditView = (SorterEditView) findViewById(R.id.sorter_edit_view);
    }

    public PresetEditView(Context context) {
        this(context, null);
    }

    public Preset getPreset() {
        return new Preset(getName(), null, sorterEditView.getSorter(), false);
    }

    public void setPreset(Preset preset) {
        setName(preset.getName());
        sorterEditView.setSorter((RowPixelSorter) preset.getSorter());
    }

    private String getName() {
        if (presetNameEditText.getText().length() > 0) {
            return presetNameEditText.getText().toString();
        }
        return null;
    }

    private void setName(String name) {
        if (name != null) {
            presetNameEditText.setText(name);
        } else {
            presetNameEditText.setText("");
        }
    }
}

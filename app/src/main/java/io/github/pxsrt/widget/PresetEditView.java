package io.github.pxsrt.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import io.github.pxsrt.R;
import io.github.pxsrt.presets.Preset;
import io.github.pxsrt.sort.RowPixelSorter;

/**TODO Thumbnails
 * Created by George on 2015-09-01.
 */
public class PresetEditView extends LinearLayout {

    private EditText presetNameEditText;
    private SorterEditView sorterEditView;

    public PresetEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);

        setBackgroundColor(0xFFFFFFFF);

        LayoutInflater.from(context).inflate(R.layout.view_preset_edit, this, true);

        presetNameEditText = (EditText) findViewById(R.id.preset_name);
        sorterEditView = (SorterEditView) findViewById(R.id.sorter_edit_view);
    }

    public PresetEditView(Context context) {
        this(context, null);
    }

    public Preset getPreset() {
        return new Preset(getName(), sorterEditView.getSorter(), false);
    }

    public void setPreset(Preset preset) {
        if (preset != null) {
            setName(preset.getName());
            sorterEditView.setSorter((RowPixelSorter) preset.getSorter());
        } else {
            setName("");
            sorterEditView.setSorter(null);
        }
    }

    private String getName() {
        if (presetNameEditText.getText().length() > 0) {
            return presetNameEditText.getText().toString();
        }
        return "";
    }

    private void setName(String name) {
        if (name != null) {
            presetNameEditText.setText(name);
        } else {
            presetNameEditText.setText("");
        }
    }
}

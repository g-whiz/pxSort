package io.github.pxsrt.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.github.pxsrt.R;
import io.github.pxsrt.presets.Preset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-09-03.
 */
public class PresetSelectorView extends LinearLayout implements View.OnClickListener {

    private static final int COLOR_SELECTED = 0x66DDDDDD;
    private static final int COLOR_UNSELECTED = 0x00000000;
    private Bitmap thumbnailBM;

    private View noPresetView;
    private View selectedView;

    private Listener listener;

    public PresetSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        thumbnailBM = null;
        selectedView = null;

        setOrientation(HORIZONTAL);
        setGravity(Gravity.START);

        LayoutInflater.from(context).inflate(R.layout.view_preset_selector, this, true);

        View newPresetView = findViewById(R.id.new_preset);
        newPresetView.setOnClickListener(this);

        noPresetView = findViewById(R.id.no_preset);
        noPresetView.setOnClickListener(this);
        selectedView = noPresetView;
        setViewAsSelected(noPresetView, true);
    }

    public PresetSelectorView(Context context) {
        this(context, null);
    }

    public PresetTileView addPreset(Preset preset) {

        PresetTileView presetView = new PresetTileView(getContext());
        presetView.setPreset(preset);
        presetView.setThumbSourceBM(thumbnailBM);
        presetView.setOnClickListener(this);

        addView(presetView, getChildCount() - 1);
        return presetView;
    }

    public void replaceSelectedPreset(Preset newPreset) {

        if (selectedView instanceof PresetTileView) {
            ((PresetTileView) selectedView).setPreset(newPreset);
        } else {
            setViewAsSelected(addPreset(newPreset), true);
        }
    }

    private void removePreset(PresetTileView presetTileView) {

        removeView(presetTileView);
        if (selectedView.equals(presetTileView)) {
            selectedView = null;
        }
        listener.onRemovePreset(presetTileView.getPreset());
    }

    /**
     * TODO
     * @param sourceBM
     */
    public void setThumbnailSource (Bitmap sourceBM) {
        ThumbnailScaleTask thumbTask = new ThumbnailScaleTask();
        thumbTask.execute(sourceBM);
    }

    private List<PresetTileView> getPresetTileViews() {
        List<PresetTileView> presetViews = new ArrayList<>();

        for (int index = 0; index < getChildCount(); index ++) {
            View child = getChildAt(index);
            if (child instanceof PresetTileView) {
                presetViews.add((PresetTileView) child);
            }
        }

        return presetViews;
    }

    @Override
    public void onClick(View v) {
        setViewAsSelected(selectedView, false);
        selectedView = v;
        setViewAsSelected(selectedView, true);

        if (v instanceof PresetTileView) {
            PresetTileView presetTileView = (PresetTileView) v;
            listener.onSelectPreset(presetTileView.getPreset());
        } else if (v.getId() == R.id.no_preset) {
            listener.onSelectPreset(null);
        } else if (v.getId() == R.id.new_preset) {
            listener.onCreateNewPreset();
        }
    }

    public Preset getSelectedPreset() {
        if (selectedView instanceof PresetTileView) {
            return ((PresetTileView) selectedView).getPreset();
        } else {
            return null;
        }
    }

    private void setViewAsSelected(View v, boolean isSelected) {
        selectedView = v;

        if (isSelected) {
            v.setBackgroundColor(COLOR_SELECTED);
        } else {
            v.setBackgroundColor(COLOR_UNSELECTED);
        }
    }

    public interface Listener {
        /**
         * TODO
         * @param preset
         */
        void onSelectPreset(Preset preset);

        /**
         *
         * @param preset
         */
        void onRemovePreset(Preset preset);

        /**
         *
         */
        void onCreateNewPreset();
    }

    private class ThumbnailScaleTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private ImageView thumbnailView;
        private int thumbViewWidth;
        private int thumbViewHeight;

        @Override
        protected void onPreExecute () {
            thumbnailView = (ImageView) noPresetView.findViewById(R.id.original_thumbnail_view);
            thumbViewWidth = thumbnailView.getWidth();
            thumbViewHeight = thumbnailView.getHeight();
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap sourceBM = params[0];

            int sourceWidth = sourceBM.getWidth();
            int sourceHeight = sourceBM.getHeight();

            int dstWidth;
            int dstHeight;

            if (sourceWidth >= sourceHeight) {
                dstHeight = thumbViewHeight;
                dstWidth = Math.round((float) sourceWidth
                        / (float) sourceHeight * (float) thumbViewWidth);
            } else {
                dstHeight = Math.round((float) sourceHeight
                        / (float) sourceWidth * (float) thumbViewHeight);
                dstWidth = thumbViewWidth;
            }

            return thumbnailBM = Bitmap.createScaledBitmap(sourceBM,
                    dstWidth, dstHeight, false);
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            thumbnailView.setImageBitmap(result);
            for (PresetTileView presetView : getPresetTileViews()) {
                presetView.setThumbSourceBM(result);
            }
        }
    }

    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }
}

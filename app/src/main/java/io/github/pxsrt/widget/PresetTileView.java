package io.github.pxsrt.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.pxsrt.R;
import io.github.pxsrt.presets.Preset;

/**The view used to represent a preset in the PresetSelectorView.
 *
 * Created by George on 2015-09-02.
 */
public class PresetTileView extends LinearLayout{

    private Preset preset;
    private Bitmap thumbSource;

    private final ImageView thumbnailView;
    private final TextView titleView;

    public PresetTileView(Context context) {
        super(context);

        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);
        setClickable(true);

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (10 * scale + 0.5f);
        setPadding(padding, padding, padding, padding);

        LayoutInflater.from(context).inflate(R.layout.view_preset_tile, this, true);

        thumbnailView = (ImageView) findViewById(R.id.preset_thumbnail);
        titleView = (TextView) findViewById(R.id.preset_title);
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
        if (preset != null) {
            titleView.setText(preset.getName());
        }
        updateThumbnail();
    }

    public void setThumbSourceBM(Bitmap bm) {
        thumbSource = bm;
        updateThumbnail();
    }

    private void updateThumbnail() {
        if (thumbSource != null && preset != null) {
            SortThumbTask sortThumbTask = new SortThumbTask();
            sortThumbTask.execute(thumbSource);
        }
    }

    private class SortThumbTask extends AsyncTask<Bitmap, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap sortedThumb = params[0].copy(params[0].getConfig(), true);
            preset.getSorter().apply(sortedThumb);
            return sortedThumb;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            thumbnailView.setImageBitmap(bitmap);
        }
    }
}

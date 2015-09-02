package com.github.pxsrt;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pxsrt.presets.Preset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/** todo setting/creating thumbnails (medium priority)
 * Created by George on 2015-08-02.
 */
public class PresetFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = PresetFragment.class.getSimpleName();
    private Preset preset = null;

    private OnPresetSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_preset, container, false);
        view.setOnClickListener(this);
        return view;
    }

    private void updateTitle() {
        if (getView() != null && preset != null) {
            TextView presetTitle = (TextView) getView().findViewById(R.id.preset_title);
            presetTitle.setText(preset.getName());
        }
    }

    private void updateThumbnail() {
        Context context = getActivity();

        if(getView() != null && preset != null) {
            ImageView presetThumbnail = (ImageView) getView().findViewById(R.id.preset_thumbnail);
            InputStream thumbnailStream = null;

            try {
                if (preset.isDefault()) {
                    thumbnailStream = context.getAssets().open(preset.getThumbnailFileName());
                } else {
                    thumbnailStream = new FileInputStream(
                            new File(context.getFilesDir(), preset.getThumbnailFileName()));
                }
            } catch (IOException e) {
                Log.e(TAG, "An error occured while decoding the thumbnail for " + preset.getName()
                        + ". Using default thumbnail.", e);
            }

            Bitmap thumbnailBm = BitmapFactory.decodeStream(thumbnailStream);
            presetThumbnail.setImageBitmap(thumbnailBm);
        }
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
        updateTitle();
        updateThumbnail();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onPresetSelected(preset);
        }
    }

    public void setListener(OnPresetSelectedListener listener) {
        this.listener = listener;
        updateTitle();
    }

    public interface OnPresetSelectedListener {
        void onPresetSelected(Preset preset);
    }
}

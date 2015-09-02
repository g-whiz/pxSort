package com.github.pxsrt.presets;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.github.pxsrt.presets.PresetConstants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-07-28.
 */
public class PresetManager {

    public static final String TAG = PresetManager.class.getSimpleName();

    private Context context;
    private List<Preset> presets;

    public PresetManager(Context context) {
        this.context = context;
        this.presets = new ArrayList<>();
        try {
            loadPresets();
        } catch (IOException e) {

        }
    }

    public void addPreset(Preset preset) {
        presets.add(preset);
    }

    public void removePreset(Preset preset) {
        presets.remove(preset);
    }

    public List<Preset> getPresets() {
        return presets;
    }

    private void loadPresets() throws IOException {

        FileReader userPresetsReader = null;
        try {
            userPresetsReader = new FileReader(new File(
                    context.getFilesDir(), USER_PRESETS));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "There was an error in loading user presets. User presets were not" +
                    "loaded.", e);
        }
        InputStreamReader defaultPresetsFileReader = new InputStreamReader(
                context.getAssets().open(DEFAULT_PRESETS));

        for (Reader reader : new Reader[]{userPresetsReader, defaultPresetsFileReader}) {
            BufferedReader presetReader = null;
            try {
                presetReader = new BufferedReader(reader);
                String presetStr;
                while ((presetStr = presetReader.readLine()) != null) {
                    try {
                        presets.add(PresetDecoder.decode(new JSONObject(presetStr)));
                    } catch (JSONException e) {
                        Log.e(TAG, "There was an error in loading a preset. Skipping...", e);
                    }
                }
            } finally {
                presetReader.close();
            }
        }
    }

    private void saveUserPresets() throws IOException {
        File userPresetsFile = new File(context.getFilesDir(), USER_PRESETS);

        PrintWriter presetsWriter = null;
        try {
            presetsWriter = new PrintWriter(userPresetsFile);

            if (userPresetsFile.exists()) {
                userPresetsFile.delete();
                userPresetsFile.createNewFile();
            }

            for (Preset preset : presets) {
                presetsWriter.println(PresetEncoder.encode(preset).toString());
            }
        } finally {
            presetsWriter.close();
        }
    }
}

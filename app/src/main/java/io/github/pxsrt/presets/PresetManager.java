package io.github.pxsrt.presets;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

    private File userPresetsFile;

    public PresetManager(Context context) {
        this.context = context;
        this.presets = new ArrayList<>();
        loadUserPresetsFile();
        loadPresets();
    }

    public boolean addPreset(Preset preset) {
        if (! presets.contains(preset)) {
            presets.add(preset);
            saveUserPresets();
            return true;
        } else {
            return false;
        }
    }

    public boolean removePreset(Preset preset) {
        if (presets.contains(preset)) {
            presets.remove(preset);
            saveUserPresets();
            return true;
        } else {
            return false;
        }
    }

    public List<Preset> getPresets() {
        List<Preset> presetsShallowCopy = new ArrayList<>();
        presetsShallowCopy.addAll(presets);
        return presetsShallowCopy;
    }

    private void loadPresets(){

        Reader userPresetsReader = getUserPresetsReader();
        Reader defaultPresetsReader = getDefaultPresetsReader();

        for (Reader reader : new Reader[]{userPresetsReader, defaultPresetsReader}) {
            if (reader != null) {
                BufferedReader presetReader = new BufferedReader(reader);

                String encodedPreset;
                try {
                    encodedPreset = presetReader.readLine();
                } catch (IOException e) {
                    encodedPreset  = null;
                    Log.e(TAG, "An error occurred while reading a preset from a file.", e);
                }

                while (encodedPreset != null) {

                    Preset preset = null;
                    try {
                        JSONObject presetJSON = new JSONObject(encodedPreset);
                        preset = PresetDecoder.decode(presetJSON);
                    } catch (JSONException e) {
                        Log.e(TAG, "There was an error in loading a preset with JSON encoding: " +
                                encodedPreset, e);
                    }

                    if (preset != null) {
                        presets.add(preset);
                    }

                    try {
                        encodedPreset = presetReader.readLine();
                    } catch (IOException e) {
                        encodedPreset = null;
                        Log.e(TAG, "An error occurred while reading a preset from a file.", e);
                    }
                }
            } else {
                Log.e(TAG, "loadPresets: A reader is null. Skipping...");
            }
        }
    }

    private Reader getUserPresetsReader(){
        Reader userPresetsReader = null;
        if (userPresetsFile != null) {
            try {
                userPresetsReader = new FileReader(userPresetsFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Could not instantiate a Reader for user presets. " +
                        "User presets will not be read.");
            }
        }

        return userPresetsReader;
    }

    private Reader getDefaultPresetsReader() {
        InputStreamReader defaultPresetsFileReader = null;
        try {
            InputStream defaultPresetsStream =
                    context.getAssets().open(PresetConstants.DEFAULT_PRESETS);
            Log.d(TAG, "Default presets stream opened.");

            defaultPresetsFileReader = new InputStreamReader(defaultPresetsStream);
            Log.d(TAG, "Default presets reader initialized.");
        } catch (IOException e) {
            Log.e(TAG, "An error occurred while loading default presets. " +
                    "Default presets were not loaded.");
        }

        return defaultPresetsFileReader;
    }

    private void loadUserPresetsFile() {
        userPresetsFile = new File(context.getFilesDir(), PresetConstants.USER_PRESETS);
        if (! userPresetsFile.exists()) {
            Log.d(TAG, "User presets file does not exist. Creating new file.");
            try {
                if (! userPresetsFile.createNewFile()) {
                    userPresetsFile = null;
                    Log.e(TAG, "ERROR: unable to create user presets file." +
                            " Presets will not be saved.");
                }
            } catch (IOException e) {
                userPresetsFile = null;
                Log.e(TAG, "ERROR: unable to create user presets file. Presets will not be saved.");
            }
        }
    }

    private void saveUserPresets(){

        if (userPresetsFile != null) {
            boolean deleted = userPresetsFile.delete();
            boolean recreated;
            try {
                recreated = userPresetsFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "saveUserPresets: an error occurred while attempting " +
                        "to create " + PresetConstants.USER_PRESETS +
                        ". Save attempt aborted.", e);
                return;
            }

            if (deleted && recreated) {
                PrintWriter presetsWriter;
                try {
                    presetsWriter = new PrintWriter(userPresetsFile);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Unexpected error while writing user presets to file, aborting.", e);
                    return;
                }

                int savedCount = 0;
                for (Preset preset : presets) {
                    if (! preset.isDefault()) {
                        try {
                            JSONObject encodedPreset = PresetEncoder.encode(preset);
                            presetsWriter.println(encodedPreset.toString());
                            savedCount++;
                        } catch (JSONException e) {
                            Log.e(TAG, "The preset \"" + preset.getName() + "\" was not" +
                                    "loaded due to an error.", e);
                        }
                    }
                }
                Log.d(TAG, "Saved " + savedCount + " user presets to file.");

                presetsWriter.close();
            }
        }
    }
}

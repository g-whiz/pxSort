package com.github.pxsrt.sort;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 2015-06-22.
 */
public class SorterPresets {

    public static final String TAG = SorterPresets.class.getSimpleName();

    private static final String PRESET_EXTENSION = ".pxsrt";
    private static final List<Preset> defaultPresets = new ArrayList<>();
    private static final List<Preset> userPresets = new ArrayList<>();

    public static void loadPresets(Context context) {
        try {
            /*Adding default Presets.*/
            String[] presetAssets = context.getAssets().list("presets");
            for (String fileName : presetAssets) {
                if (fileName.endsWith(PRESET_EXTENSION)) {
                    ObjectInput presetInput =
                            new ObjectInputStream(context.getAssets().open(fileName));
                    defaultPresets.add((Preset) presetInput.readObject());
                }
            }

            /*Adding user Presets.*/
            File[] privateStorage = context.getFilesDir().listFiles();
            for (File file : privateStorage) {
                if (file.getName().endsWith(PRESET_EXTENSION)) {
                    ObjectInput presetInput = new ObjectInputStream(new FileInputStream(file));
                    userPresets.add((Preset) presetInput.readObject());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "An error occurred when loading saved presets. Presets were not loaded.", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "An unexpected class was found in the" +
                    " saved presets. Presets were not loaded.", e);
        }
    }

    public static void savePreset(Context context, Preset preset){
        Log.d(TAG, "Saving preset \" " + preset.name +" \"");
        File presetFile = new File(context.getFilesDir(), preset.fileName);
        if (presetFile.exists()) {
            presetFile.delete();
            try {
                presetFile.createNewFile();
                ObjectOutput output = new ObjectOutputStream(new FileOutputStream(presetFile));
                output.writeObject(preset);
            } catch (IOException e) {
                Log.e(TAG, "An error occurred while saving the preset \" " + preset.name + " \". " +
                        "It was not saved. ", e);
                return;
            }
        }
        Log.d(TAG, "Preset saved.");
    }

    public static List<Preset> getPresets() {
        List<Preset> presets = new ArrayList<>();
        presets.addAll(defaultPresets);
        presets.addAll(userPresets);
        return presets;
    }

    public static void addUserPreset(String name, PixelSorter sorter){
        Preset preset = new Preset(name, generateFileName(name), sorter);
        userPresets.add(preset);
    }

    //TODO removeUserPreset

    private static String generateFileName(String name) {
        int nameCollisions = 0;
        for (Preset preset : userPresets) {
            if (preset.name.equals(name))
                nameCollisions++;
        }
        return name + nameCollisions + PRESET_EXTENSION;
    }

    public static class Preset {
        private String name;
        private String fileName;
        private PixelSorter sorter;

        public Preset(String name, String fileName, PixelSorter sorter) {
            this.name = name;
            this.sorter = sorter;
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public String getFileName() {
            return fileName;
        }

        public PixelSorter getSorter() {
            return sorter;
        }

        public void setSorter(PixelSorter sorter) {
            this.sorter = sorter;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}

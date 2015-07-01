package com.github.pxsrt.sort;

import android.graphics.Color;

/**
 * Created by George on 2015-05-22.
 */
public class Pixel {

    private int color;

    public Pixel(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int red() {
        return Color.red(color);
    }

    public void setRed(int value){
        this.color = Color.rgb(value, green(), blue());
    }

    public int green() {
        return Color.green(color);
    }

    public void setGreen(int value) {
        this.color = Color.rgb(red(), value, blue());
    }

    public int blue() {
        return Color.blue(color);
    }

    public void setBlue(int value) {
        this.color = Color.rgb(red() ,green(), value);
    }

    public float hue() {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);

        return hsv[0];
    }

    public void setHue(float hue) {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);
        hsv[0] = hue;
        color = Color.HSVToColor(hsv);
    }

    public float saturation() {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);

        return hsv[1];
    }

    public void setSaturation(float saturation) {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);
        hsv[1] = saturation;
        color = Color.HSVToColor(hsv);
    }

    public float value() {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);

        return hsv[2];
    }

    public void setValue(float value) {
        float[] hsv = null;
        Color.colorToHSV(color, hsv);
        hsv[2] = value;
        color = Color.HSVToColor(hsv);
    }
}


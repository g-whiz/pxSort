package com.github.pxsrt;

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
}


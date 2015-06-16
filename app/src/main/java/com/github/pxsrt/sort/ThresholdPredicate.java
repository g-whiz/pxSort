package com.github.pxsrt.sort;

/**
 * Created by George on 2015-06-10.
 */
public abstract class ThresholdPredicate extends PixelPredicate{
    private volatile int threshold;

    private final int thresholdMax;
    private final int thresholdMin;

    public ThresholdPredicate(String name, int thresholdMin, int thresholdMax) {
        super(name);
        this.thresholdMax = thresholdMax;
        this.thresholdMin = thresholdMin;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold){

        if (thresholdMin <= threshold && threshold <= thresholdMax) {
            this.threshold = threshold;
        } else {
            this.threshold = thresholdMin;
        }
    }
}

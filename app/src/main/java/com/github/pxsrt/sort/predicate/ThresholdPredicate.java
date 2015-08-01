package com.github.pxsrt.sort.predicate;

import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-21.
 */
public abstract class ThresholdPredicate extends PixelPredicate {

    private volatile Number threshold;

    private final Number thresholdMin;
    private final Number thresholdMax;
    public ThresholdPredicate(String type, String component,
                              Number thresholdMax, Number thresholdMin, Number threshold) {
        super(type, component);
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
        this.threshold = threshold;
    }

    @Override
    public boolean apply(Pixel pixel) {
        switch (getType()) {
            case PixelPredicate.ABOVE_THRESHOLD:
                return evaluate(pixel).doubleValue() >= threshold.doubleValue();

            case PixelPredicate.BELOW_THRESHOLD:
                return evaluate(pixel).doubleValue() <= threshold.doubleValue();

            default:
                return true;
        }
    }

    public Number getThreshold() {
        return threshold;
    }

    public void setThreshold(Float threshold) {
        if (thresholdMin.doubleValue() <= threshold && threshold <= thresholdMax.doubleValue()) {
            this.threshold = threshold;
        } else {
            this.threshold = thresholdMin;
        }
    }
}

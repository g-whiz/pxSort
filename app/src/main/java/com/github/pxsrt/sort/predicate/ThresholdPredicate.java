package com.github.pxsrt.sort.predicate;

import com.github.pxsrt.sort.Evaluator;
import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-21.
 */
public abstract class ThresholdPredicate extends PixelPredicate {

    /**The value is above the threshold.*/
    public static final String ABOVE = "Above Threshold";
    /**The value is below the threshold.*/
    public static final String BELOW = "Below Threshold";

    private volatile Number threshold;

    private final Number thresholdMin;
    private final Number thresholdMax;
    private final String aboveOrBelow;

    public ThresholdPredicate(String name,
                              Number thresholdMin, Number thresholdMax,
                              String aboveOrBelow, Number threshold) {
        super(name);
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
        this.aboveOrBelow = aboveOrBelow;
        this.threshold = threshold;
    }

    public ThresholdPredicate(String name, Evaluator<Pixel, Number> evaluator,
                              Number thresholdMin, Number thresholdMax,
                              String aboveOrBelow) {
        this(name, thresholdMin, thresholdMax, aboveOrBelow, thresholdMin);
    }

    @Override
    public boolean apply(Pixel pixel) {
        switch (aboveOrBelow) {
            case ABOVE:
                return evaluate(pixel).doubleValue() >= threshold.doubleValue();

            case BELOW:
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

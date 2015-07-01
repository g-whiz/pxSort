package com.github.pxsrt.sort.predicate;

import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-21.
 */
public abstract class RangePredicate extends PixelPredicate {

    private volatile Number lowerBound;
    private volatile Number upperBound;

    private final Number rangeMin;
    private final Number rangeMax;

    public RangePredicate(String name,
                          Number rangeMin, Number rangeMax,
                          Number lowerBound, Number upperBound) {
        super(name);
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    public RangePredicate(String name,
                          Number rangeMin, Number rangeMax) {
        this(name, rangeMin, rangeMax, rangeMin, rangeMax);
    }

    @Override
    public boolean apply(Pixel pixel) {
        return evaluate(pixel).doubleValue() >= lowerBound.doubleValue()
                && evaluate(pixel).doubleValue() <= upperBound.doubleValue();
    }

    public Number getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Number upperBound) {
        if (upperBound.doubleValue() >= rangeMin.doubleValue()
                && upperBound.doubleValue() <= rangeMax.doubleValue()) {
            this.upperBound = upperBound;
        } else {
            this.upperBound = rangeMax;
        }
    }

    public Number getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Number lowerBound) {
        if (lowerBound.doubleValue() >= rangeMin.doubleValue()
                && lowerBound.doubleValue() <= rangeMax.doubleValue()) {
            this.upperBound = lowerBound;
        } else {
            this.lowerBound = rangeMin;
        }
    }
}

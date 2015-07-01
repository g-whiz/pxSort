package com.github.pxsrt.sort.predicate;

import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-25.
 */
public class PixelPredicateFactory {

    /**The predicate will return true if the pixel's component value is at or above the threshold.
     * For use with {@link #createPredicate(String, String, Number) 
     * createThresholdPredicate}
     */
    public static final String ABOVE_THRESHOLD = ThresholdPredicate.ABOVE;

    /**The predicate will return true if the pixel's component value is at or below the threshold.
     * For use with {@link #createPredicate(String, String, Number) 
     * createThresholdPredicate}
     */
    public static final String BELOW_THRESHOLD = ThresholdPredicate.BELOW;

    /**The predicate will return true if the pixel's component valueis within the specified range.
     * For use with {@link #createPredicate(String, String, Number, Number, Number, Number)
     * createRangePredicate}
     */
    public static final String WITHIN_RANGE = "Within Range";

    /**The predicate will examine the pixel's red component.*/
    public static final String RED = "Red";

    /**The predicate will examine the pixel's green component.*/
    public static final String GREEN = "Green";

    /**The predicate will examine the pixel's blue component.*/
    public static final String BLUE = "Blue";

    /**The predicate will examine the pixel's hue component.*/
    public static final String HUE = "Hue";

    /**The predicate will examine the pixel's saturation component.*/
    public static final String SATURATION = "Saturation";

    /**The predicate will examine the pixel's value component.*/
    public static final String VALUE = "Value";

    /**
     * Returns a new PixelPredicate according to the specified parameters.
     * @param aboveOrBelow String Either {@link #ABOVE_THRESHOLD ABOVE_THRESHOLD} or {@link #BELOW_THRESHOLD BELOW_THRESHOLD}.
     * @param component String The component of each {@link } on which the threshold will act.
     * @param threshold Number The value of the PixelPredicate's threshold.
     * @return
     */
    public static PixelPredicate createPredicate(String aboveOrBelow, String component, Number threshold) {
        switch (component) {
            case RED:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0.0, 360.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0.0, 1.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow, 0.0, 1.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }

    public static PixelPredicate createPredicate(String name, String component,
                                                 Number rangeMin, Number rangeMax,
                                                 Number lowerBound, Number upperBound) {
        switch (component) {
            case RED:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new RangePredicate(component + " Within Range", 0.0, 360.0, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new RangePredicate(component + " Within Range", 0.0, 1.0, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new RangePredicate(component + " Within Range", 0.0, 1.0, lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }
}

package com.github.pxsrt.sort.predicate;

import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-25.
 */
public class PixelPredicateFactory {

    /**Return a new {@link ThresholdPredicate ThresholdPredicate} that returns true when a given
     * pixel's component is above the predicate's threshold.
     * For use with {@link #createPredicate(String, String, Number, Number) createPredicate}
     */
    public static final String ABOVE_THRESHOLD = ThresholdPredicate.ABOVE;

    /**Return a new {@link ThresholdPredicate ThresholdPredicate} that returns true when a given
     * pixel's component is below the predicate's threshold.
     * For use with {@link #createPredicate(String, String, Number, Number)
     * createThresholdPredicate}
     */
    public static final String BELOW_THRESHOLD = ThresholdPredicate.BELOW;

    /**Return a new {@link RangePredicate RangePredicate}.
     * For use with {@link #createPredicate(String, String, Number, Number)
     * createPredicate}
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
     * @param type String Either {@link #ABOVE_THRESHOLD ABOVE_THRESHOLD},
     * {@link #BELOW_THRESHOLD BELOW_THRESHOLD}, or {@link #WITHIN_RANGE WITHIN_RANGE}.
     * @param component The component of each {@link Pixel Pixel} on which the threshold will act.
     * @param val1 int If the given type is {@link #ABOVE_THRESHOLD ABOVE_THRESHOLD} or
     * {@link #BELOW_THRESHOLD BELOW_THRESHOLD} this value is the threshold of the new
     * PixelPredicate. If the given type is {@link #WITHIN_RANGE WITHIN_RANGE}, this value is the
     * lower bounnd of the PixelPredicate's range.
     * @param val2 int If the given type is {@link #ABOVE_THRESHOLD ABOVE_THRESHOLD} or
     * {@link #BELOW_THRESHOLD BELOW_THRESHOLD} this value is discarded.
     * If the given type is {@link #WITHIN_RANGE WITHIN_RANGE}, this value is the
     * upper bounnd of the PixelPredicate's range.
     * @return A new PixelPredicate
     */
    public static PixelPredicate createPredicate(String type, String component,
                                                 Number val1, Number val2){
        if (type.equals(WITHIN_RANGE)) {
            return createRangePredicate(component, val1, val2);
        } else if (type.equals(ABOVE_THRESHOLD) || type.equals(BELOW_THRESHOLD)){
            return createThresholdPredicate(type, component, val1);
        }
        return null;
    }

    private static ThresholdPredicate createThresholdPredicate(String aboveOrBelow, String component,
                                                          Number threshold) {
        switch (component) {
            case RED:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0x00, 0xFF, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0.0, 360.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0.0, 1.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new ThresholdPredicate(component + " " + aboveOrBelow,
                        0.0, 1.0, aboveOrBelow, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }

    private static RangePredicate createRangePredicate(String component, Number lowerBound,
                                                      Number upperBound) {
        switch (component) {
            case RED:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new RangePredicate(component + " Within Range", 0x00, 0xFF,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new RangePredicate(component + " Within Range", 0.0, 360.0,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new RangePredicate(component + " Within Range", 0.0, 1.0,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new RangePredicate(component + " Within Range", 0.0, 1.0,
                        lowerBound, upperBound) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }
}

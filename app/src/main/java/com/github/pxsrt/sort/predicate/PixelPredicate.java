package com.github.pxsrt.sort.predicate;

import com.android.internal.util.Predicate;
import com.github.pxsrt.sort.Evaluator;
import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-12.
 */
public abstract class PixelPredicate implements Predicate<Pixel>, Evaluator<Pixel, Number> {

    public String getType() {
        return type;
    }

    public String getComponent() {
        return component;
    }

    private final String type;
    private final String component;

    public PixelPredicate(String type, String component){
        this.type = type;
        this.component = component;
    }

    @Override
    public String toString(){
        return component + type;
    }

    /**Return a new {@link ThresholdPredicate ThresholdPredicate} that returns true when a given
     * pixel's component is above the predicate's threshold.
     * For use with {@link #createPredicate(String, String, Number, Number) createPredicate}
     */
    public static final String ABOVE_THRESHOLD = "Above Threshold";

    /**Return a new {@link ThresholdPredicate ThresholdPredicate} that returns true when a given
     * pixel's component is below the predicate's threshold.
     * For use with {@link #createPredicate(String, String, Number, Number)
     * createThresholdPredicate}
     */
    public static final String BELOW_THRESHOLD = "Below Threshold";

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
            return createThresholdPredicate(component, type, val1);
        }
        return null;
    }

    private static ThresholdPredicate createThresholdPredicate(String component, String type,
                                                               Number threshold) {
        switch (component) {
            case RED:
                return new ThresholdPredicate(type, component, 0xFF, 0x00, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new ThresholdPredicate(type, component, 0xFF, 0x00, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new ThresholdPredicate(type, component, 0xFF, 0x00, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new ThresholdPredicate(type, component, 360.0, 0.0, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new ThresholdPredicate(type, component,  1.0, 0.0, threshold) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new ThresholdPredicate(type, component, 1.0, 0.0, threshold) {
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
                return new RangePredicate(WITHIN_RANGE, component, 0xFF,
                        lowerBound, upperBound, 0x00) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.red();
                    }
                };
            case GREEN:
                return new RangePredicate(WITHIN_RANGE, component, 0xFF,
                        lowerBound, upperBound, 0x00) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.green();
                    }
                };
            case BLUE:
                return new RangePredicate(WITHIN_RANGE, component, 0xFF,
                        lowerBound, upperBound, 0x00) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.blue();
                    }
                };
            case HUE:
                return new RangePredicate(WITHIN_RANGE, component, 360.0,
                        lowerBound, upperBound, 0.0) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.hue();
                    }
                };
            case SATURATION:
                return new RangePredicate(WITHIN_RANGE, component, 1.0,
                        lowerBound, upperBound, 0.0) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.saturation();
                    }
                };
            case VALUE:
                return new RangePredicate(WITHIN_RANGE, component, 1.0,
                        lowerBound, upperBound, 0.0) {
                    @Override
                    public Number evaluate(Pixel input) {
                        return input.value();
                    }
                };
        }
        return null;
    }
}

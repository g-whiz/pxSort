package io.github.pxsrt.sort.predicate;

import com.android.internal.util.Predicate;
import io.github.pxsrt.PixelComponents;
import io.github.pxsrt.sort.Pixel;

import static io.github.pxsrt.PixelComponents.RED;
import static io.github.pxsrt.PixelComponents.GREEN;
import static io.github.pxsrt.PixelComponents.BLUE;
import static io.github.pxsrt.PixelComponents.HUE;
import static io.github.pxsrt.PixelComponents.SATURATION;
import static io.github.pxsrt.PixelComponents.VALUE;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by George on 2015-06-21.
 */
public class ComponentPredicate implements Predicate<Pixel> {

    private static final Map<String, Double[]> componentBoundsMap = new HashMap<>();
        static {
            Double[] rgbMinMax = new Double[]{0.0, 255.0};
            Double[] satValMinMax = new Double[]{0.0, 1.0};
            componentBoundsMap.put(RED, rgbMinMax);
            componentBoundsMap.put(GREEN, rgbMinMax);
            componentBoundsMap.put(BLUE, rgbMinMax);
            componentBoundsMap.put(HUE, new Double[]{0.0, 360.0});
            componentBoundsMap.put(SATURATION, satValMinMax);
            componentBoundsMap.put(VALUE, satValMinMax);
        }

    private String component;

    private Double lowerBound;
    private Double upperBound;

    private Double boundsMin;
    private Double boundsMax;

    /**
     * Sole Constructor
     * @param component String The component of a Pixel that this ComponentPredicate will examine.
     * @param lowerBound Double The value that the component of a Pixel must be at or higher than
     *                   for this ComponentPredicate to return true when applied to the Pixel.
     *                   If null is given, this ComponentPredicate will not have a lower bound.
     * @param upperBound Double The value that the component of a Pixel must be at or lower than
     *                   for this ComponentPredicate to return true when applied to the Pixel.
     *                   If null is given, this ComponentPredicate will not have an upper bound.
     */
    public ComponentPredicate(String component, Number lowerBound, Number upperBound) {
        this.component = component;
        Double[] componentBounds = componentBoundsMap.get(component);
        boundsMin = componentBounds[0];
        boundsMax = componentBounds[1];

        if (lowerBound != null) {
            setLowerBound(lowerBound);
        } else {
            this.lowerBound = null;
        }

        if (upperBound != null) {
            setUpperBound(upperBound);
        } else {
            this.upperBound = null;
        }
    }

    @Override
    public boolean apply(Pixel pixel) {
        return (lowerBound == null || isAboveLowerBound(pixel))
                && (upperBound == null || isBelowUpperBound(pixel));
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isAboveLowerBound(Pixel pixel) {
        return PixelComponents.getFromPixel(component, pixel).doubleValue() >= lowerBound;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isBelowUpperBound(Pixel pixel) {
        return PixelComponents.getFromPixel(component, pixel).doubleValue() <= upperBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    private void setUpperBound(Number upperBound) {
        double dblUpperBound = upperBound.doubleValue();

        if (dblUpperBound >= boundsMin && dblUpperBound <= boundsMax) {
            this.upperBound = dblUpperBound;
        } else {
            this.upperBound = boundsMax;
        }
    }

    public double getLowerBound() {
        return lowerBound;
    }

    private void setLowerBound(Number lowerBound) {
        double dblLowerBound = lowerBound.doubleValue();

        if (dblLowerBound >= boundsMin && dblLowerBound <= boundsMax) {
            this.lowerBound = dblLowerBound;
        } else {
            this.lowerBound = boundsMin;
        }
    }

    public String getComponent() {
        return component;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ComponentPredicate
                && component.equals(((ComponentPredicate)o).getComponent())
                && lowerBound == ((ComponentPredicate)o).getLowerBound()
                && upperBound == ((ComponentPredicate)o).getUpperBound();
    }
}

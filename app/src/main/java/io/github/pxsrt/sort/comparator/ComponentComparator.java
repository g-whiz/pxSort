package io.github.pxsrt.sort.comparator;

import io.github.pxsrt.PixelComponents;
import io.github.pxsrt.sort.Pixel;

import java.util.Comparator;

/**
 * Created by George on 2015-06-11.
 */
public class ComponentComparator implements Comparator<Pixel> {

    /**Values will be sorted in ascending (left to right) order.*/
    public static final String ASCENDING_ORDER = "Ascending Order";

    /**Values will be sorted in descending (left to right) order.*/
    public static final String DESCENDING_ORDER = "Descending Order";

    private volatile String ordering;

    public String getComponent() {
        return component;
    }

    private final String component;

    /**
     * Main constructor. Ordering is set to DESCENDING_ORDER by default.
     * @param component The component of this PixelComparator.
     */
    public ComponentComparator(String component, String ordering) {
        this.component = component;
        this.ordering = ordering;
    }


    @Override
    public int compare(Pixel lpx, Pixel rpx) {

        int result;

        if(PixelComponents.getFromPixel(component, lpx).doubleValue()
                >= PixelComponents.getFromPixel(component, rpx).doubleValue()){
            result = 1;
        } else if (PixelComponents.getFromPixel(component, lpx).doubleValue()
                == PixelComponents.getFromPixel(component, lpx).doubleValue()) {
            result = 0;
        } else {
            result = -1;
        }

        switch (ordering) {
            case ASCENDING_ORDER:
                return result * -1;

            case DESCENDING_ORDER:
                return result;

            default:
                return 0;
        }
    }

    /**
     * Returns the current direction this PixelComparator is ordering pixels in.
     * @return Either ASCENDING_ORDER or DESCENDING_ORDER
     */
    public String getOrdering() {
        return ordering;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ComponentComparator
                && component.equals(((ComponentComparator) o).getComponent())
                && ordering.equals(((ComponentComparator) o).getOrdering());
    }
}

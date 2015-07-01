package com.github.pxsrt.sort.predicate;

import com.android.internal.util.Predicate;
import com.github.pxsrt.sort.Evaluator;
import com.github.pxsrt.sort.Pixel;

/**
 * Created by George on 2015-06-12.
 */
public abstract class PixelPredicate implements Predicate<Pixel>, Evaluator<Pixel, Number> {

    private final String name;

    public PixelPredicate(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}

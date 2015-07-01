package com.github.pxsrt.sort;

/**
 * Created by George on 2015-06-11.
 */
public interface Evaluator<I, O> {
    O evaluate(I input);
}

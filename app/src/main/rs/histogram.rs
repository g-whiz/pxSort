/*
  A renderscript kernel to generate a histogram from pixels.
*/

#include "pxsort_util.rsh"

#pragma version(1)
#pragma rs java_package_name(io.pxsort.pxsort.sorting.renderscript)
#pragma rs_fp_relaxed

/* The component to generate a histogram of. */
uint32_t component_const;

/* The (dynamically allocated) histogram. */
int32_t *histogram;


void __attribute__((kernel)) populate_histogram(uchar4 in) {

    uchar idx = get_component(in, component_const);
    // Atomically increment the count of pixels with
    rsAtomicAdd(&(histogram[idx]), 1);
}
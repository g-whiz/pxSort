/*
 * Code shared across multiple kernels.
 */

#ifndef __PXSORT_UTIL_RSH__
#define __PXSORT_UTIL_RSH__


#define CMP_ALPHA       (0)
#define CMP_RED         (1)
#define CMP_GREEN       (2)
#define CMP_BLUE        (3)
#define CMP_HUE         (4)
#define CMP_SATURATION  (5)
#define CMP_VALUE       (6)

#define ORD_DESCENDING  (0)
#define ORD_ASCENDING   (1)

#define __HUE_COEFF     (42.666f) // coefficient for mapping interdelta [0-6) to [0-256)


/* Converts an ARGB color to an AHSV color. */
static uchar4 __argb_to_ahsv(uchar4 *px) {
    uchar4 ahsv;

    uchar a = px->x;
    uchar r = px->y;
    uchar g = px->z;
    uchar b = px->w;

    uchar rgb_min = min(r, min(g, b));
    uchar rgb_max = max(r, max(g, b));
    uchar delta = rgb_max - rgb_min;
    uchar val = rgb_max;

    if (delta == 0) { // shade of grey (one of over 50!)
        ahsv = (uchar4) {a, 0, 0, val};
        return ahsv;
    }

    // max is now guaranteed to be nonzero
    uchar sat = (uchar) (255.0 * ((float) delta / (float) val));

    uchar hue;

    if (r == rgb_max) {
        hue = (uchar) (__HUE_COEFF * ((float) (g - b) / (float) delta));

    } else if (g == rgb_max) {
        hue = (uchar) (__HUE_COEFF * (2.0 + ((float) (b - r) / (float) delta)));

    } else { // b == rgb_max
        hue = (uchar) (__HUE_COEFF * (4.0 + ((float) (r - g) / (float) delta)));
    }

    ahsv = (uchar4) {a, hue, sat, val};
    return ahsv;
}


/* Combine 2 pixels.
    todo: how will combine funcs be specified accross java/rs?

uchar4 combine_px(uchar4 *px_old, uchar4 *px_new, uint32_t combine_type) {

}
*/


/* Converts an AHSV color to an ARGB color.
 * NOTE: the approach taken when writing this method was to invert the calculations done in
 *       __argb_to_ahsv.
 */
static uchar4 __ahsv_to_argb(uchar4 *px) {
    uchar4 argb;

    uchar a = px->x;
    uchar h = px->y;
    uchar s = px->z;
    uchar v = px->w;

    if (s == 0) {
        argb = (uchar4) {a, v, v, v};
        return argb;
    }

    uchar delta = (uchar) ( (float) v * (float) s / 255.0 );
    uchar rgb_max = v;
    uchar rgb_min = (uchar) ( -1 * ((int32_t) delta - (int32_t) rgb_max) );

    float h_float = (float) h / __HUE_COEFF; // map hue to [0-6)

    float hue_ratio = h_float >= 4.0 ? h_float - 4.0
                                     : h_float >= 2.0 ? h_float - 2.0
                                                      : h_float;
    // calculate the value of the middle rgb component
    uchar rgb_mid = hue_ratio < 1.0 ? rgb_min + (uchar) (h_float * (float) delta)
                                    : 255 - ((uchar) (h_float * (float) delta) - rgb_min);

    uchar h_whole = (uchar) h_float; // truncate decimal to get the "whole" part of h_float
    switch (h_whole) {
        case 0:
            argb = (uchar4) {a, rgb_max, rgb_mid, rgb_min};
        case 1:
            argb = (uchar4) {a, rgb_max, rgb_min, rgb_mid};
        case 2:
            argb = (uchar4) {a, rgb_min, rgb_max, rgb_mid};
        case 3:
            argb = (uchar4) {a, rgb_mid, rgb_max, rgb_min};
        case 4:
            argb = (uchar4) {a, rgb_mid, rgb_min, rgb_max};
        default:
            argb = (uchar4) {a, rgb_min, rgb_mid, rgb_max};
    }

    return argb;
}


/*
 * Returns the component of px as specified by cmp_const.
 *
 * @px the pixel to extract the component from
 * @cmp_const the CMP_<component> constant corresponding to the desired component
 */
static uchar get_component(uchar4 *px, uint32_t cmp_const) {
    switch (cmp_const) {
        case CMP_ALPHA:
            return px->x;

        case CMP_RED:
            return px->y;

        case CMP_GREEN:
            return px->z;

        case CMP_BLUE:
            return px->w;

        case CMP_HUE:
            return __argb_to_ahsv(px).y;

        case CMP_SATURATION:
            return __argb_to_ahsv(px).z;

        default:
            return __argb_to_ahsv(px).w;
    }
}




#endif // __PXSORT_UTIL_RSH__
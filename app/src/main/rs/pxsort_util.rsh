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

// #define __HUE_COEFF     (42.666f) // coefficient for mapping interdelta [0-6) to [0-256)


/* Converts an ARGB color to an AHSV color. */
static void __argb_to_ahsv(uchar4 argb, uchar4 *ahsv) {

    float4 c = rsUnpackColor8888(argb); // map the rgb values to [0..1]

    // The following (magic) code is adapted from here:
    // http://lolengine.net/blog/2013/07/27/rgb-to-hsv-in-glsl

    float4 K = (float4) {0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0};

    float4 p = mix((float4) {c.b, c.g, K.w, K.z}, (float4) {c.g, c.b, K.x, K.y}, step(c.b, c.g));
    float4 q = mix((float4) {p.x, p.y, p.w, c.r}, (float4) {c.r, p.y, p.z, p.x}, step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-5;

    float3 hsv_vec = (float3) {
        fabs((float) (q.z + (q.w - q.y) / (6.0 * d + e))),
        d / (q.x + e),
        q.x
    };

    *ahsv = rsPackColorTo8888(hsv_vec.x, hsv_vec.y, hsv_vec.z, c.a);

/*
    float tR = fabs((float) (q.z + (q.w - q.y) / (6.0 * d + e)));
    float tG = d / (q.x + e);
    float tB = q.x;
    float tA = c.a;
    *ahsv = rsPackColorTo8888(tR, tG, tB, tA);
*/
}



/* Converts an AHSV color to an ARGB color. */
static void __ahsv_to_argb(uchar4 ahsv, uchar4 *argb) {

    float4 c = rsUnpackColor8888(ahsv);

    // The following (magic) code is adapted from here:
    // http://lolengine.net/blog/2013/07/27/rgb-to-hsv-in-glsl

    float4 K = (float4) {1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0};
    float3 p = fabs((fract(c.xxx + K.xyz) * 6.0 - K.www));
    float3 rgb_vec = c.z * mix(K.xxx, clamp((p - K.xxx), (float) 0.0, (float) 1.0), c.y);

    *argb = rsPackColorTo8888(rgb_vec.r, rgb_vec.g, rgb_vec.b, c.a);
}


/*
 * Returns the component of px as specified by cmp_const.
 *
 * @px the pixel to extract the component from
 * @cmp_const the CMP_<component> constant corresponding to the desired component
 */
static uchar get_component(uchar4 argb, uint32_t cmp_const) {
    uchar4 ahsv;

    switch (cmp_const) {
        case CMP_ALPHA:
            return argb.a;

        case CMP_RED:
            return argb.r;

        case CMP_GREEN:
            return argb.g;

        case CMP_BLUE:
            return argb.b;

        case CMP_HUE:
            __argb_to_ahsv(argb, &ahsv);
            return ahsv.x;

        case CMP_SATURATION:
            __argb_to_ahsv(argb, &ahsv);
            return ahsv.y;

        default: // CMP_VALUE
            __argb_to_ahsv(argb, &ahsv);
            return ahsv.z;
    }
}




#endif // __PXSORT_UTIL_RSH__





/* Converts an ARGB color to an AHSV color. */
/* COMMENTED OUT
static uchar4 __argb_to_ahsv(uchar4 *px) {
    uchar4 ahsv;

    uchar a = px->a;
    uchar r = px->r;
    uchar g = px->g;
    uchar b = px->b;

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

    ahsv.argb = (uchar4) {a, hue, sat, val};
    return ahsv;
}
*/

/* Combine 2 pixels.
    todo: how will combine funcs be specified accross java/rs?

uchar4 combine_px(uchar4 *px_old, uchar4 *px_new, uint32_t combine_type) {

}
*/




/* Converts an AHSV color to an ARGB color.
 * NOTE: the approach taken when writing this method was to invert the calculations done in
 *       __argb_to_ahsv.
 */

 /* COMMENTED OUT
static uchar4 __ahsv_to_argb(uchar4 ahsv) {
    uchar4 argb;

    uchar a = ahsv.a;
    uchar h = ahsv.r;
    uchar s = ahsv.g;
    uchar v = ahsv.b;

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
            argb.argb = (uchar4) {a, rgb_max, rgb_mid, rgb_min};
        case 1:
            argb.argb = (uchar4) {a, rgb_max, rgb_min, rgb_mid};
        case 2:
            argb.argb = (uchar4) {a, rgb_min, rgb_max, rgb_mid};
        case 3:
            argb.argb = (uchar4) {a, rgb_mid, rgb_max, rgb_min};
        case 4:
            argb.argb = (uchar4) {a, rgb_mid, rgb_min, rgb_max};
        default:
            argb.argb = (uchar4) {a, rgb_min, rgb_mid, rgb_max};
    }

    return argb;
}
*/
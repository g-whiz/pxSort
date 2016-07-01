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

#define __HUE_COEFF     (42.666f) // coefficient for mapping interval [0-6) to [0-256)


/*
 * Returns the component of px as specified by cmp_const.
 *
 * @px the pixel to extract the component from
 * @cmp_const the CMP_<component> constant corresponding to the desired component
 */
uchar get_component(uchar4 *px, uint32_t cmp_const) {
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
            return __argb_to_ahsv(px)->y;

        case CMP_SATURATION:
            return __argb_to_ahsv(px)->z;

        case CMP_VALUE:
            return __argb_to_ahsv(px)->w;
    }
}


/* Converts an ARGB color to an AHSV color. */
uchar4 __argb_to_ahsv(uchar4 *px) {
    uchar4 ahsv;

    uchar a = px->x;
    uchar r = px->y;
    uchar g = px->z;
    uchar b = px->w;

    uchar rgb_min = min(r, min(g, b));
    uchar rgb_max = max(r, max(g, b));
    uchar val = rgb_max - rgb_min;

    if (val == 0) { // shade of grey (one of over 50!)
        ahsv = {a, 0, 0, val};
        return ahsv;
    }

    // max is now guaranteed to be nonzero
    uchar sat = (uchar) (255f * ((float) val / (float) rgb_max));

    uchar hue;

    if (r == rgb_max) {
        hue = (uchar) (__HUE_COEFF * ((float) (g - b) / (float) val));

    } else if (g == rgb_max) {
        hue = (uchar) (__HUE_COEFF * (2f + ((float) (b - r) / (float) val)));

    } else { // b == rgb_max
        hue = (uchar) (__HUE_COEFF * (4f + ((float) (r - g) / (float) val)));
    }

    ahsv = {a, hue, sat, val};
    return ahsv;
}


/* Converts an AHSV color to an ARGB color. */
uchar4 __ahsv_to_argb(uchar4 *px) {
    uchar4 argb;

    uchar a = px->x;
    uchar h = px->y;
    uchar s = px->z;
    uchar v = px->w;

    if (s == 0) {
        argb = {a, v, v, v};
        return argb;
    }

    
}

/*
Skia's relevant hsv methods...

SkColor SkHSVToColor(U8CPU a, const SkScalar hsv[3]) {
    SkASSERT(hsv);

    U8CPU s = SkUnitScalarClampToByte(hsv[1]);
    U8CPU v = SkUnitScalarClampToByte(hsv[2]);

    if (0 == s) { // shade of gray
        return SkColorSetARGB(a, v, v, v);
    }
    SkFixed hx = (hsv[0] < 0 || hsv[0] >= SkIntToScalar(360)) ? 0 : SkScalarToFixed(hsv[0]/60);
    SkFixed f = hx & 0xFFFF;

    unsigned v_scale = SkAlpha255To256(v);
    unsigned p = SkAlphaMul(255 - s, v_scale);
    unsigned q = SkAlphaMul(255 - (s * f >> 16), v_scale);
    unsigned t = SkAlphaMul(255 - (s * (SK_Fixed1 - f) >> 16), v_scale);

    unsigned r, g, b;

    SkASSERT((unsigned)(hx >> 16) < 6);
    switch (hx >> 16) {
        case 0: r = v; g = t; b = p; break;
        case 1: r = q; g = v; b = p; break;
        case 2: r = p; g = v; b = t; break;
        case 3: r = p; g = q; b = v; break;
        case 4: r = t;  g = p; b = v; break;
        default: r = v; g = p; b = q; break;
    }
    return SkColorSetARGB(a, r, g, b);
}

void SkRGBToHSV(U8CPU r, U8CPU g, U8CPU b, SkScalar hsv[3]) {
    SkASSERT(hsv);

    unsigned min = SkMin32(r, SkMin32(g, b));
    unsigned max = SkMax32(r, SkMax32(g, b));
    unsigned delta = max - min;

    SkScalar v = ByteToScalar(max);
    SkASSERT(v >= 0 && v <= SK_Scalar1);

    if (0 == delta) { // we're a shade of gray
        hsv[0] = 0;
        hsv[1] = 0;
        hsv[2] = v;
        return;
    }

    SkScalar s = ByteDivToScalar(delta, max);
    SkASSERT(s >= 0 && s <= SK_Scalar1);

    SkScalar h;
    if (r == max) {
        h = ByteDivToScalar(g - b, delta);
    } else if (g == max) {
        h = SkIntToScalar(2) + ByteDivToScalar(b - r, delta);
    } else { // b == max
        h = SkIntToScalar(4) + ByteDivToScalar(r - g, delta);
    }

    h *= 60;
    if (h < 0) {
        h += SkIntToScalar(360);
    }
    SkASSERT(h >= 0 && h < SkIntToScalar(360));

    hsv[0] = h;
    hsv[1] = s;
    hsv[2] = v;
}
*/

#endif // __PXSORT_UTIL_RSH__
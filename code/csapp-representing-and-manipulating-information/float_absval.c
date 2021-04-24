#include <assert.h>

typedef unsigned float_bits;

/* Compute |f|.  If f is NaN, then return f. */
float_bits float_absval(float_bits f) {
    unsigned sign = f & 0x80000000, exp = f & 0x7f800000, frac = f & 0x007fffff;
    if (sign && !(exp == 0x7f800000 && frac != 0))
        sign = 0;
    return sign | exp | frac;
}

int main() {
    assert(float_absval(0x7f800001) == 0x7f800001);
    assert(float_absval(0x7f800000) == 0x7f800000);
    assert(float_absval(0x80000000) == 0x00000000);
    return 0;
}

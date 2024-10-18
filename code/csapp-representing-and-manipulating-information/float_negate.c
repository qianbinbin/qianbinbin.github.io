#include <assert.h>

typedef unsigned float_bits;

/* Compute -f.  If f is NaN, then return f. */
float_bits float_negate(float_bits f) {
    unsigned sign = f & 0x80000000, exp = f & 0x7f800000, frac = f & 0x007fffff;
    if (!(exp == 0x7f800000 && frac != 0))
        sign ^= 0x80000000;
    return sign | exp | frac;
}

float u2f(unsigned u) {
    return *(float *) &u;
}

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int main() {
    assert(float_negate(0x7f800001) == 0x7f800001);
    assert(float_negate(0x7f800000) == 0xff800000);
    assert(float_negate(0x00000000) == 0x80000000);
    return 0;
}

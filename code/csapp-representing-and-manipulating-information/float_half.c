#include <assert.h>

typedef unsigned float_bits;

/* Compute 0.5*f.  If f is NaN, then return f. */
float_bits float_half(float_bits f) {
    if (f << 1 == 0 || (f & 0x7f800000) == 0x7f800000)
        return f;
    unsigned sign = f & 0x80000000, exp = f & 0x7f800000, frac = f & 0x007fffff;
    if ((exp & 0x7f000000) == 0) {
        f &= 0x7fffffff;
        if ((f & 3) == 3)
            ++f;
        return sign | f >> 1;
    }
    exp -= 0x00800000;
    return sign | exp | frac;
}

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int main() {
    assert(float_half(f2u(-1.234)) == f2u(-1.234 * 0.5));
    assert(float_half(0x80000000) == 0x80000000);
    assert(float_half(0x80000001) == 0x80000000);
    assert(float_half(0x807fffff) == 0x80400000);
    assert(float_half(0x80800000) == 0x80400000);
    assert(float_half(0xff7fffff) == 0xfeffffff);
    assert(float_half(0xff800000) == 0xff800000);
    assert(float_half(0xff800001) == 0xff800001);

    assert(float_half(f2u(1.234)) == f2u(1.234 * 0.5));
    assert(float_half(0) == 0);
    assert(float_half(1) == 0);
    assert(float_half(0x007fffff) == 0x00400000);
    assert(float_half(0x00800000) == 0x00400000);
    assert(float_half(0x7f7fffff) == 0x7effffff);
    assert(float_half(0x7f800000) == 0x7f800000);
    assert(float_half(0x7f800001) == 0x7f800001);
    return 0;
}

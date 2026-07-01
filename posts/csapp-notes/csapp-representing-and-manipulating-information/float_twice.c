#include <assert.h>

typedef unsigned float_bits;

/* Compute 2*f.  If f is NaN, then return f. */
float_bits float_twice(float_bits f) {
    if (f << 1 == 0 || (f & 0x7f800000) == 0x7f800000)
        return f;
    unsigned sign = f & 0x80000000, exp = f & 0x7f800000, frac = f & 0x007fffff;
    if (exp == 0)
        return sign | f << 1;
    exp += 0x00800000;
    if (exp == 0x7f800000)
        frac = 0;
    return sign | exp | frac;
}

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int main() {
    assert(float_twice(f2u(-1.234)) == f2u(-1.234 * 2));
    assert(float_twice(0x80000000) == 0x80000000);
    assert(float_twice(0x80000001) == 0x80000002);
    assert(float_twice(0x807fffff) == 0x80fffffe);
    assert(float_twice(0x80800000) == 0x81000000);
    assert(float_twice(0xff7fffff) == 0xff800000);
    assert(float_twice(0xff800000) == 0xff800000);
    assert(float_twice(0xff800001) == 0xff800001);

    assert(float_twice(f2u(1.234)) == f2u(1.234 * 2));
    assert(float_twice(0) == 0);
    assert(float_twice(1) == 2);
    assert(float_twice(0x007fffff) == 0x00fffffe);
    assert(float_twice(0x00800000) == 0x01000000);
    assert(float_twice(0x7f7fffff) == 0x7f800000);
    assert(float_twice(0x7f800000) == 0x7f800000);
    assert(float_twice(0x7f800001) == 0x7f800001);
    return 0;
}

#include <assert.h>

typedef unsigned float_bits;

/*
 * Compute (int) f.
 * If convertion causes overflow or f is NaN, return 0x80000000
 */
int float_f2i(float_bits f) {
    int exp = ((f >> 23) & 0xff) - 127;
    if (exp < 0)
        return 0;
    if (exp > 30)
        return 0x80000000;
    int sign = f >> 31, frac = (f & 0x007fffff) | 0x00800000, value;
    if (exp < 23)
        value = frac >> (23 - exp);
    else if (exp > 23)
        value = frac << (exp - 23);
    return sign ? -value : value;
}

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int main() {
    assert(float_f2i(f2u(-1.234)) == (int) -1.234);
    assert(float_f2i(0x80000000) == 0);
    assert(float_f2i(0x80000001) == 0);
    assert(float_f2i(0x807fffff) == 0);
    assert(float_f2i(0x80800000) == 0);
    assert(float_f2i(0xff7fffff) == 0x80000000);
    assert(float_f2i(0xff800000) == 0x80000000);
    assert(float_f2i(0xff800001) == 0x80000000);

    assert(float_f2i(f2u(1.234)) == (int) 1.234);
    assert(float_f2i(0) == 0);
    assert(float_f2i(1) == 0);
    assert(float_f2i(0x007fffff) == 0);
    assert(float_f2i(0x00800000) == 0);
    assert(float_f2i(0x7f7fffff) == 0x80000000);
    assert(float_f2i(0x7f800000) == 0x80000000);
    assert(float_f2i(0x7f800001) == 0x80000000);
    return 0;
}

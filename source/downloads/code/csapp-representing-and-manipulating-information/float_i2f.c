#include <assert.h>
#include <limits.h>
#include <stdlib.h>

typedef unsigned float_bits;

/* Compute (float) i */
float_bits float_i2f(int i) {
    unsigned sign = i & 0x80000000, frac = sign ? -i : i, exp = -1;
    for (unsigned u = frac; u; ++exp) u >>= 1;
    if (exp < 23) {
        frac <<= 23 - exp;
    } else if (exp > 23) {
        unsigned w = exp - 23, mask = (unsigned) -1 >> (32 - w);
        unsigned shifts = frac & mask, mid = 1 << (w - 1);
        unsigned bias = 0;
        if (shifts > mid)
            bias = 1 << (w - 1);
        else if (shifts == mid && (frac & (1 << w)))
            bias = 1 << (w - 1);
        frac = (frac + bias) >> (exp - 23);
    }
    exp = (exp + 127) << 23;
    frac &= 0x007fffff;
    return sign | exp | frac;
}

float u2f(unsigned u) {
    return *(float *) &u;
}

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int main() {
    assert(float_i2f(INT_MIN) == f2u((float) INT_MIN));
    for (int i = 0, num; i < 1000000; ++i) {
        num = rand();
        assert(float_i2f(num) == f2u((float) num));
        assert(float_i2f(-num) == f2u((float) -num));
    }
    return 0;
}

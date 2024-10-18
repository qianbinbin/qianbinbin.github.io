#include <assert.h>
#include <limits.h>
#include <math.h>

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

float u2f(unsigned u) {
    return *(float *) &u;
}

float fpwr2(int x) {
    /* Result exponent and fraction */
    unsigned exp, frac;
    unsigned u;

    if (x < -149) {
        /* Too small.  Return 0.0 */
        exp = 0;
        frac = 0;
    } else if (x < -126) {
        /* Denormalized result */
        exp = 0;
        frac = 1 << (149 + x);
    } else if (x < 128) {
        /* Normalized result. */
        exp = x + 127;
        frac = 0;
    } else {
        /* Too big.  Return +oo */
        exp = 255;
        frac = 0;
    }

    /* Pack exp and frac into 32 bits */
    u = exp << 23 | frac;
    /* Return as float */
    return u2f(u);
}

int main() {
    assert(f2u(fpwr2(INT_MIN)) == 0 && f2u(pow(2, INT_MIN)) == 0);
    assert(f2u(fpwr2(-150)) == 0 && f2u(pow(2, -150)) == 0);
    assert(f2u(fpwr2(-149)) == 1 && f2u(pow(2, -149)) == 1);
    assert(f2u(fpwr2(-127)) == 0x00400000 && f2u(pow(2, -127)) == 0x00400000);
    assert(f2u(fpwr2(-126)) == 0x00800000 && f2u(pow(2, -126)) == 0x00800000);
    assert(f2u(fpwr2(127)) == 0x7f000000 && f2u(pow(2, 127)) == 0x7f000000);
    assert(f2u(fpwr2(128)) == 0x7f800000 && f2u(pow(2, 128)) == 0x7f800000);
    assert(f2u(fpwr2(INT_MAX)) == 0x7f800000 && f2u(pow(2, INT_MAX)) == 0x7f800000);
    return 0;
}

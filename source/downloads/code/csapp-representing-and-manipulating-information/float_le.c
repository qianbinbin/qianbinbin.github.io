#include <assert.h>
#include <stddef.h>

unsigned f2u(float f) {
    return *(unsigned *) &f;
}

int float_le(float x, float y) {
    assert(sizeof(unsigned) == 4);
    unsigned ux = f2u(x);
    unsigned uy = f2u(y);

    /* Get the sign bits */
    unsigned sx = ux >> 31;
    unsigned sy = uy >> 31;

    /* Give an expression using only ux, uy, sx, and sy */
    return (ux << 1 == 0 && uy << 1 == 0) ? 1
        : (sx ^ sy ? sx : (sx ? ux >= uy : ux <= uy));
}

int main() {
    assert(float_le(1.2, 1.3));
    assert(!float_le(-1.2, -1.3));
    assert(float_le(0.0, -0.0));
    assert(float_le(-0.0, 0.0));
    return 0;
}

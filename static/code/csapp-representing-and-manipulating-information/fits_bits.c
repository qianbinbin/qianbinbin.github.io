#include <assert.h>
#include <stddef.h>

const size_t W = sizeof(int) << 3;

/*
 * Return 1 when x can be represented as an n-bit, 2's-complement
 * number; 0 otherwise
 * Assume 1 <= n <= w
 */
int fits_bits(int x, int n) {
    assert(1 <= n && n <= W);
    unsigned mask = (unsigned) -1 >> (W - n);
    return x == (x & mask);
}

int main() {
    assert(fits_bits(0xff, 8));
    assert(!fits_bits(0x1ff, 8));
    return 0;
}

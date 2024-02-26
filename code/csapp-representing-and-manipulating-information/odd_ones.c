#include <assert.h>
#include <limits.h>

/* Return 1 when x contains an odd number of 1s; 0 otherwise.
 * Assume w=32 */
int odd_ones(unsigned x) {
    assert(sizeof(unsigned) == 4);
    x ^= x >> 16;
    x ^= x >> 8;
    x ^= x >> 4;
    x ^= x >> 2;
    x ^= x >> 1;
    return x & 1;
}

int main() {
    assert(odd_ones(0) == 0);
    assert(odd_ones(UINT_MAX) == 0);
    assert(odd_ones(INT_MIN) == 1);
    assert(odd_ones(INT_MAX) == 1);
    return 0;
}

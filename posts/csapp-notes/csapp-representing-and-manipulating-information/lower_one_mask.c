#include <assert.h>
#include <stddef.h>

const size_t W = sizeof(int) << 3;

/*
 * Mask with least signficant n bits set to 1
 * Examples: n = 6 --> 0x3F, n = 17 --> 0x1FFFF
 * Assume 1 <= n <= w
 */
int lower_one_mask(int n) {
    assert(1 <= n && n <= W);
    return (1 << (n - 1) << 1) - 1;
}

int main() {
    assert(W >= 32);
    assert(lower_one_mask(6) == 0x3f);
    assert(lower_one_mask(17) == 0x1ffff);
    assert(lower_one_mask(32) == 0xffffffff);
    return 0;
}

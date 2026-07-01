#include <assert.h>
#include <stddef.h>

const size_t W = sizeof(unsigned) << 3;

/*
 * Do rotating left shift.  Assume 0 <= n < w
 * Examples when x = 0x12345678 and w = 32:
 *    n=4 -> 0x23456781, n=20 -> 0x67812345
 */
unsigned rotate_left(unsigned x, int n) {
    assert(0 <= n && n < W);
    return (x << n) | (x >> (W - n));
}

int main() {
    assert(rotate_left(0x12345678, 4) == 0x23456781);
    assert(rotate_left(0x12345678, 20) == 0x67812345);
    return 0;
}

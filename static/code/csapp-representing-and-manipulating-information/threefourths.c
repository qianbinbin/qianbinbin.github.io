#include <assert.h>
#include <limits.h>
#include <stdint.h>

int threefourths(int x) {
    int high = x & ~3, low = x & 3;
    high = (high >> 1) + (high >> 2);
    low += low << 1;
    int bias = 0;
    (x & INT_MIN) && (bias = 3);
    return high + ((low + bias) >> 2);
}

int main() {
    assert(threefourths(INT_MIN) != INT_MIN * 3 / 4);
    assert(threefourths(INT_MIN) == (int) ((int64_t) INT_MIN * 3 / 4));
    assert(threefourths(INT_MAX) != INT_MAX * 3 / 4);
    assert(threefourths(INT_MAX) == (int) ((int64_t) INT_MAX * 3 / 4));
    return 0;
}

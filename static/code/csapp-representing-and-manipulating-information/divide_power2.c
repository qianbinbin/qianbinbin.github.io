#include <assert.h>
#include <limits.h>
#include <stddef.h>

const size_t W = sizeof(int) << 3;

/* Divide by power of 2. Assume 0 <= k < w-1 */
int divide_power2(int x, int k) {
    assert(0 <= k && k < W - 1);
    int bias = 0;
    (x & INT_MIN) && (bias = (1 << k) - 1);
    return (x + bias) >> k;
}

int main() {
    assert(divide_power2(INT_MIN, 4) == INT_MIN / 16);
    assert(divide_power2(INT_MAX, 6) == INT_MAX / 64);
    return 0;
}

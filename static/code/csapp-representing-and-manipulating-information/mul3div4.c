#include <assert.h>
#include <limits.h>
#include <stdint.h>

int mul3div4(int x) {
    x += (x << 1);
    int bias = 0;
    (x & INT_MIN) && (bias = 3);
    return (x + bias) >> 2;
}

int main() {
    assert(mul3div4(INT_MIN) == INT_MIN * 3 / 4);
    assert(mul3div4(INT_MIN) != (int) ((int64_t) INT_MIN * 3 / 4));
    assert(mul3div4(INT_MAX) == INT_MAX * 3 / 4);
    assert(mul3div4(INT_MAX) != (int) ((int64_t) INT_MAX * 3 / 4));
    return 0;
}

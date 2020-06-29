#include <assert.h>
#include <limits.h>
#include <stddef.h>

const size_t W = sizeof(int) << 3;

unsigned srl(unsigned x, int k) {
    assert(0 <= k && k < W);
    /* Perform shift arithmetically */
    unsigned xsra = (int) x >> k;
    unsigned mask = (1 << (W - k)) - 1;
    return xsra & mask;
}

int sra(int x, int k) {
    assert(0 <= k && k < W);
    /* Perform shift logically */
    int xsrl = (unsigned) x >> k;
    unsigned sign = ((unsigned) x & INT_MIN) >> (W - 1);
    unsigned mask = ~((sign << (W - k)) - 1);
    return xsrl | mask;
}

int main() {
    assert(srl(-1, 5) == (unsigned) -1 >> 5);
    assert(srl(INT_MIN, 5) == (unsigned) INT_MIN >> 5);
    assert(srl(1, 5) == (unsigned) 1 >> 5);
    assert(srl(INT_MAX, 5) == (unsigned) INT_MAX >> 5);

    assert(sra(-1, 5) == -1 >> 5);
    assert(sra(INT_MIN, 5) == INT_MIN >> 5);
    assert(sra(1, 5) == 1 >> 5);
    assert(sra(INT_MAX, 5) == INT_MAX >> 5);
    return 0;
}

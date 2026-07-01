#include <assert.h>
#include <limits.h>
#include <stddef.h>
#include <stdint.h>

const size_t W = sizeof(int) << 3;

int signed_high_prod(int x, int y) {
    return (int64_t) x * y >> W;
}

unsigned unsigned_high_prod(unsigned x, unsigned y) {
    int shp = signed_high_prod(x, y);
    return shp + x * (y >> (W - 1)) + y * (x >> (W - 1));
}

unsigned uhp(unsigned x, unsigned y) {
    return (uint64_t) x * y >> W;
}

int main() {
    assert(unsigned_high_prod(INT_MIN, INT_MIN) == uhp(INT_MIN, INT_MIN));
    assert(unsigned_high_prod(INT_MAX, INT_MAX) == uhp(INT_MAX, INT_MAX));
    assert(unsigned_high_prod(INT_MIN, INT_MAX) == uhp(INT_MIN, INT_MAX));
    return 0;
}

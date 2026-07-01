#include <assert.h>
#include <limits.h>

/* Determine whether arguments can be subtracted without overflow */
int tsub_ok(int x, int y) {
    int s = x - y;
    int of = !(x & INT_MIN) && (y & INT_MIN) && (s & INT_MIN);
    int uf = (x & INT_MIN) && !(y & INT_MIN) && !(s & INT_MIN);
    return !of && !uf;
}

int main() {
    assert(tsub_ok(0, INT_MAX));
    assert(!tsub_ok(0, INT_MIN));
    assert(tsub_ok(INT_MIN, 0));
    assert(!tsub_ok(INT_MIN, 1));
    assert(tsub_ok(INT_MAX, 0));
    assert(!tsub_ok(INT_MAX, -1));
    return 0;
}

#include <assert.h>
#include <limits.h>

/* Addition that saturates to TMin or TMax */
int saturating_add(int x, int y) {
    int s = x + y;
    int of = !(x & INT_MIN) && !(y & INT_MIN) && (s & INT_MIN);
    of && (s = INT_MAX);
    int uf = (x & INT_MIN) && (y & INT_MIN) && !(s & INT_MIN);
    uf && (s = INT_MIN);
    return s;
}

int main() {
    assert(saturating_add(INT_MAX - 1, 2) == INT_MAX);
    assert(saturating_add(1, 2) == 3);
    assert(saturating_add(INT_MIN, -1) == INT_MIN);
    assert(saturating_add(-1, -2) == -3);
    return 0;
}

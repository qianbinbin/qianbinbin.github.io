#include <assert.h>
#include <limits.h>
#include <stdint.h>

int tmult_ok(int x, int y) {
    return !x || x * y / x == y;
}

int tmult_ok1(int x, int y) {
    int64_t pll = (int64_t) x * y;
    return pll == (int) pll;
}

int main() {
    assert(tmult_ok(INT_MAX, 0));
    assert(tmult_ok(INT_MAX, 1));
    assert(tmult_ok(INT_MAX, -1));
    assert(!tmult_ok(INT_MAX, 2));
    assert(tmult_ok(INT_MIN, 0));
    assert(tmult_ok(INT_MIN, 1));
    assert(!tmult_ok(INT_MIN, -1));
    assert(!tmult_ok(INT_MIN, 2));

    assert(tmult_ok1(INT_MAX, 0));
    assert(tmult_ok1(INT_MAX, 1));
    assert(tmult_ok1(INT_MAX, -1));
    assert(!tmult_ok1(INT_MAX, 2));
    assert(tmult_ok1(INT_MIN, 0));
    assert(tmult_ok1(INT_MIN, 1));
    assert(!tmult_ok1(INT_MIN, -1));
    assert(!tmult_ok1(INT_MIN, 2));
    return 0;
}

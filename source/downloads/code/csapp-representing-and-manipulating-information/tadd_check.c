#include <assert.h>
#include <limits.h>

int tadd_ok(int x, int y) {
    int sum = x + y;
    return !(x >= 0 && y >= 0 && sum < 0) &&
        !(x < 0 && y < 0 && sum >= 0);
}

int main() {
    assert(tadd_ok(INT_MAX, 0));
    assert(!tadd_ok(INT_MAX, 1));
    assert(tadd_ok(INT_MIN, 0));
    assert(!tadd_ok(INT_MIN, -1));
    return 0;
}

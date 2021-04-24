#include <assert.h>
#include <limits.h>

int uadd_ok(unsigned x, unsigned y) {
    return x + y >= x;
}

int main() {
    assert(uadd_ok(UINT_MAX, 0));
    assert(!uadd_ok(UINT_MAX, 1));
    return 0;
}

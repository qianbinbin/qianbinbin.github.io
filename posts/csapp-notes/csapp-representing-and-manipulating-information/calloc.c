#include <assert.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

void *calloc(size_t nmemb, size_t size) {
    if (nmemb == 0 || size == 0)
        return NULL;
    size_t n = nmemb * size;
    if (n / nmemb != size)
        return NULL;
    void *p = malloc(n);
    if (p)
        memset(p, 0, n);
    return p;
}

int main() {
    int *p = (int *) calloc(100, sizeof(int));
    for (size_t i = 0; i <= 100; ++i)
        assert(p[i] == 0);
    free(p);
    assert(calloc(UINT64_MAX, UINT64_MAX) == NULL);
    return 0;
}

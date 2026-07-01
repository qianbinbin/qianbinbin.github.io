#include <assert.h>
#include <math.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

bool prime(char *data, uint32_t n) {
    if (n == 2)
        return true;
    if (!(n & 1))
        return false;
    return !(data[n >> 4] & (1 << (n >> 1 & 7)));
}

void sieve_of_eratosthenes(char *data) {
    data[0] = 1;
    uint32_t n, sq, start, c, step;
    for (n = 3, sq = (uint32_t) sqrt(UINT32_MAX); n <= sq; n += 2) {
        if (data[n >> 4] & (1 << (n >> 1 & 7)))
            continue;
        for (start = n * n, step = n << 1, c = start; c >= start; c += step)
            data[c >> 4] |= 1 << (c >> 1 & 7);
    }
}

int main() {
    clock_t start = clock();

    char *data = (char *) calloc(1 << 28, sizeof(char));
    sieve_of_eratosthenes(data);

    printf("Sieve of Eratosthenes cost %lf seconds.\n", (double) (clock() - start) / CLOCKS_PER_SEC);

    uint32_t count = 1;
    for (uint32_t n = 3; n <= 1000000000; n += 2) {
        if (!(data[n >> 4] & (1 << (n >> 1 & 7))))
            ++count;
    }
    assert(count == 50847534);

    start = clock();

    uint32_t even, n;
    bool verified;
    assert(prime(data, 2));
    for (even = 6; even; even += 2) {
        verified = false;
        for (n = 3; n <= even - 3; n += 2) {
            if (prime(data, n) && prime(data, even - n)) {
                // printf("%u = %u + %u\n", even, n, (uint32_t)(even - n));
                verified = true;
                break;
            }
        }
        if (!verified) {
            printf("Oops! Verification failed, even = %u.\n", even);
            break;
        }
    }
    free(data);
    assert(verified);
    printf("Same story.\n");

    printf("Verification cost %lf seconds.\n", (double) (clock() - start) / CLOCKS_PER_SEC);
}

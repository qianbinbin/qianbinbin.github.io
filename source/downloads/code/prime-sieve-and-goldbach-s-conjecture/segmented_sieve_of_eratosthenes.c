#include <assert.h>
#include <math.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// Note: the size must be power of 2.
#define SEGMENT_SIZE 0x200000

// 32 KiB L1 data cache = 2^15*2^3 = 2^18 bits, identifying 2^19 numbers.
// #define SEGMENT_SIZE 0x80000

// 256 KiB L2 cache
// #define SEGMENT_SIZE 0x400000

// 6 MiB L3 cache, use 2 MiB of it
// #define SEGMENT_SIZE 0x2000000

bool prime(char *data, uint32_t n) {
    if (n == 2)
        return true;
    if (!(n & 1))
        return false;
    return !(data[n >> 4] & (1 << (n >> 1 & 7)));
}

/*
 * Run sieve of Eratosthenes in [0, max(SEGMENT_SIZE, sqrt(UINT32_MAX))),
 * and store prime numbers in [3, sqrt(UINT32_MAX)].
 */
uint32_t initial_sieve(char *data, uint32_t *primes) {
    const uint32_t SQ = (uint32_t) sqrt(UINT32_MAX), LIMIT = SEGMENT_SIZE > SQ ? SEGMENT_SIZE : SQ;
    data[0] = 1;
    uint32_t n, sq, c, step, count = 0;
    for (n = 3, sq = (uint32_t) sqrt(LIMIT); n <= sq; n += 2) {
        if (data[n >> 4] & (1 << (n >> 1 & 7)))
            continue;
        for (c = n * n, step = n << 1; c <= LIMIT; c += step)
            data[c >> 4] |= 1 << (c >> 1 & 7);
    }
    for (n = 3; n <= SQ; n += 2) {
        if (!(data[n >> 4] & (1 << (n >> 1 & 7))))
            primes[count++] = n;
    }
    return count;
}

void segmented_sieve_of_eratosthenes(char *data) {
    // store prime numbers in [3, sqrt(UINT32_MAX)]
    uint32_t *primes = (uint32_t *) malloc(8192 * sizeof(uint32_t));
    const uint32_t count = initial_sieve(data, primes);

    uint32_t sq, i, p, c, step;
    const uint32_t SEGMENTS = (uint32_t)(1 << 31) / (SEGMENT_SIZE >> 1);
    // first segment has been sieved
    for (uint32_t s = 1, low = SEGMENT_SIZE, high = (SEGMENT_SIZE << 1) - 1;
         s < SEGMENTS; ++s, low += SEGMENT_SIZE, high += SEGMENT_SIZE) {
        sq = (uint32_t) sqrt(high);
        for (i = 0; i < count && (p = primes[i]) <= sq; ++i) {
            // c = p^2 + 2k * p
            c = low / p * p;
            if (c < low) c += p;
            if (!(c % 2)) c += p;
            // uint32t overflow in last segment
            for (step = p << 1; low <= c && c <= high; c += step)
                data[c >> 4] |= 1 << (c >> 1 & 7);
        }
    }
    free(primes);
}

int main() {
    clock_t start = clock();

    char *data = (char *) calloc(1 << 28, sizeof(char));
    segmented_sieve_of_eratosthenes(data);

    printf("Segmented sieve of Eratosthenes cost %lf seconds.\n", (double) (clock() - start) / CLOCKS_PER_SEC);

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

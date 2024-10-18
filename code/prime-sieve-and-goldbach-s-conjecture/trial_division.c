#include <assert.h>
#include <math.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <time.h>

bool prime(uint32_t number) {
    if (number < 6)
        return number == 2 || number == 3 || number == 5;
    if (number % 6 != 1 && number % 6 != 5)
        return false;

    for (uint32_t i = 5, sq = (uint32_t) sqrt(number); i <= sq; i += 6)
        if (!(number % i) || !(number % (i + 2)))
            return false;
    return true;
}

int main() {
    uint32_t count = 0;
    for (uint32_t n = 0; n <= 10000000; ++n) {
        if (prime(n))
            ++count;
    }
    assert(count == 664579);

    clock_t start = clock();

    uint32_t even, n;
    bool verified;
    assert(prime(2));
    for (even = 6; even <= 10000000; even += 2) {
        verified = false;
        for (n = 3; n <= even - 3; n += 2) {
            if (prime(n) && prime(even - n)) {
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
    assert(verified);
    printf("Same story.\n");

    printf("Verification cost %lf seconds.\n", (double) (clock() - start) / CLOCKS_PER_SEC);
}

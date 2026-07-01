#include <stdio.h>
#include <limits.h>
#include <stdlib.h>

int main() {
    double a, b, c;
    for (int i = 0; i < 10; ++i) {
        a = rand();
        b = rand();
        c = rand();
        printf("%lf, %lf, %lf, %d, %lf, %lf\n",
                a, b, c,
                a * b * c == a * (b * c),
                a * b * c,
                a * (b * c)
        );
    }
    return 0;
}

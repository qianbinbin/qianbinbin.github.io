#include <stddef.h>
#include <stdio.h>

const size_t W = sizeof(int) << 3;

int is_shifts_are_arithmetic() {
    return (-1 >> (W - 1) >> 1) & 1;
}

int main() {
    if (is_shifts_are_arithmetic())
        printf("arithmetic\n");
    else
        printf("logical\n");
    return 0;
}

#include <stdio.h>

int int_size_is_32() {
    int set_msb = 1 << 15 << 15 << 1;
    int beyond_msb = set_msb << 1;
    return set_msb && !beyond_msb;
}

int main() {
    if (int_size_is_32())
        printf("int size is 32.\n");
    else
        printf("int size is not 32.\n");
    return 0;
}

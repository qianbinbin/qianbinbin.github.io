#include <stdio.h>

int is_little_endian() {
    int i = 1;
    return *(char *) &i;
}

int main() {
    if (is_little_endian())
        printf("little endian\n");
    else
        printf("big endian\n");
    return 0;
}

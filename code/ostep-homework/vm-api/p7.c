#include <stdlib.h>

int main() {
    int *data = malloc(sizeof(int) * 100);
    free(data + 1);
    return 0;
}
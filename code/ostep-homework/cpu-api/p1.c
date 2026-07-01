#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {
    int x = 100;
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        printf("child, pid=%d, x=%d\n", getpid(), x);
        x = 10;
        printf("child, pid=%d, x=%d\n", getpid(), x);
    } else {
        printf("parent, pid=%d, x=%d\n", getpid(), x);
        x = 1;
        printf("parent, pid=%d, x=%d\n", getpid(), x);
    }
    return 0;
}
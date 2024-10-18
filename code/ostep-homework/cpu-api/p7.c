#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        close(STDOUT_FILENO);
        printf("child, pid=%d\n", getpid());
    } else {
        printf("parent, pid=%d\n", getpid());
    }
    return 0;
}
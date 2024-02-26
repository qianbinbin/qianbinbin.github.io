#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <unistd.h>

int main() {
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        int wc = wait(NULL);
        printf("child, pid=%d, wc=%d\n", getpid(), wc);
    } else {
        int wc = wait(NULL);
        printf("parent, pid=%d, wc=%d\n", getpid(), wc);
    }
    return 0;
}
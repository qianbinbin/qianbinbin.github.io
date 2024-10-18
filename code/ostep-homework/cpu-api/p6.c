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
        printf("child, pid=%d\n", getpid());
    } else {
        int wc = waitpid(-1, NULL, 0);
        printf("parent, pid=%d, wc=%d\n", getpid(), wc);
    }
    return 0;
}
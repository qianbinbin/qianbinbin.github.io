#include <sched.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>

int main() {
    int pfd1[2], pfd2[2];
    if (pipe(pfd1) != 0 || pipe(pfd2) != 0) {
        fprintf(stderr, "pipe failed\n");
        exit(1);
    }
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    }

    char buf1, buf2;
    int i = 0, n = 100000;
    if (rc == 0) {
        close(pfd1[1]);
        close(pfd2[0]);
        for (; i < n; ++i) {
            read(pfd1[0], &buf1, 1);
            write(pfd2[1], &buf2, 1);
        }
    } else {
        close(pfd1[0]);
        close(pfd2[1]);
        struct timeval start, end;
        gettimeofday(&start, NULL);
        for (; i < n; ++i) {
            write(pfd1[1], &buf1, 1);
            read(pfd2[0], &buf2, 1);
        }
        gettimeofday(&end, NULL);
        long us = (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec -
                  start.tv_usec;
        printf("%ld ns\n", us * 1000 / 2 / n);
    }
    return 0;
}
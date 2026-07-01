#include <fcntl.h>
#include <stdio.h>
#include <sys/time.h>
#include <unistd.h>

int main() {
    int fd = open("/dev/null", O_RDONLY);
    char buf;
    int i = 0, n = 1000000;
    struct timeval start, end;
    gettimeofday(&start, NULL);
    for (; i < n; ++i) {
        read(fd, &buf, 0);
    }
    gettimeofday(&end, NULL);
    long us =
            (end.tv_sec - start.tv_sec) * 1000000 + end.tv_usec - start.tv_usec;
    printf("%ld ns\n", us * 1000 / n);
    return 0;
}
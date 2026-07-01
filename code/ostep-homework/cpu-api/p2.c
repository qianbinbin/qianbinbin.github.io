#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>

int main() {
    int fd = open("./p2.output", O_CREAT | O_WRONLY | O_TRUNC, 0644);
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        write(fd, "child\n", 6);
    } else {
        write(fd, "parent\n", 7);
    }
    return 0;
}
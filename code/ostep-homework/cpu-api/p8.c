#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {
    int pipefd[2];
    if (pipe(pipefd) != 0) {
        fprintf(stderr, "pipe failed\n");
        exit(1);
    }

    int i, rc;
    for (i = 0; i < 2; ++i)
        if ((rc = fork()) == 0) break;

    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        printf("child, pid=%d, i=%d\n", getpid(), i);
        if (i == 0) {
            // writer
            dup2(pipefd[1], STDOUT_FILENO);
            close(pipefd[0]);
            close(pipefd[1]);
            printf("writer\n");
        } else {
            // reader
            dup2(pipefd[0], STDIN_FILENO);
            close(pipefd[0]);
            close(pipefd[1]);
            char buf;
            while (read(STDIN_FILENO, &buf, 1) > 0)
                write(STDOUT_FILENO, &buf, 1);
        }
    } else {
        printf("parent, pid=%d\n", getpid());
    }
    return 0;
}
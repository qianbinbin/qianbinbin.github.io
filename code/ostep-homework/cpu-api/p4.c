#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main() {
    int rc = fork();
    if (rc < 0) {
        fprintf(stderr, "fork failed\n");
        exit(1);
    } else if (rc == 0) {
        // printf("execl\n");
        // execl("/bin/ls", "/bin/ls", (char *) NULL);

        // printf("execlp\n");
        // execlp("ls", "ls", (char *) NULL);

        // printf("execv\n");
        // char *cvargs[2];
        // cvargs[0] = strdup("/bin/ls");
        // cvargs[1] = NULL;
        // execv(cvargs[0], cvargs);


        printf("execvp\n");
        char *cvpargs[2];
        cvpargs[0] = strdup("ls");
        cvpargs[1] = NULL;
        execvp(cvpargs[0], cvpargs);
    } else {
    }
    return 0;
}
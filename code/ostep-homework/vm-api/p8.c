#include <assert.h>
#include <stdlib.h>

#define VEC_INIT_SIZE 16

typedef struct {
    int size;
    int capacity;
    int *data;
} vector;

vector *vector_new() {
    vector *vec = malloc(sizeof(vector));
    vec->size = 0;
    vec->capacity = VEC_INIT_SIZE;
    vec->data = malloc(sizeof(int) * VEC_INIT_SIZE);
    assert(vec->data && "malloc failed");
    return vec;
}

void vector_add(vector *vec, int val) {
    if (vec->size >= vec->capacity) {
        vec->capacity <<= 1;
        assert(vec->capacity > 0 && "capacity overflow");
        vec->data = realloc(vec->data, sizeof(int) * vec->capacity);
        assert(vec->data && "realloc failed");
    }
    vec->data[vec->size++] = val;
}

void vector_free(vector *vec) {
    free(vec->data);
    free(vec);
}

int main() {
    vector *vec = vector_new();
    for (int i = 0; i < 32; ++i) {
        vector_add(vec, i);
    }
    vector_free(vec);
    return 0;
}
#include <gtest/gtest.h>

extern "C" {
#include "binary_tree_zigzag_level_order_traversal.h"
}

TEST(binary_tree_zigzag_level_order_traversal_test, zigzagLevelOrder_103_1) {
    int nums[] = {3, 9, 20, NTNODE, NTNODE, 15, 7};
    struct TreeNode *root = tree_create(nums, 7);
    int size = 0;
    int *columnSizes = NULL;
    int **ret = zigzagLevelOrder_103_1(root, &columnSizes, &size);
    for (int i = 0; i < size; ++i) {
        array_print(ret[i], columnSizes[i]);
        free(ret[i]);
    }
    free(ret);
    free(columnSizes);
    tree_free(root);
}

TEST(binary_tree_zigzag_level_order_traversal_test, zigzagLevelOrder_103_2) {
    int nums[] = {3, 9, 20, NTNODE, NTNODE, 15, 7};
    struct TreeNode *root = tree_create(nums, 7);
    int size = 0;
    int *columnSizes = NULL;
    int **ret = zigzagLevelOrder_103_2(root, &columnSizes, &size);
    for (int i = 0; i < size; ++i) {
        array_print(ret[i], columnSizes[i]);
        free(ret[i]);
    }
    free(ret);
    free(columnSizes);
    tree_free(root);
}

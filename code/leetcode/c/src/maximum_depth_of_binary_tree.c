#include "maximum_depth_of_binary_tree.h"

#include <stddef.h>

int maxDepth_104_1(struct TreeNode *root) {
    if (root == NULL) return 0;
    int left_depth = maxDepth_104_1(root->left);
    int right_depth = maxDepth_104_1(root->right);
    return (left_depth > right_depth ? left_depth : right_depth) + 1;
}

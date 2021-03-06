// Given a binary tree, return the level order traversal of its nodes' values.
// (ie, from left to right, level by level).
//
// For example:
// Given binary tree [3,9,20,null,null,15,7],
//
//     3
//    / \
//   9  20
//     /  \
//    15   7
//
// return its level order traversal as:
//
// [
//   [3],
//   [9,20],
//   [15,7]
// ]

#ifndef LEETCODECPP_BINARYTREELEVELORDERTRAVERSAL_H
#define LEETCODECPP_BINARYTREELEVELORDERTRAVERSAL_H

#include "TreeNode.h"
#include <vector>

namespace lcpp {

class Solution102_1 {
public:
  std::vector<std::vector<int>> levelOrder(TreeNode *root);
};

class Solution102_2 {
public:
  std::vector<std::vector<int>> levelOrder(TreeNode *root);
};

}

#endif //LEETCODECPP_BINARYTREELEVELORDERTRAVERSAL_H

// Given the head of a singly linked list where elements are sorted in ascending
// order, convert it to a height balanced BST.
//
// For this problem, a height-balanced binary tree is defined as a binary tree
// in which the depth of the two subtrees of every node never differ by more
// than 1.
//
//
//
// Example 1:
// https://assets.leetcode.com/uploads/2020/08/17/linked.jpg
//
// Input: head = [-10,-3,0,5,9]
// Output: [0,-3,9,-10,null,5]
// Explanation: One possible answer is [0,-3,9,-10,null,5], which represents the
// shown height balanced BST.
//
// Example 2:
//
// Input: head = []
// Output: []
//
//
// Constraints:
//
// The number of nodes in head is in the range [0, 2 * 10^4].
// -10^5 <= Node.val <= 10^5

#ifndef LEETCODECPP_CONVERTSORTEDLISTTOBINARYSEARCHTREE_H
#define LEETCODECPP_CONVERTSORTEDLISTTOBINARYSEARCHTREE_H

#include "ListNode.h"
#include "TreeNode.h"

namespace lcpp {

class Solution109_1 {
public:
  TreeNode *sortedListToBST(ListNode *head);
};

} // namespace lcpp

#endif // LEETCODECPP_CONVERTSORTEDLISTTOBINARYSEARCHTREE_H

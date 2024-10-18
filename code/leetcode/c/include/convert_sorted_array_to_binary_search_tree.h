/*
 * Given an integer array nums where the elements are sorted in ascending order, convert it to a height-balanced binary search tree.
 *
 * A height-balanced binary tree is a binary tree in which the depth of the two subtrees of every node never differs by more than one.
 *
 *
 *
 * Example 1:
 * https://assets.leetcode.com/uploads/2021/02/18/btree1.jpg
 *
 * Input: nums = [-10,-3,0,5,9]
 * Output: [0,-3,9,-10,null,5]
 * Explanation: [0,-10,5,null,-3,null,9] is also accepted:
 * https://assets.leetcode.com/uploads/2021/02/18/btree2.jpg
 *
 * Example 2:
 * https://assets.leetcode.com/uploads/2021/02/18/btree.jpg
 *
 * Input: nums = [1,3]
 * Output: [3,1]
 * Explanation: [1,3] and [3,1] are both a height-balanced BSTs.
 *
 *
 * Constraints:
 *
 * 1 <= nums.length <= 10^4
 * -10^4 <= nums[i] <= 10^4
 * nums is sorted in a strictly increasing order.
 */

#ifndef LEETCODE_CONVERT_SORTED_ARRAY_TO_BINARY_SEARCH_TREE_H
#define LEETCODE_CONVERT_SORTED_ARRAY_TO_BINARY_SEARCH_TREE_H

#include "common.h"

struct TreeNode *sortedArrayToBST_108_1(int *nums, int numsSize);

#endif //LEETCODE_CONVERT_SORTED_ARRAY_TO_BINARY_SEARCH_TREE_H

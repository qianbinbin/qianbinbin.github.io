"""
Given a binary tree, determine if it is height-balanced.

For this problem, a height-balanced binary tree is defined as:

a binary tree in which the left and right subtrees of every node differ in height by no more than 1.



Example 1:
https://assets.leetcode.com/uploads/2020/10/06/balance_1.jpg

Input: root = [3,9,20,null,null,15,7]
Output: true

Example 2:
https://assets.leetcode.com/uploads/2020/10/06/balance_2.jpg

Input: root = [1,2,2,3,3,null,null,4,4]
Output: false

Example 3:

Input: root = []
Output: true


Constraints:

The number of nodes in the tree is in the range [0, 5000].
-10^4 <= Node.val <= 10^4
"""
from typing import List, Optional

from .utils import TreeNode


class Solution1:
    def tree_height(self, root: Optional[TreeNode]) -> int:
        if root is None:
            return 0
        left = self.tree_height(root.left)
        if left == -1:
            return -1
        right = self.tree_height(root.right)
        if right == -1:
            return -1
        if abs(left - right) > 1:
            return -1
        return max(left, right) + 1

    def isBalanced(self, root: Optional[TreeNode]) -> bool:
        return self.tree_height(root) != -1
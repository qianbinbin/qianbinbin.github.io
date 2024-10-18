/*
 * You are given the head of a singly linked-list. The list can be represented as:
 *
 * L0 → L1 → … → Ln - 1 → Ln
 *
 * Reorder the list to be on the following form:
 *
 * L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
 *
 * You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 *
 *
 *
 * Example 1:
 * https://assets.leetcode.com/uploads/2021/03/04/reorder1linked-list.jpg
 *
 * Input: head = [1,2,3,4]
 * Output: [1,4,2,3]
 *
 * Example 2:
 * https://assets.leetcode.com/uploads/2021/03/09/reorder2-linked-list.jpg
 *
 * Input: head = [1,2,3,4,5]
 * Output: [1,5,2,4,3]
 *
 *
 * Constraints:
 *
 * The number of nodes in the list is in the range [1, 5 * 10^4].
 * 1 <= Node.val <= 1000
 */

#ifndef LEETCODE_REORDER_LIST_H
#define LEETCODE_REORDER_LIST_H

#include "common.h"

void reorderList_143_1(struct ListNode *head);

#endif //LEETCODE_REORDER_LIST_H

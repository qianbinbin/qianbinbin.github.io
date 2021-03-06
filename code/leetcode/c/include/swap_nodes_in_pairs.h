/*
 * Given a linked list, swap every two adjacent nodes and return its head.
 *
 *
 *
 * Example 1:
 *
 * https://assets.leetcode.com/uploads/2020/10/03/swap_ex1.jpg
 *
 * Input: head = [1,2,3,4]
 * Output: [2,1,4,3]
 *
 * Example 2:
 *
 * Input: head = []
 * Output: []
 *
 * Example 3:
 *
 * Input: head = [1]
 * Output: [1]
 *
 *
 * Constraints:
 *
 * The number of nodes in the list is in the range [0, 100].
 * 0 <= Node.val <= 100
 *
 *
 * Follow up: Can you solve the problem without modifying the values in the list's nodes? (i.e., Only nodes themselves may be changed.)
 */

#ifndef LEETCODE_SWAP_NODES_IN_PAIRS_H
#define LEETCODE_SWAP_NODES_IN_PAIRS_H

#include "common.h"

struct ListNode *swapPairs_24_1(struct ListNode *head);

#endif //LEETCODE_SWAP_NODES_IN_PAIRS_H

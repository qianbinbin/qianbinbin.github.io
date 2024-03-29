#include "swap_nodes_in_pairs.h"

#include <stdlib.h>

struct ListNode *swapPairs_24_1(struct ListNode *head) {
    struct ListNode dummy = {0, head};
    struct ListNode *tail = &dummy, *p1, *p2;
    while ((p1 = tail->next) != NULL && (p2 = p1->next) != NULL) {
        p1->next = p2->next;
        p2->next = p1;
        tail->next = p2;
        tail = p1;
    }
    return dummy.next;
}

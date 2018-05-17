---
title: 二叉树非递归遍历算法的快速实现
date: 2018-03-25 15:50:19
tags:
- Algorithm
- 算法
- Binary Tree
- 二叉树
---

二叉树的递归遍历简洁明了，而非递归遍历则相对复杂，三种递归思路有很大区别，还容易忘。如果不用线索二叉树的话，一般要用栈来实现，即便都是用栈实现，实现思路也有差别，这给我们理解和记忆带来困扰。

本文介绍利用栈来实现的二叉树非递归算法，主要目的是快速实现，而不是详细解释。

<!-- more -->

# 先序遍历

递归算法的伪代码：

```py
preorder(node)
  if (node = null)
    return
  visit(node)
  preorder(node.left)
  preorder(node.right)
```

首先我们知道，栈用来存储已经经过，但是还未到访问时机的结点。

先序遍历的顺序是：根 - 左 - 右，当处理一个结点时，当前结点可以直接访问，而左右子树还未到处理时机，需要将子树的根结点压入栈中：先压入右子结点，再压入左子结点。下次迭代再从栈中取出并处理，这样就保证了 根 - 左 - 右 的访问顺序。

伪代码：

```py
iterativePreorder(node)
  if (node = null)
    return
  s ← empty stack
  s.push(node)
  while (not s.isEmpty())
    node ← s.pop()
    visit(node)
    //right child is pushed first so that left is processed first
    if (node.right ≠ null)
      s.push(node.right)
    if (node.left ≠ null)
      s.push(node.left)
```

# 中序遍历

递归算法伪代码：

```py
inorder(node)
  if (node = null)
    return
  inorder(node.left)
  visit(node)
  inorder(node.right)
```

中序遍历的顺序为 左 - 根 - 右，在处理一个结点时，要先处理它的左子树，再访问本身，再处理右子树。那么就要先把自身压入栈中，然后处理左子树，即指针指向左子结点。

当指针为空，可以把指针指向的地方当成一棵树，并认为这棵树已经访问完毕，
- 如果这棵树是它父结点的左子树，接下来就应该访问父结点，即栈顶元素
- 如果这棵树是它父结点的右子树，说明以父结点为根的子树已经全部访问完毕，接下来就应该访问父结点向上首个还未访问的祖先结点，同样是栈顶元素

总之直接出栈访问即可。

当访问某个结点时，它的左子树必定已经访问完毕，接下来要处理的是右子树，就将指针指向右子结点。

伪代码：

```py
iterativeInorder(node)
  s ← empty stack
  while (not s.isEmpty() or node ≠ null)
    if (node ≠ null)
      s.push(node)
      node ← node.left
    else
      node ← s.pop()
      visit(node)
      node ← node.right
```

很多实现会做一些优化，用几个嵌套的`while`循环，其实不利于理解。

如果把先序遍历写成和中序遍历风格一致的话：

```py
iterativePreorder(node)
  if (node = null)
    return
  s ← empty stack
  while (not s.isEmpty() or node ≠ null)
    if (node ≠ null)
      visit(node)
      s.push(node)
      node ← node.left
    else
      node ← s.pop().right
```

不同的是这时栈中保存的就是已经访问过的结点了。

# 后序遍历

递归算法伪代码：

```py
postorder(node)
  if (node = null)
    return
  postorder(node.left)
  postorder(node.right)
  visit(node)
```

后序遍历的顺序为 左 - 右 - 根，在处理一个结点时，要先处理它的左右子树，再访问本身，那么就要先把自身压入栈中，然后把指针指向左子结点。

当指针为空，可以把指针指向的地方当成一棵树，并认为这棵树已经访问完毕，
- 如果这棵树是父结点的左子树，接下来就应该处理父结点的右子树，父结点即栈顶元素
- 如果这棵树是父结点的右子树，接下来就应该访问父结点，即栈顶元素

麻烦之处在于，当处理栈顶元素时，此结点的左子树已经处理完毕，我们并不知道它的右子树是否已经处理，这时可以用一个指针指向上一次访问的结点，根据后序遍历的性质，结点右子树最后一个访问的结点必定是这个右子树的根结点，也就是结点的右子结点。如果这个右子结点已经访问，那么说明左右子树全部处理完毕，接下来出栈并访问此结点即可，否则就不要出栈，而让指针指向右子结点。

伪代码：

```py
iterativePostorder(node)
  s ← empty stack
  lastNodeVisited ← null
  while (not s.isEmpty() or node ≠ null)
    if (node ≠ null)
      s.push(node)
      node ← node.left
    else
      peekNode ← s.peek()
      // if right child exists and traversing node
      // from left child, then move right
      if (peekNode.right ≠ null and lastNodeVisited ≠ peekNode.right)
        node ← peekNode.right
      else
        visit(peekNode)
        lastNodeVisited ← s.pop()
```

## 双栈法

如果使用两个栈，则后序遍历会简单很多。

栈`s1`存储等待处理的子树的根结点，`s2`存储等待访问的结点。

当处理一个子树时，将其根结点从`s1`中出栈，压入`s2`，此结点的左右子结点依次压入`s1`，这样就保证了先左后右的处理顺序。

`s2`总是先存储根结点，它的进栈顺序和遍历结果是相反的，出栈顺序则相同，因此可以得到后序遍历序列。

```py
iterativePostorder(node)
  s1 ← empty stack
  s1.push(node)
  s2 ← empty stack
  while (not s1.isEmpty())
    node ← s1.pop()
    if (node.left ≠ null)
      s1.push(node.left)
    if (node.right ≠ null)
      s1.push(node.right)
    s2.push(node)
  while (not s2.isEmpty())
    visit(s2.pop())
```

# 参考资料

[Tree traversal - Wikipedia](https://en.wikipedia.org/wiki/Tree_traversal)

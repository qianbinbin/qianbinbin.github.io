---
title: Java 集合框架
date: 2019-04-17 16:36:05
tags:
- Java
- 数据结构
- 算法
---

# Collection

![](/images/java-collections-framework/collection.png)

Collection 接口基本可分为三种，List、Set 和 Queue。这些接口有对应实现的抽象类，实体类只需要继承抽象类即可，免去不必要的重复编码。

为什么实体类继承了对应的抽象类，还要实现接口呢？例如：

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

事实上这就是个错误，Java 集合框架最初版的作者承认了：[java - Why does LinkedHashSet<E> extend HashSet<e> and implement Set<E> - Stack Overflow](https://stackoverflow.com/questions/2165204/why-does-linkedhashsete-extend-hashsete-and-implement-sete)

## List

- ArrayList

  数组实现。默认容量为 10，容量不够时，增加一半。

- LinkedList

  双向链表实现，同时实现了 Deque 接口。链式存储的 cache 命中率低，作为 List 时性能不如 ArrayList，除非需要经常在表头插入或删除。

- Vector

  废弃类。数组实现，加入了线程安全设计。默认容量为 10，容量不够时，默认翻倍。

  在不要求线程安全的情况下，性能远不如 ArrayList。

  在线程安全方面，它对每个单独的操作用 `synchronized` 修饰，但一般是要将一系列操作整体进行同步，没有必要对每个操作重复上锁。将 List 接口和同步强行结合在一起本身也是不好的设计。

  要将 List 接口和同步设计分离，可以用 `Collections.synchronizedList` 做一个包装，程序员可以自由选择什么时候进行同步：

  ```java
  List<Integer> list = new ArrayList<>();
  // ...
  List<Integer> syncedList = Collections.synchronizedList(list);
  // ...
  ```

- Stack

  废弃类。Stack 类是糟糕的设计，它继承自 Vector，加入了栈相关的方法（还用 `synchronized` 修饰）。这就意味着 Stack 可以随机存取，而栈是一种限制只能在栈顶存取的数据结构。可以用 ArrayDeque 代替。

## Set

- HashSet

  HashMap 的封装，实际上就是将插入的元素作为 key，全局相同的一个对象作为 value 保存在 HashMap 中。

- LinkedHashSet

  LinkedHashMap 的封装，元素可以按插入顺序来遍历。LinkedHashSet 继承自 HashSet，但其构造方法调用了 HashSet 的一个构造方法：

  ```java
  HashSet(int initialCapacity, float loadFactor, boolean dummy) {
      map = new LinkedHashMap<>(initialCapacity, loadFactor);
  }
  ```

  这个构造方法没有用 `public` 修饰，只提供给 LinkedHashSet 使用。因此 LinkedHashSet 最终使用的是 LinkedHashMap 来保存插入顺序。

- TreeSet

  TreeMap 的封装，与 HashSet 之于 HashMap 类似。保持插入的元素有序。

- EnumSet

  枚举类型 Set 的抽象类，提供一系列静态方法，返回继承它的实体类的实例。如果枚举数量不超过 64，则返回 RegularEnumSet 的实例，它维护一个 `long` 变量，每个比特标识一个枚举；如果枚举数量超过 64，则返回 JumboEnumSet 的实例，它使用 `long` 数组来标识。

## Queue

- LinkedList

  双向链表实现，实现了继承自 Queue 的 Deque 接口。作为 Queue/Deque 时性能不如 ArrayDeque。元素可以为 `null`。

- ArrayDeque

  数组实现，实现了 Deque 接口。比较尴尬的是没有继承 AbstractQueue。

  元素不能为 `null`，Deque 接口建议禁止元素为 `null`，因为队列为空时，`null` 可以作为一些获取元素的方法的返回值。尴尬的是，LinkedList 元素可以为 `null`，当获取到元素为 `null` 时，你就获得了一个薛定谔的队列，你不知道队列到底是不是为空，除非你用 `isEmpty` 或 `size` 等方法。

  数组首尾在逻辑上相连，用两个指针 `head`、`tail` 来标识队首和队尾，分别指向第一个元素的位置和最后一个元素后一个位置，指针超过范围时取模来实现循环。默认初始容量为 16，容量不够时扩大为两倍，因此容量必定是 2 的整数次幂，方便进行位运算。

  Deque 的设计在我看来也很糟糕。它有很多功能重复的方法，还有 `removeFirstOccurrence`、`removeLastOccurrence` 这种意义不明的方法。

- PriorityQueue

  优先队列，内部使用最小堆实现，最小的元素总是在队头。元素不能为 `null`。

  存放堆的数组默认初始容量为 11，容量不够时，如果容量较小则翻倍，如果较大则增加一半。元素的比较可以用自然排序（需要元素的类型实现 Comparable 接口），也可以在构造方法传入一个 Comparator 对象。

  与 C++、Python 不同，Java 标准库没有提供在数组上直接建堆的方法。

# Map

![](/images/java-collections-framework/map.png)

- HashMap

  使用拉链法解决哈希冲突，从 Java 8 开始，当链表过长时，转换为红黑树。key 和 value 可以为 `null`。

- LinkedHashMap

  继承自 HashMap，key 和 value 可以为 `null`。结点增加了 `before`、`after` 两个指针，用于建立双链表，从而维护了元素顺序。默认构造方法只维护插入顺序，也可以传入一个参数令其维护访问顺序，这可以用来实现 LRU cache。

- WeakHashMap

  使用拉链法解决哈希冲突。key 和 value 可以为 `null`。其结点实现继承自 WeakReference 类，仅对 key 进行弱引用。访问哈希表时，自动触发回收机制，对所有已经被回收的 key 的结点进行移除，对应 value 强引用置空。

- TreeMap

  红黑树实现。元素的比较可以用自然排序（需要元素类型实现 Comparable 接口），也可以在构造方法传入一个 Comparator 对象。value 可以为 `null`，如果需要 key 可为 `null`，则需要在构造方法中传入一个合适的 Comparator 对象，例如用 `Comparator.nullsFirst` 或 `Comparator.nullsLast` 进行包装。

- EnumMap

  key 的类型为枚举，直接使用数组保存。value 可为 `null`。

- Hashtable

  处于薛定谔的废弃状态。加入了同步设计，对方法用 `synchronized` 修饰。拉链法解决哈希冲突。key 和 value 不能为 `null`。

  不需要线程安全时，效率不如 HashMap，需要线程安全时，效率又不如 ConcurrentHashMap，因为 Hashtable 的 `synchronized` 方法是对整个对象上锁，ConcurrentHashMap 是对表进行分段上锁。

  其父类 Dictionary 已废弃，被 Map 代替。其子类 Properties 用于存取 String 类型的键值对，`System.getProperty` 方法就是一个应用。

# 工具类

- Arrays

  数组相关的工具类。

  其中有一个 `asList` 方法，返回一个内部类 ArrayList 的实例，这个类和 java.util.ArrayList 不是同一个，而且大小不可变，Java 入门常见坑。

- Collections

  集合相关的工具类。例如将集合包装成同步，将 Map 包装成 Set 等。

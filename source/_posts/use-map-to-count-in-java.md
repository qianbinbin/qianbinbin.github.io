---
title: 在 Java 中使用 Map 计数的几种姿势
date: 2018-06-02 19:09:03
tags:
- Java
- HashMap
- 哈希表
---

一个老生常谈的问题：在 Java 中，如何使用 Map 给对象计数，例如统计字符串出现的次数？

# 姿势一：containsKey()

```java
Map<String, Integer> map = new HashMap<>();
for (String word : words) {
    if (map.containsKey(word))
        map.put(word, map.get(word) + 1);
    else
        map.put(word, 1);
}
```

或者：

```java
int count = map.containsKey(word) ? map.get(word) : 0;
map.put(word, count + 1);
```

这是最容易想到的方法，然而这种方法至少有两个问题：

1. Integer 中的`value`声明为`final`，无法修改，每次更新均会产生一个新的 Integer 对象
2. 过于频繁且不必要的查表，具体来说，在`map`中不含有`word`的情况下会查两次表，分别是`containsKey()`、`put()`方法；在含有`word`的情况下会查三次表，分别为`containsKey()`、`get()`、`put()`方法

<!-- more -->

# 姿势二：get() / getOrDefault()

```java
Integer count = map.get(word);
if (count == null)
    map.put(word, 1);
else
    map.put(word, count + 1);
```

Java 8 则可以用`getOrDefault()`：

```java
map.put(word, map.getOrDefault(word, 0) + 1);
```

1. Integer 对象问题仍存在
2. 改善了查表问题，不管`map`中是否已经存在`word`，都只查两次表

# 姿势三：AtomicInteger / 自定义可变 int 封装类

```java
Map<String, AtomicInteger> map = new HashMap<>();
```

```java
AtomicInteger count = map.get(word);
if (count == null)
    map.put(word, new AtomicInteger(1));
else
    count.incrementAndGet();
```

利用 AtomicInteger，可以避免产生不必要的 Integer 对象。

也可以自己封装一个 MutableInt：

```java
class MutableInt {
    int value = 1;

    public void increment() {
        ++value;
    }

    public int get() {
        return value;
    }
}
```

AtomicInteger 是为线程安全设计的，可能有一定性能损失，但我实际测试发现，AtomicInteger 与自己封装类的性能表现几乎相同。

1. 解决了 Integer 对象问题
2. 改善了查表问题，每次只查两次表

# 姿势四：利用 put() 方法的返回值

这种方法有点 trick：Map 的`put()`方法返回的是之前对应键的值，如果不存在，则为`null`。

利用这一点就可以只查表一次即完成自增了：

```java
AtomicInteger count = new AtomicInteger(1);
AtomicInteger old = map.put(word, count);
if (old != null)
    count.set(old.get() + 1);
```

1. 每次仍产生一个冗余的 int 封装对象
2. 只查表一次，大大提高效率

# 姿势五：Java 8 中的 merge() / compute()

```java
map.merge(word, 1, (a, b) -> a + b);
```

或者：

```java
map.merge(word, 1, Integer::sum);
```

也可以用`compute()`方法实现：

```java
map.compute(word, (k, v) -> v == null ? 1 : v + 1);
```

虽然 Integer 对象问题仍存在，但只查一次表就实现了自增，Integer 自动拆箱装箱机制使代码非常简洁，如果使用 Java 8，毫无疑问代替姿势四。

当然 Integer 问题也是可以解决的，例如我们使用 AtomicInteger：

```java
map.compute(word, (k, v) -> {
    if (v == null)
        return new AtomicInteger(1);
    else {
        v.incrementAndGet();
        return v;
    }
});
```

只不过代码没那么简洁了，这样既解决了 Integer 对象问题，又减少了查表时间。

# 总结

以下是我个人测试得到的性能表现：

在字符串重复率较高的情况下，姿势三 > 姿势五 > 姿势二 > 姿势四 > 姿势一

在重复率较低的情况下，姿势五 > 姿势二 > 姿势四 > 姿势一 > 姿势三

基本符合预期。

- 姿势一表现一直都较差
- 姿势二中规中矩，很多人也确实就是这样写的
- 姿势三避免了创建冗余对象，在重复率较高的情况下表现非常好，甚至好于姿势五
- 姿势四则比较奇葩，看似做了优化，但产生的冗余对象拖了后腿，代码可读性也不如其它，不建议使用
- 姿势五不管重复率如何整体表现都很稳定，代码也很简洁，如果不是量很大，那么使用封装类相比 Integer 优势不明显

以上均不考虑线程安全。

# 参考资料

[optimization - Most efficient way to increment a Map value in Java - Stack Overflow](https://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java)
[java - Increment an Integer within a HashMap - Stack Overflow](https://stackoverflow.com/questions/4277388/increment-an-integer-within-a-hashmap/37296348)

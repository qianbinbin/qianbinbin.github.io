---
title: 享元模式 Flyweight Pattern
date: 2020-03-08 01:40:44
tags:
- Java
- 设计模式
- 算法
---

享元模式与其说是一种设计模式，不如说是一种算法思想。将可以共享的对象存储在一个表中以节省内存，而不是每次都重新创建。

例如，Java 中的 `Integer` 类型对 -128 到 127 的值做了缓存，`java.lang.Integer#valueOf(int)` 会直接返回缓存的对象。`String` 则可以保存在常量池中。这些都是典型应用。

使用享元模式的一个条件是，对象是可共享的，例如对于 `Integer`、`String`，只要值相同，即使指向相同的对象一般也没有问题。

但可共享不一定是不可变的，需要共享的只是对象的内部状态，外部状态可以是变化的。例如，设计一种“字符”类型，它的内容是内部状态，坐标是外部状态，相同内容的字符可以设置不同的坐标来共享使用。

<!-- more -->

# 实例

传统写法中把 `Flyweight` 设计为抽象，然后让可共享和不可共享的具体来实现它，显得怪异、冗余、令人费解，这里假设 `Flyweight` 就是可共享的，简单起见，不加入状态字段：

```java
public class Flyweight {
}
```

```java
public class FlyweightFactory {
    private static final WeakHashMap<String, Flyweight> sCache = new WeakHashMap<>();

    public static Flyweight get(String key) {
        return sCache.computeIfAbsent(key, s -> new Flyweight());
    }

    public static int size() {
        return sCache.size();
    }
}
```

这里用一个静态的 `WeakHashMap` 来做缓存以便自动回收。`HashMap` 有内存无法回收的风险。

## 测试

```java
// create a String object to avoid String pool
String key = new String("key");
WeakReference<Flyweight> ref = new WeakReference<>(FlyweightFactory.get(key));
assertNotNull(ref.get());
key = null;

assertTimeout(Duration.ofSeconds(1), () -> {
    // use WeakHashMap#size to call WeakHashMap#expungeStaleEntries
    while (FlyweightFactory.size() != 0 || ref.get() != null) {
        System.gc();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
});
```

用 `new String("key")` 而不是常量作为 `FlyweightFactory#get` 参数的原因是避免使用常量池，否则常量池持有对 `"key"` 的引用，造成 `WeakHashMap` 中对应的结点无法清除。

调用 `System#gc` 只是建议 JVM 进行垃圾回收，是否立即执行与 JVM 的实现有关，所以用循环来不断触发。

# 源码实现

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/flyweight>

# 参考资料

1. [Flyweight pattern - Wikipedia](https://en.wikipedia.org/wiki/Flyweight_pattern)

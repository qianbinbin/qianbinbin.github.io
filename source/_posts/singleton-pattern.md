---
title: 单例模式 Singleton Pattern
date: 2017-05-26 23:07:31
tags:
- Java
- 设计模式
---

许多时候整个系统只需要拥有一个的全局对象，这样有利于我们协调系统整体的行为。单例模式限制只有一个实例存在。

通常单例模式有两种构建方式：

- 懒汉方式，全局的单例实例在第一次被使用时创建，不试图获取这个实例就不会创建，从而实现了延迟加载

    - 优点：类加载快，可调用动态数据（例如 Android 中的 `Context` 对象）

    - 缺点：对象获取慢，多线程环境下需要考虑线程安全问题

- 饿汉方式，全局的单例实例在类装载时构建，在装载类时就初始化这个实例，而不是获取时才创建

    - 优点：对象获取快，天生线程安全

    - 缺点：类加载慢，无法调用动态数据

<!-- more -->

## 懒汉方式

### 适用于单线程的懒汉方式

```java
public class SimpleLazySingleton {
    private static SimpleLazySingleton sInstance;

    private SimpleLazySingleton() {
    }

    public static SimpleLazySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new SimpleLazySingleton();
        }
        return sInstance;
    }
}
```

在同一虚拟机的单线程应用场合中，`SimpleLazySingleton` 的构造方法私有化，其唯一实例只能通过静态方法 `getInstance` 来获取（不考虑反射机制）。

- 优点：实现简单，效率高

- 缺点：多线程环境下，可能有多个线程同时进入 `if` 代码块，从而多次创建实例

### 使用简单的锁机制保证线程安全

可使用 `synchronized` 关键字修饰 `getInstance` 方法，这种方式锁定的是类对象：

```java
public class SyncedSingleton {
    private static SyncedSingleton sInstance;

    private SyncedSingleton() {
    }

    public static synchronized SyncedSingleton getInstance() {
        if (sInstance == null) {
            sInstance = new SyncedSingleton();
        }
        return sInstance;
    }
}
```

保证了同时只有一个线程能进入 `getInstance` 代码块，从而保证线程安全。

- 优点：保证了线程安全

- 缺点：每次调用 `getInstance` 方法都会获取同步锁，影响效率

### 使用双重检查的锁机制保证线程安全

```java
public class DoubleCheckedLockingSingleton {
    private static volatile DoubleCheckedLockingSingleton sInstance;

    private DoubleCheckedLockingSingleton() {
    }

    public static DoubleCheckedLockingSingleton getInstance() {
        if (sInstance == null) {
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (sInstance == null) {
                    sInstance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return sInstance;
    }
}
```

这里的同步的是 `DoubleCheckedLockingSingleton` 类对象锁，也可以专门指定一个对象作为同步锁。

`getInstance` 方法有两个空指针的检查，因此称为双重检查。

- 在 `sInstance` 没有初始化时，假设有多个线程同时通过了第一重检查，只有第一个进入 `synchronized` 代码块的线程才能对 `sInstance` 进行初始化，其它线程均不会通过第二重检查

- 在 `sInstance` 初始化后，调用 `getInstance` 方法不会通过第一重检查，直接返回 `sInstance` 实例

注意到代码中使用 `volatile` 关键字修饰 `sInstance` 对象，这是因为

```java
sInstance = new DoubleCheckedLockingSingleton();
```

这一语句并非原子操作，事实上在 JVM 中它主要做了三件事：

1. 为 `sInstance` 分配内存

2. 调用 `DoubleCheckedLockingSingleton` 构造方法进行初始化

3. 将 `sInstance` 指针指向对象

如果不使用 `volatile` 修饰，JVM 的指令重排序优化会使 2 和 3 的顺序不固定。

在多线程环境下，当某个线程执行完 1、3，还未完成 2 时，`sInstance` 已经不为 `null`，其它线程调用 `getInstance` 方法就不会通过第一重检查，获取到的是尚未完全初始化的 `sInstance` 对象。

而 `volatile` 关键字的可见性禁止了指令重排序，使 `sInstance` 的实例化成为原子操作。需要注意的是，Java 5 之前的 `volatile` 并不能保证禁止指令重排序。

- 优点：与简单的锁机制相比，双重检查的锁机制只有在 `sInstance` 尚未初始化时，才会竞争类对象锁，效率更高

- 缺点：实现略显繁琐

### 使用静态嵌套类保证线程安全

```java
public class NestedHolderSingleton {
    private static class StaticSingletonHolder {
        private static final NestedHolderSingleton sInstance = new NestedHolderSingleton();
    }

    private NestedHolderSingleton() {
    }

    public static NestedHolderSingleton getInstance() {
        return StaticSingletonHolder.sInstance;
    }
}
```

这种方法利用 JVM 本身机制保证了线程安全。首次调用 `getInstance` 时，静态嵌套类 `StaticSingletonHolder` 才会加载，并初始化它的静态成员 `sInstance`，类在虚拟机中只会加载一次，因此是线程安全的。

- 优点：实现简单，比锁机制实现性能高

- 缺点：调用动态数据不够灵活

## 饿汉方式

### 简单的饿汉方式

```java
public class SimpleSingleton {
    private static final SimpleSingleton sInstance = new SimpleSingleton();

    private SimpleSingleton() {
    }

    public static SimpleSingleton getInstance() {
        return sInstance;
    }
}
```

在加载 `SimpleSingleton` 类时，`sInstance` 就会被初始化，天生就是线程安全的。

### 使用枚举实现饿汉方式

```java
public enum EnumSingleton {
    INSTANCE;

    private EnumSingleton() {
    }
}
```

利用 JVM 保证了仅有一个实例，保证了线程安全，而且实现简单，一般来说性能高于简单的饿汉方式。此外，枚举无法使用反射机制破解。虽然很多人，包括 *Effective Java* 也推荐这种方式，但利用枚举实现单例模式很少见。

## 总结

如何选择合适的方式实现单例模式？个人认为，先确定是否需要延迟加载（例如要用动态数据作为构造单例的参数），再看是否在多线程环境下，

- 如果不需要延迟加载，使用饿汉方式即可

- 如果需要延迟加载

    - 如果是单线程环境，使用简单的懒汉方式即可

    - 如果是多线程，

        - 如果对性能有要求，可使用双重检查的锁机制来实现

        - 否则简单的锁机制即可满足要求

## 实现源码

<https://github.com/qianbinbin/DesignPattern/tree/master/src/main/java/io/binac/designpattern/singleton>

## 参考资料

1. [Singleton pattern - Wikipedia](https://en.wikipedia.org/wiki/Singleton_pattern)
2. [如何正确地写出单例模式 | Jark's Blog](http://wuchong.me/blog/2014/08/28/how-to-correctly-write-singleton-pattern/)
3. [Java编程设计模式，第 1 部分: 单例模式](https://www.ibm.com/developerworks/cn/java/j-lo-Singleton/index.html)

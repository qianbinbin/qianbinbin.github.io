---
title: Singleton Pattern 单例模式
date: 2017-05-26 23:07:31
tags:
- Java
- 设计模式
---
许多时候整个系统只需要拥有一个的全局对象，这样有利于我们协调系统整体的行为。单例模式限制只有一个实例存在。

通常单例模式在 Java 语言中，有两种构建方式：

- 懒汉方式，指全局的单例实例在第一次被使用时构建。不试图获取这个实例，就不会创建，从而实现了延迟加载，因此称为懒汉方式
    - 优点：类加载快，可调用动态数据（例如 Android 中的 Context 对象）
    - 缺点：对象获取慢，多线程环境下需要考虑线程安全问题
- 饿汉方式，指全局的单例实例在类装载时构建。在装载类时就初始化这个实例，而不是获取时才创建，称为饿汉方式
    - 优点：对象获取快，天生线程安全
    - 缺点：类加载慢，无法调用动态数据

## 懒汉方式

### 适用于单线程的懒汉方式

```java
public class Singleton {
    private static Singleton INSTANCE;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }
}
```

在同一虚拟机的单线程应用场合中，Singleton 的构造方法私有化，其唯一实例只能通过静态方法`getInstance()`来获取（不考虑反射机制）。

- 优点：实现简单，效率高
- 缺点：多线程环境下，这种方式是线程不安全的，因为可能有多个线程同时进入`if`代码块，从而多次创建实例

### 使用简单的锁机制保证线程安全

可使用`synchronized`关键字修饰`getInstance()`方法，这种方式锁定的是类对象：

```java
public class Singleton {
    private static Singleton INSTANCE;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }
}
```

保证了同时只有一个线程能进入`getInstance()`代码块，从而保证线程安全。

也可以专门为单例指定一个对象作为同步锁：

```java
public class Singleton {
    private static final Object LOCK = new Object();
    private static Singleton INSTANCE;

    private Singleton() {
    }

    public static Singleton getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new Singleton();
            }
        }
        return INSTANCE;
    }
}
```

利用`LOCK`对象锁，将`INSTANCE`的空指针判断及其实例化过程变为原子操作，保证只实例化一次，从而保证线程安全。

- 优点：保证了线程安全
- 缺点：每次调用`getInstance()`方法都会获取同步锁，影响效率

### 使用双重检查的锁机制保证线程安全

```java
public class Singleton {
    private static volatile Singleton INSTANCE;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}
```

这里的同步的是 Singleton 类对象锁，也可以专门指定一个对象作为同步锁。

`getInstance()`方法有两个空指针的检查，因此称为双重检查。

- 在`INSTANCE`还没有初始化时，假设有多个线程同时通过了第一重检查，接下来的空指针判断和实例化过程是一个原子操作，只有第一个进入`synchronized`代码块的线程才能对`INSTANCE`进行初始化，其它线程均不会通过第二重检查

- 在`INSTANCE`初始化后，如果有线程再调用`getInstance()`方法，则不会通过第一重检查，直接返回`INSTANCE`实例

注意到代码中使用`volatile`关键字修饰`INSTANCE`对象，这是因为

```java
INSTANCE = new Singleton();
```

这一语句并非原子操作，事实上在 JVM 中它主要做了三件事：

1. 为`INSTANCE`分配内存
2. 调用 Singleton 构造方法进行初始化
3. 将`INSTANCE`指针指向对象

如果不使用`volatile`修饰，JVM 的指令重排序优化会使 2 和 3 的顺序不固定，可能变为 1、3、2，这在单线程中当然是没有问题的。但在多线程环境下，当某个线程执行完 1、3，还未完成 2 时，`INSTANCE`已经不为`null`，其它线程调用`getInstance()`方法就不会通过第一重检查，而使用返回的不完全初始化的`INSTANCE`对象就可能出现错误。

`volatile`关键字的可见性特性禁止了指令重排序，使`INSTANCE`的实例化成为原子操作。但必须在 Java 5.0 后使用，因为之前的`volatile`关键字并不能保证禁止指令重排序。

回过头再看简单的锁机制中，`INSTANCE`对象就不需要`volatile`修饰，因为`synchronized`就保证了可见性。

- 优点：与简单的锁机制相比，双重检查的锁机制只有在`INSTANCE`尚未初始化时，才会竞争类对象锁，而不需要每次都获取，效率更高
- 缺点：略显繁琐，实现起来需要谨慎

### 使用静态嵌套类保证线程安全

```java
public class Singleton {
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    private Singleton() {
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

这种方法利用 JVM 本身机制保证了线程安全。首次调用`getInstance()`时，静态嵌套类 Singleton.SingletonHolder 才会加载，并初始化它的静态成员`INSTANCE`，由于在虚拟机中只会加载一次，因此是线程安全的。

- 优点：实现简单，避免了同步锁带来的性能影响
- 缺点：调用动态数据不够灵活（可使用`static`变量、配置文件等）

## 饿汉方式

### 简单的饿汉方式

```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {
    }

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

在加载 Singleton 类时，`INSTANCE`就会被初始化，天生就是线程安全的。

### 使用枚举 Enum 实现饿汉方式

在 Java 5.0 之后，还可以使用枚举实现单例模式：

```java
public enum Singleton {
    INSTANCE;

    private Singleton() {
    }
}
```

利用 JVM 保证了仅有一个实例，因此天生也是线程安全的，而且实现简单，一般来说性能高于简单的饿汉方式。此外，Enum 无法使用反射机制破解。虽然很多人，包括 *Effective Java* 也推荐这种方式，但利用 Enum 实现单例模式很少见。

## 总结

如何选择合适的方式实现单例模式？个人认为，先确定是否需要延迟加载（例如要用动态数据作为构造单例的参数），再看是否在多线程环境下，

- 如果不需要延迟加载，使用两种饿汉方式即可
- 如果需要延迟加载
    - 如果是单线程环境，使用简单的懒汉方式即可
    - 如果是多线程，
        - 如果高并发，或获取单例方法调用频繁，可使用双重检查的锁机制来实现
        - 否则简单的锁机制即可满足要求
